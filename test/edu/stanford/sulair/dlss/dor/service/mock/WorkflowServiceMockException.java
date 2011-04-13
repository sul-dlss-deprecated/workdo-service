package edu.stanford.sulair.dlss.dor.service.mock;

import edu.stanford.sulair.dlss.dor.DorRuntimeExeption;
import edu.stanford.sulair.dlss.dor.admin.Workflow;
import edu.stanford.sulair.dlss.dor.admin.Process;
import edu.stanford.sulair.dlss.dor.service.WorkflowService;

public class WorkflowServiceMockException implements WorkflowService {

	public void persist(Process p, String repoName) {
		throw new DorRuntimeExeption("persist exception");
	}

	public void persist(Workflow gwf, String repoName) {
		throw new DorRuntimeExeption("persist exception");
	}

	public Workflow findWorkflow(String repository, String druid, String datastreamName) {
		throw new DorRuntimeExeption("persist exception");
	}

}
