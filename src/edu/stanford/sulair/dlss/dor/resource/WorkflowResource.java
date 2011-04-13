package edu.stanford.sulair.dlss.dor.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import edu.stanford.sulair.dlss.dor.admin.DateAdapter;
import edu.stanford.sulair.dlss.dor.dao.ProcessDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sun.jersey.spi.resource.PerRequest;

import edu.stanford.sulair.dlss.dor.admin.Workflow;
import edu.stanford.sulair.dlss.dor.admin.Process;
import edu.stanford.sulair.dlss.dor.service.WorkflowService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Path("/")
@PerRequest
@Component
@Scope("prototype")
public class WorkflowResource {
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    private Logger logger = Logger.getLogger(WorkflowResource.class);

	@Context
	UriInfo uriInfo;
	
	@Autowired
	WorkflowService wfService;

    @Autowired
    ProcessDao processDao;
	
	public WorkflowResource() {}
	
	
	@PUT
    @Path("{repo}/objects/{druid}/workflows/{workflow}")
	@Consumes("application/xml")
	public Response addUpdateWorkflow(@PathParam("repo") String repo, @PathParam("druid") String druid, @PathParam("workflow") String workflow, Workflow gwf) {
		try{
			gwf.initilizeProcesses(repo, druid, workflow);
			wfService.persist(gwf, repo);
		} catch(Exception e){
			return ResourceUtilities.createErrorResponse(e, logger);
		}
		return Response.noContent().build();
	}
	
	@PUT
	@Path("{repo}/objects/{druid}/workflows/{workflow}/{procName}")
	@Consumes("application/xml")
	public Response addUpdateProcessStep(@PathParam("repo") String repo, @PathParam("druid") String druid,
                                         @PathParam("workflow") String workflow, @PathParam("procName") String procName,
                                         Process p){
		if(!p.getName().equals(procName)){
			return Response.status(400).entity("Process name does not match URI").build();
		}
		try{
			p.setDruid(druid);
			p.setDatastream(workflow);
            p.setRepository(repo);
			wfService.persist(p, repo);
		} catch(Exception e){
			return ResourceUtilities.createErrorResponse(e, logger);
		}
		return Response.noContent().build();
	}

    @GET
    @Path("{repo}/objects/{druid}/workflows/{workflow}/{procName}")
	@javax.ws.rs.Produces("application/xml")
	public Response getWorkflowStep(
            @PathParam("repo") String repo,
            @PathParam("druid") String druid,
            @PathParam("workflow") String workflow,
            @PathParam("procName") String procName)
    {
		Process p = null;

		try{
			p = processDao.findProcess(repo, druid, workflow, procName);
		} catch(Exception e){
			return ResourceUtilities.createErrorResponse(e, logger);
		}

		if(p != null){
			return Response.status(200).entity(p).build();
		} else {
			return Response.status(Status.NOT_FOUND).entity("Workflow step not found").type(MediaType.TEXT_PLAIN_TYPE).build();
		}
	}
		
	@GET
    @Path("{repo}/objects/{druid}/workflows/{workflow}")
	@javax.ws.rs.Produces("application/xml")
	public Response getWorkflow(@PathParam("repo") String repo, @PathParam("druid") String druid, @PathParam("workflow") String workflow) {
		Workflow gwf = null;
		
		try{
			gwf = wfService.findWorkflow(repo, druid, workflow);
		} catch(Exception e){
			return ResourceUtilities.createErrorResponse(e, logger);
		}
		
		if(gwf != null){
			return Response.status(200).entity(gwf).build();
		} else {
            return Response.status(Status.NOT_FOUND).build();
		}
	}

    @GET
    @Path("{repo}/objects/{druid}/lifecycle")
	@javax.ws.rs.Produces("application/xml")
	public Response getLifecycle(@PathParam("repo") String repo, @PathParam("druid") String druid) {
		List<Process> procs = null;

		try{
			procs = processDao.findLifecycleCompletedProcesses(repo, druid);
		} catch(Exception e){
			return ResourceUtilities.createErrorResponse(e, logger);
		}

		if(procs != null && procs.size() > 0){
			return Response.ok(buildLifecycleXml(procs, druid), MediaType.APPLICATION_XML_TYPE).build();
		} else {
			return Response.status(Status.NOT_FOUND).entity("No lifecycle found").type(MediaType.TEXT_PLAIN_TYPE).build();
		}
	}

    private String buildLifecycleXml(List<Process> procs, String druid){
        StringBuilder xml = new StringBuilder("<lifecycle objectId=\"" + druid + "\">\n");
        for(Process p: procs){
            xml.append("  <milestone date=\"").append(DateAdapter.STANDARD_DATE_FORMAT.format(p.getDatetime())).append("\">").
                append(p.getLifecycle()).append("</milestone>\n");
        }
        xml.append("</lifecycle>\n");
        return xml.toString();
    }

}
