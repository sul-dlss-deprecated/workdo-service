package edu.stanford.sulair.dlss.dor.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.stanford.sulair.dlss.dor.admin.Workflow;
import edu.stanford.sulair.dlss.dor.admin.Process;
import edu.stanford.sulair.dlss.dor.dao.ProcessDao;
import org.springframework.context.ApplicationContext;

//TODO: methods need to be revised if we ever store another type of workflow
public class WorkflowServiceImpl implements WorkflowService {
	private Logger logger = Logger.getLogger(WorkflowService.class);
	
	@Autowired
	ProcessDao processDao;
	@Autowired
    ApplicationContext appCtx;

	@Transactional(propagation = Propagation.NESTED )
	public void persist(Process p, String repoName) {
		persistOneProcess(repoName, p);
        MessagingService msgService = (MessagingService)appCtx.getBean(repoName + ".messaging");
		msgService.sendObjectUpdatedMessage(p.getDruid());
	}

	private void persistOneProcess(String repository, Process p) {
		Process pOld = processDao.findProcess(repository, p.getDruid(), p.getDatastream(), p.getName());
		if(pOld != null){
			p.setId(pOld.getId());
			p.setAttempts(pOld.getAttempts() + 1);
            p.setLifecycle(pOld.getLifecycle());
		} else{
			p.setAttempts(1);
		}
		processDao.persistProcess(p);
	}

	@Transactional(propagation = Propagation.NESTED )
	public void persist(Workflow gwf, String repoName) {
        RepositoryService repoService = (RepositoryService)appCtx.getBean(repoName);
        MessagingService msgService = (MessagingService)appCtx.getBean(repoName + ".messaging");
		List<Process> procs = gwf.getProcesses();
		Process p1 = gwf.getProcesses().get(0);
		//If any processes exist in the database
		//then delete all process rows, persist new rows, and send message
		//else persist all rows then create object in fedora
		//Delete all process rows
		List<Process> existingProcs = processDao.findProcessesByDruidAndDatastream(repoName, p1.getDruid(), p1.getDatastream());
		if(existingProcs.size() > 0){
			//TODO save off the number of attempts per pre-existing row
			logger.debug("Deleting all existing process rows by druid");
			processDao.deleteProcessesByRepoDruidAndWorkflowName(repoName, p1.getDruid(), p1.getDatastream());
			//Add each process row
			logger.debug("Persisting new processes");
			for(Process p: procs){
				processDao.persistProcess(p);
			}
			logger.debug("Sending object updated message");
			msgService.sendObjectUpdatedMessage(p1.getDruid());
		} else {
			logger.debug("Creating new process rows");
			for(Process p: procs){
				processDao.persistProcess(p);
			}
			//create in fedora
			logger.debug("Creating workflow datastream in fedora");
			repoService.createWorkflowDatastream(p1.getDruid(), p1.getDatastream());
		}
		
	}

	public Workflow findWorkflow(String repository, String druid, String datastreamName) {
		List<Process> procs = processDao.findProcessesByDruidAndDatastream(repository, druid, datastreamName);
		Workflow gwf = new Workflow();
		gwf.setProcesses(procs);
		gwf.setId(datastreamName);
        gwf.setObjectId(druid);
		return gwf;
	}

}
