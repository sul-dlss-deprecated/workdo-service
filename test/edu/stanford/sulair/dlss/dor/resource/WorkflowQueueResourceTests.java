package edu.stanford.sulair.dlss.dor.resource;

import java.io.IOException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import edu.stanford.sulair.dlss.dor.AbstractResourceTest;

public class WorkflowQueueResourceTests extends AbstractResourceTest {

	public WorkflowQueueResourceTests() {
		super();
        this._springConfig = "edu/stanford/sulair/dlss/dor/resource/WorkflowQueueResourceTests-context.xml";
        
		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setIgnoreComments(true);
		XMLUnit.setIgnoreAttributeOrder(false);
	}
	
	@Test
	public void findWorkflowByWaiting() throws SAXException, IOException {

		final WebResource objResource = resource( "workflow_queue?repository=dor&workflow=GoogleScannedWF&waiting=descriptive-metadata");

		ClientResponse response = objResource.get(ClientResponse.class);
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		Assert.assertEquals(MediaType.APPLICATION_XML_TYPE, response.getType());
		String xml = response.getEntity(String.class);
		String expected = "<objects count=\"2\"><object id=\"dr:123\" url=\"https://dor-server/fedora/objects/dr:123\" /><object id=\"dr:abc\" url=\"https://dor-server/fedora/objects/dr:abc\" /></objects>";
		XMLAssert.assertXMLEqual(expected, xml);
	}

    @Test
	public void countWorkflowByWaiting() throws SAXException, IOException {

		final WebResource objResource = resource( "workflow_queue?repository=dor&workflow=GoogleScannedWF&waiting=descriptive-metadata&count-only=true");

		ClientResponse response = objResource.get(ClientResponse.class);
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		Assert.assertEquals(MediaType.APPLICATION_XML_TYPE, response.getType());
		String xml = response.getEntity(String.class);
		String expected = "<objects count=\"2\" />";
		XMLAssert.assertXMLEqual(expected, xml);
	}
	
	@Test
	public void noWorkflowFound() throws SAXException, IOException {

		final WebResource objResource = resource( "workflow_queue?repository=dor&workflow=GoogleScannedWF&waiting=no-objects-found&completed=download");

		ClientResponse response = objResource.get(ClientResponse.class);
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		Assert.assertEquals(MediaType.APPLICATION_XML_TYPE, response.getType());
		String body = response.getEntity(String.class);
		String expected = "<objects count=\"0\" />";
		XMLAssert.assertXMLEqual(expected, body);
	}

    @Test
	public void countReturnsNoObjects() throws SAXException, IOException {

		final WebResource objResource = resource( "workflow_queue?repository=dor&workflow=GoogleScannedWF&waiting=no-objects-found&completed=download&count-only=true");

		ClientResponse response = objResource.get(ClientResponse.class);
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		Assert.assertEquals(MediaType.APPLICATION_XML_TYPE, response.getType());
		String body = response.getEntity(String.class);
		String expected = "<objects count=\"0\" />";
		XMLAssert.assertXMLEqual(expected, body);
	}
	
	@Test
	public void queryCompletedProcesses() throws SAXException, IOException {

		final WebResource objResource = resource( "workflow_queue?repository=dor&workflow=GoogleScannedWF&completed=download");

		ClientResponse response = objResource.get(ClientResponse.class);
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		Assert.assertEquals(MediaType.APPLICATION_XML_TYPE, response.getType());
		String xml = response.getEntity(String.class);
		String expected = "<objects count=\"2\"><object id=\"dr:123\" url=\"https://dor-server/fedora/objects/dr:123\" /><object id=\"dr:abc\" url=\"https://dor-server/fedora/objects/dr:abc\" /></objects>";
		XMLAssert.assertXMLEqual(expected, xml);
	}

    @Test
	public void countCompletedProcesses() throws SAXException, IOException {

		final WebResource objResource = resource( "workflow_queue?repository=dor&workflow=GoogleScannedWF&completed=download&count-only=true");

		ClientResponse response = objResource.get(ClientResponse.class);
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		Assert.assertEquals(MediaType.APPLICATION_XML_TYPE, response.getType());
		String xml = response.getEntity(String.class);
		String expected = "<objects count=\"2\" />";
		XMLAssert.assertXMLEqual(expected, xml);
	}
	
	@Test
	public void queryErrorProcesses() throws SAXException, IOException {

		final WebResource objResource = resource( "workflow_queue?repository=dor&workflow=GoogleScannedWF&error=download");

		ClientResponse response = objResource.get(ClientResponse.class);
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		Assert.assertEquals(MediaType.APPLICATION_XML_TYPE, response.getType());
		String xml = response.getEntity(String.class);
		String expected = "<objects count=\"2\"><object id=\"dr:123\" url=\"https://dor-server/fedora/objects/dr:123\" errorMessage=\"NullPointer\" errorText=\"some trace\" /><object id=\"dr:abc\" url=\"https://dor-server/fedora/objects/dr:abc\" errorMessage=\"NullPointer\" /></objects>";
		XMLAssert.assertXMLEqual(expected, xml);
	}

    @Test
	public void countErrorProcesses() throws SAXException, IOException {

		final WebResource objResource = resource( "workflow_queue?repository=dor&workflow=GoogleScannedWF&error=download&count-only=true");

		ClientResponse response = objResource.get(ClientResponse.class);
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		Assert.assertEquals(MediaType.APPLICATION_XML_TYPE, response.getType());
		String xml = response.getEntity(String.class);
		String expected = "<objects count=\"2\" />";
		XMLAssert.assertXMLEqual(expected, xml);
	}
	
	@Test
	public void noErrorsFound() throws SAXException, IOException {

		final WebResource objResource = resource( "workflow_queue?repository=dor&workflow=noError&error=noError");

		ClientResponse response = objResource.get(ClientResponse.class);
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		Assert.assertEquals(MediaType.APPLICATION_XML_TYPE, response.getType());
		String body = response.getEntity(String.class);
		String expected = "<objects count=\"0\" />";
		XMLAssert.assertXMLEqual(expected, body);
	}

    	@Test
	public void queryQueuedProcesses() throws SAXException, IOException {

		final WebResource objResource = resource( "workflow_queue?repository=dor&workflow=GoogleScannedWF&queued=download");

		ClientResponse response = objResource.get(ClientResponse.class);
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		Assert.assertEquals(MediaType.APPLICATION_XML_TYPE, response.getType());
		String xml = response.getEntity(String.class);
		String expected = "<objects count=\"2\"><object id=\"dr:123\" url=\"https://dor-server/fedora/objects/dr:123\" /><object id=\"dr:abc\" url=\"https://dor-server/fedora/objects/dr:abc\" /></objects>";
		XMLAssert.assertXMLEqual(expected, xml);
	}

    @Test
	public void countQueuedProcesses() throws SAXException, IOException {

		final WebResource objResource = resource( "workflow_queue?repository=dor&workflow=GoogleScannedWF&queued=download&count-only=true");

		ClientResponse response = objResource.get(ClientResponse.class);
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		Assert.assertEquals(MediaType.APPLICATION_XML_TYPE, response.getType());
		String xml = response.getEntity(String.class);
		String expected = "<objects count=\"3\" />";
		XMLAssert.assertXMLEqual(expected, xml);
	}
	
	@Test
	public void query1Waiting2Completed() throws SAXException, IOException {

		final WebResource objResource = resource( "workflow_queue?repository=dor&workflow=GoogleScannedWF&waiting=cleanup&completed=ingest&completed=shelve");

		ClientResponse response = objResource.get(ClientResponse.class);
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		Assert.assertEquals(MediaType.APPLICATION_XML_TYPE, response.getType());
		String xml = response.getEntity(String.class);
		String expected = "<objects count=\"2\"><object id=\"dr:123\" url=\"https://dor-server/fedora/objects/dr:123\" /><object id=\"dr:abc\" url=\"https://dor-server/fedora/objects/dr:abc\" /></objects>";
		XMLAssert.assertXMLEqual(expected, xml);
	}

    @Test
	public void count1Waiting2Completed() throws SAXException, IOException {

		final WebResource objResource = resource( "workflow_queue?repository=dor&workflow=GoogleScannedWF&waiting=cleanup&completed=ingest&completed=shelve&count-only=true");

		ClientResponse response = objResource.get(ClientResponse.class);
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		Assert.assertEquals(MediaType.APPLICATION_XML_TYPE, response.getType());
		String xml = response.getEntity(String.class);
		String expected = "<objects count=\"2\" />";
		XMLAssert.assertXMLEqual(expected, xml);
	}

    @Test
	public void query1Waiting1Completed() throws SAXException, IOException {

		final WebResource objResource = resource( "workflow_queue?repository=dor&workflow=GoogleScannedWF&waiting=cleanup&completed=ingest");

		ClientResponse response = objResource.get(ClientResponse.class);
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		Assert.assertEquals(MediaType.APPLICATION_XML_TYPE, response.getType());
		String xml = response.getEntity(String.class);
		String expected = "<objects count=\"2\"><object id=\"dr:123\" url=\"https://dor-server/fedora/objects/dr:123\" /><object id=\"dr:abc\" url=\"https://dor-server/fedora/objects/dr:abc\" /></objects>";
		XMLAssert.assertXMLEqual(expected, xml);
	}

    @Test
	public void count1Waiting1Completed() throws SAXException, IOException {

		final WebResource objResource = resource( "workflow_queue?repository=dor&workflow=GoogleScannedWF&waiting=cleanup&completed=ingest&count-only=true");

		ClientResponse response = objResource.get(ClientResponse.class);
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		Assert.assertEquals(MediaType.APPLICATION_XML_TYPE, response.getType());
		String xml = response.getEntity(String.class);
		String expected = "<objects count=\"3\" />";
		XMLAssert.assertXMLEqual(expected, xml);
	}
}
