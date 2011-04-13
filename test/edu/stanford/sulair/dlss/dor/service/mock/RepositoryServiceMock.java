package edu.stanford.sulair.dlss.dor.service.mock;

import edu.stanford.sulair.dlss.dor.service.RepositoryService;

public class RepositoryServiceMock implements RepositoryService {
	
	private boolean createWorkflowDatastreamUsed = false;

	public void createWorkflowDatastream(String druid, String workflowName) {
		createWorkflowDatastreamUsed = true;

	}

    public boolean wasCreateWorkflowDatastreamUsed(){
		return createWorkflowDatastreamUsed;
	}
	
	public void setUp(){
		createWorkflowDatastreamUsed = false;
	}

}
