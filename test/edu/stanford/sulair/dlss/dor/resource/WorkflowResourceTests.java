package edu.stanford.sulair.dlss.dor.resource;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.Test;
import edu.stanford.sulair.dlss.dor.AbstractResourceTest;
import org.xml.sax.SAXException;


import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * User: wmene
 * Date: Apr 27, 2010
 * Time: 2:43:38 PM
 */
public class WorkflowResourceTests extends AbstractResourceTest {
    public WorkflowResourceTests() {
        super();
        this._springConfig = "edu/stanford/sulair/dlss/dor/resource/WorkflowResourceTests-context.xml";

        XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setIgnoreComments(true);
		XMLUnit.setIgnoreAttributeOrder(false);
    }

    @Test
    public void testPutWorkflow() {

        final WebResource objResource = resource( "dor/objects/pid:123/workflows/GoogleScannedWF");

        ClientResponse r = objResource.entity("<workflow id=\"GoogleScannedWF\" objectId=\"pid:123\"><process name=\"convert\" status=\"waiting\" datetime=\"2008.11.15 13:30:00 PST\"/></workflow>",
                    "application/xml").put(ClientResponse.class);
            Assert.assertEquals(204, r.getStatus());

    }

    @Test
    public void testPutWorkflowProcessStep() {

        final WebResource objResource = resource( "dor/objects/pid:123/workflows/GoogleScannedWF/convert");

        ClientResponse r = objResource.entity("<process name=\"convert\" status=\"waiting\" datetime=\"2008.11.15 13:30:00 PST\"/>",
                    "application/xml").put(ClientResponse.class);
            Assert.assertEquals(204, r.getStatus());

    }

    @Test
    public void testGetWorkflowProcessStep() throws Exception {

        final WebResource objResource = resource( "dor/objects/pid:123/workflows/googleScannedWF/convert");

        ClientResponse response = objResource.get(ClientResponse.class);
		Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		Assert.assertEquals(MediaType.APPLICATION_XML_TYPE, response.getType());
		String xml = response.getEntity(String.class);
        System.out.println(xml);
        XMLAssert.assertXpathEvaluatesTo("convert", "//process/@name", xml);
		XMLAssert.assertXpathEvaluatesTo("waiting", "//process/@status", xml);
    }

    @Test
    public void testGetWorkflowProcessStepNotFound() {

        final WebResource objResource = resource( "dor/objects/pid:123/workflows/GoogleScannedWF/ingest");

        ClientResponse response = objResource.get(ClientResponse.class);
		Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
		Assert.assertEquals(MediaType.TEXT_PLAIN_TYPE, response.getType());
		String body = response.getEntity(String.class);
		Assert.assertEquals("Workflow step not found", body);

    }

    @Test
	public void testGetLifecycle() throws SAXException, IOException {

		final WebResource objResource = resource("dor/objects/dr:123/lifecycle");

		ClientResponse response = objResource.get(ClientResponse.class);
		Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		Assert.assertEquals(MediaType.APPLICATION_XML_TYPE, response.getType());
		String xml = response.getEntity(String.class);
		String expected = "<lifecycle objectId=\"dr:123\">" +
                          "  <milestone date=\"2010-11-15T13:30:00-0800\">registered</milestone>" +
                          "  <milestone date=\"2010-11-16T13:30:00-0800\">inprocess</milestone>" +
                          "  <milestone date=\"2010-11-17T13:30:00-0800\">released</milestone>" +
                          "</lifecycle>";
		XMLAssert.assertXMLEqual(expected, xml);
	}

    @Test
	public void testNoLifecycleFound() throws SAXException, IOException {

		final WebResource objResource = resource( "dor/objects/dr:nolifecycle123/lifecycle");

		ClientResponse response = objResource.get(ClientResponse.class);
		Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
		Assert.assertEquals(MediaType.TEXT_PLAIN_TYPE, response.getType());
		String body = response.getEntity(String.class);
		Assert.assertEquals("No lifecycle found", body);
	}
}
