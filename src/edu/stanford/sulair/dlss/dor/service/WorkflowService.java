package edu.stanford.sulair.dlss.dor.service;

import edu.stanford.sulair.dlss.dor.admin.Workflow;
import edu.stanford.sulair.dlss.dor.admin.Process;

public interface WorkflowService {

	void persist(Process p, String repoName);

	void persist(Workflow gwf, String repoName);
	
	Workflow findWorkflow(String repository, String druid, String datastreamName);

}
