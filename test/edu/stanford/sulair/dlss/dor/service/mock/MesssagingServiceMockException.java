package edu.stanford.sulair.dlss.dor.service.mock;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.stanford.sulair.dlss.dor.DorRuntimeExeption;
import edu.stanford.sulair.dlss.dor.service.MessagingService;

public class MesssagingServiceMockException implements MessagingService {

	//@Transactional(propagation = Propagation.NESTED )
	public void sendObjectUpdatedMessage(String druid) {
		throw new DorRuntimeExeption("messaging exception");

	}

}
