package edu.stanford.sulair.dlss.dor.service.mock;

import edu.stanford.sulair.dlss.dor.service.MessagingService;


public class MesssagingServiceMock implements MessagingService {

	private boolean serviceUsed = false;
	//@Transactional(propagation = Propagation.NESTED )
	public void sendObjectUpdatedMessage(String druid) {
		serviceUsed = true;

	}
	
	public boolean wasServiceUsed(){
		return serviceUsed;
	}
	
	public void setUp(){
		serviceUsed = false;
	}

}
