package edu.stanford.sulair.dlss.dor.service.mock;

import java.util.Calendar;
import java.util.List;

import org.junit.Assert;

import edu.stanford.sulair.dlss.dor.admin.Workflow;
import edu.stanford.sulair.dlss.dor.admin.Process;
import edu.stanford.sulair.dlss.dor.service.WorkflowService;

public class WorkflowServiceMock implements WorkflowService {

	
	public void persist(Process p, String repoName) {
		checkProcess(p, repoName);
	}

	private void checkProcess(Process p, String repo) {
		Assert.assertNotNull(p.getDatastream());
		Assert.assertTrue(!p.getDatastream().equals(""));
		Assert.assertNotNull(p.getDruid());
		Assert.assertTrue(!p.getDruid().equals(""));
        Assert.assertNotNull(p.getRepository());
        Assert.assertEquals(repo, p.getRepository());
	}

	public void persist(Workflow gwf, String repoName) {
        Assert.assertNotNull(repoName);
        Assert.assertNotNull(gwf.getObjectId());
        Assert.assertNotSame("", gwf.getObjectId());
		List<Process> procs = gwf.getProcesses();
		for(Process p: procs){
			checkProcess(p, repoName);
		}
		
	}

	public Workflow findWorkflow(String repository, String druid, String datastreamName) {
		Workflow gwf = new Workflow();
		Process p = new Process();
		p.setName("convert");
		p.setStatus("waiting");
		Calendar c = Calendar.getInstance();
		c.set(2008, 10, 15, 13, 30, 0);
		p.setDatetime(c.getTime());
		
		gwf.addProcess(p);
        gwf.setObjectId(druid);
		
		return gwf;
	}
	
}
