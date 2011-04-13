package edu.stanford.sulair.dlss.dor.admin;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import edu.stanford.sulair.dlss.dor.AbstractXmlMarhallingTest;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Test;


public class ProcessTests extends AbstractXmlMarhallingTest {
	
	@Test
	public void dumpProcessXml() throws Exception {
		JAXBContext jaxbContext = JAXBContext
				.newInstance(edu.stanford.sulair.dlss.dor.admin.Process.class);
		Process p = new Process();
		p.setName("download");
		p.setStatus("waiting");
		Calendar c = Calendar.getInstance();
		c.set(2008, 10, 15, 13, 30, 0);
		p.setDatetime(c.getTime());
		p.setDatastream("GoogleWorkflow");
		p.setDruid("dr:123");
		p.setId(5);
		p.setErrorMessage("NullPointerException");
		p.setErrorText("stacktrace");
		p.setLifecycle("in-process");
		p.setElapsed(1.173);
        p.setRepository("dor");
				
		String xml = marshall(jaxbContext, p);
		System.out.println(xml);
		String expected = "<process name=\"download\" status=\"waiting\" datetime=\"2008-11-15T13:30:00-0800\" attempts=\"0\" errorMessage=\"NullPointerException\" " +
							"errorText=\"stacktrace\" lifecycle=\"in-process\" elapsed=\"1.173\" />";
		XMLAssert.assertXMLEqual(expected, xml);
		Diff diff = new Diff(xml, expected);
		XMLAssert.assertXMLIdentical(diff, true);
		
	}
	
	@Test
	public void unMarshallProcessXml() throws JAXBException, Exception {
		String xml = "<process name=\"download\" status=\"waiting\" datetime=\"2008-11-15T13:30:00-0800\" errorMessage=\"NullPointerException\" errorText=\"stacktrace\" lifecycle=\"ingested\" elapsed=\"1.173\"/>";

		JAXBContext jaxbContext = JAXBContext
				.newInstance(edu.stanford.sulair.dlss.dor.admin.Process.class);
		Unmarshaller um = jaxbContext.createUnmarshaller();		
		ByteArrayInputStream bis = new ByteArrayInputStream(xml.getBytes());
		Process p = (Process) um.unmarshal(bis);
		assertEquals("download", p.getName());
		assertEquals("waiting", p.getStatus());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		Date d = sdf.parse("2008-11-15T13:30:00-0800");
		assertEquals(0, d.compareTo(p.getDatetime()));
		assertEquals("NullPointerException", p.getErrorMessage());
		assertEquals("stacktrace", p.getErrorText());
		assertEquals("ingested", p.getLifecycle());
		assertEquals(1.173, p.getElapsed(), .01);

	}
	
	@Test
	public void unMarshallProcessXmlWithoutCompletedDate() throws JAXBException, Exception {
		Date now = new Date();
		String xml = "<process name=\"download\" status=\"waiting\" />";

		JAXBContext jaxbContext = JAXBContext
				.newInstance(edu.stanford.sulair.dlss.dor.admin.Process.class);
		Unmarshaller um = jaxbContext.createUnmarshaller();		
		ByteArrayInputStream bis = new ByteArrayInputStream(xml.getBytes());
		Process p = (Process) um.unmarshal(bis);
		assertEquals("download", p.getName());
		assertEquals("waiting", p.getStatus());
		assertTrue(p.getDatetime().after(now));

	}
	
}
