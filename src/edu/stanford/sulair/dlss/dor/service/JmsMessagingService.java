package edu.stanford.sulair.dlss.dor.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import edu.stanford.sulair.dlss.dor.DorRuntimeExeption;
import org.springframework.context.ApplicationContext;

public class JmsMessagingService implements MessagingService {

	private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Autowired
    ApplicationContext appCtx;
		
	private String topicName;
	private String fedoraUrl;
	
	private Connection connection;
	private Session session;
	private Topic topic;
	private MessageProducer publisher;
	
	private static Random generator;

    private String repository;

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public String getFedoraUrl() {
		return fedoraUrl;
	}

	public void setFedoraUrl(String fedoraUrl) {
		this.fedoraUrl = fedoraUrl;
	}

    public void setRepository(String repo){
        this.repository = repo;
    }
	
	public Random getGenerator(){
		if(generator == null){
			generator = new Random(System.currentTimeMillis());
		}
		return generator;
	}

	public void sendObjectUpdatedMessage(String druid) {
		try {
            ActiveMQConnectionFactory factory = (ActiveMQConnectionFactory)appCtx.getBean(repository + ".broker");
			connection = factory.createConnection();
	        connection.setClientID(Integer.toString(getGenerator().nextInt()));
	        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	        topic = session.createTopic(topicName);
	
	        publisher = session.createProducer(topic);
	        publisher.setDeliveryMode(DeliveryMode.PERSISTENT);
	
	        Date now = new Date();
	        String stamp = df.format(now);
	     
	        StringBuilder buf = new StringBuilder();
	        buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	        buf.append("<entry xmlns=\"http://www.w3.org/2005/Atom\"");
	        buf.append("		xmlns:fedora-types=\"http://www.fedora.info/definitions/1/0/types/\"");
	        buf.append("		xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">");
	        buf.append("	<id>urn:uuid:").append(UUID.randomUUID()).append("</id>");
	        buf.append("	<updated>").append(stamp).append("</updated>");
	        buf.append("	<author>");
	        buf.append("		<name>fedoraAdmin</name>");
	        buf.append("		<uri>").append(fedoraUrl).append("</uri>");
	        buf.append("	</author>");
	        buf.append("	<title type=\"text\">modifyObject</title>");
	        buf.append("	<summary type=\"text\">").append(druid).append("</summary>");
	        buf.append("	<content type=\"text\">").append(stamp).append("</content>");
	        buf.append("</entry>");
	
	        publisher.send(session.createTextMessage(buf.toString()));
	
	        connection.close();
		} catch(Exception e){
			throw new DorRuntimeExeption("Unable to send JMS update message for druid: " + druid, e);
		}

	}

}
