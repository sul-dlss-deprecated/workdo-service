package edu.stanford.sulair.dlss.dor.resource;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sun.jersey.spi.resource.PerRequest;

import edu.stanford.sulair.dlss.dor.admin.Process;
import edu.stanford.sulair.dlss.dor.dao.ProcessDao;

@Path("workflow_queue")
@PerRequest
@Component
@Scope( "prototype" )
public class WorkflowQueueResource {
    private Logger logger = Logger.getLogger(WorkflowResource.class);

	@Autowired
	ProcessDao processDao;

    Map<String, String> repositoryMap;
	
	@Context
	UriInfo uriInfo;
	
	public WorkflowQueueResource(){}

    public void setRepositoryMap(Map<String, String> repositoryMap) {
        this.repositoryMap = repositoryMap;
    }
	
	@GET
	public Response getWorkflowQueue(
            @QueryParam("repository") String repository,
            @DefaultValue("") @QueryParam("workflow") String workflow,
            @DefaultValue("") @QueryParam("waiting") String waitingProcess,
            @QueryParam("completed") List<String> completedProcesses,
            @DefaultValue("") @QueryParam("error") String errorProcess,
            @DefaultValue("") @QueryParam("queued") String queuedProcess,
            @DefaultValue("false") @QueryParam("count-only") boolean countOnly) {
        logger.debug("Starting WorkflowQueueResource.getWorkflowQueue()");
		List<String> druids = null;
		try {
            if(countOnly)
                return processCountOnlyQueries(repository, workflow, waitingProcess, completedProcesses, errorProcess, queuedProcess);
			if(!errorProcess.trim().equals("")) {
				List<Process> errorProcesses = processDao.findErrorProcessesByDatastreamAndName(repository, workflow, errorProcess);
				if(errorProcesses != null && errorProcesses.size() != 0)
					return createErrorProcessesResponse(errorProcesses, repository);
				//otherwise no errors found.  druids is null, so we will return a 404 below
            }else if(!queuedProcess.trim().equals("")){
                druids = processDao.findQueuedDruids(repository, workflow, queuedProcess);
			}else if(completedProcesses != null && completedProcesses.size() == 2) {
                logger.debug("Calling processDao.findWaitingDruidsWithTwoCompletedProcesses");
				druids = processDao.findWaitingDruidsWithTwoCompletedProcesses(repository, workflow, waitingProcess,
																				completedProcesses.get(0), completedProcesses.get(1));
			}else if(completedProcesses == null) {
				druids = processDao.findWaitingDruids(repository, workflow, waitingProcess);
			} else if(waitingProcess.equals("")){
				druids = processDao.findCompletedDruids(repository, workflow, completedProcesses.get(0));
			}else {
                logger.debug("Calling processDao.findWaitingDruidsByDatastreamNameAndCompleted");
				druids = processDao.findWaitingDruidsByDatastreamNameAndCompleted(repository, workflow, waitingProcess, completedProcesses.get(0));
			} 
		}catch(Exception e){
			return ResourceUtilities.createErrorResponse(e, logger);
		}
		
		if(druids == null){
			druids = new ArrayList<String>();
		}
        logger.debug("Creating response XML");
		String xml = createWorkflowResponseXml(druids, repository);
		return Response.ok(xml, MediaType.APPLICATION_XML_TYPE).build();
	}

    private Response processCountOnlyQueries(String repository, String workflow, String waitingProcess, List<String> completedProcesses, String errorProcess, String queuedProcess) {
        int count = 0;
        try {
            if(!errorProcess.trim().equals("")) {
				count = processDao.countErrorProcessesByDatastreamAndName(repository, workflow, errorProcess);
            }else if(!queuedProcess.trim().equals("")) {
                count = processDao.countQueuedDruids(repository, workflow, queuedProcess);
			}else if(completedProcesses != null && completedProcesses.size() == 2) {
				count = processDao.countWaitingDruidsWithTwoCompletedProcesses(repository, workflow, waitingProcess,
																				completedProcesses.get(0), completedProcesses.get(1));
			}else if(completedProcesses == null) {
				count = processDao.countWaitingDruids(repository, workflow, waitingProcess);
			} else if(waitingProcess.equals("")){
				count = processDao.countCompletedDruids(repository, workflow, completedProcesses.get(0));
			}else {
				count = processDao.countWaitingDruidsByDatastreamNameAndCompleted(repository, workflow, waitingProcess, completedProcesses.get(0));
			}

        } catch(Exception e) {
           return ResourceUtilities.createErrorResponse(e, logger);
        }
        
        StringBuilder buf = new StringBuilder("<objects count=\"");
        buf.append(count).append("\" />");
        String xml = buf.toString();

        return Response.ok(xml, MediaType.APPLICATION_XML_TYPE).build();
    }

    private String createWorkflowResponseXml(List<String> druids, String repository) {
		StringBuilder buf = new StringBuilder("<objects ");
        buf.append("count=\"").append(druids.size()).append("\" >");
		for(String druid: druids) {
			buf.append("<object id=\"").append(druid).append("\" url=\"").append(repositoryMap.get(repository)).append("/fedora/objects/").
					append(druid).append("\" />");
		}
		buf.append("</objects>");
		return buf.toString();
	}
	
	private Response createErrorProcessesResponse(List<Process> errorProcesses, String repository){
		StringBuilder buf = new StringBuilder("<objects ");
        buf.append("count=\"").append(errorProcesses.size()).append("\" >");
		for(Process proc: errorProcesses) {
			buf.append("<object id=\"").append(proc.getDruid()).append("\" url=\"").append(repositoryMap.get(repository)).append("/fedora/objects/").
					append(proc.getDruid()).append("\" errorMessage=\"").append(proc.getErrorMessage());
			if(proc.getErrorText() != null && !proc.getErrorText().trim().equals("")){
				buf.append("\" errorText=\"").append(proc.getErrorText());
			}
			buf.append("\" />");
		}
		buf.append("</objects>");
		return Response.ok(buf.toString(), MediaType.APPLICATION_XML_TYPE).build();
	}
}
