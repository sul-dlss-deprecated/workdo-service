package edu.stanford.sulair.dlss.dor.admin;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Test;
import edu.stanford.sulair.dlss.dor.AbstractXmlMarhallingTest;

public class WorkflowTests extends AbstractXmlMarhallingTest {
	
	@Test
	public void setDruidsForAllProcesses(){
		Workflow gwf = new Workflow();
		Process p = new Process();
		p.setName("p1");
		gwf.addProcess(p);
		p = new Process();
		p.setName("p2");
		gwf.addProcess(p);
		
		gwf.initilizeProcesses("dor", "dr:123", null);
		List<Process> procs = gwf.getProcesses();
		for(Process p2: procs){
			assertEquals("dr:123", p2.getDruid());
            assertEquals("dor", p2.getRepository());
		}
	}
	
	@Test
	public void dumpGwfXml() throws Exception {
		JAXBContext jaxbContext = JAXBContext
				.newInstance(edu.stanford.sulair.dlss.dor.admin.Workflow.class);
		Process p = new Process();
		p.setName("download");
		p.setStatus("waiting");
		Calendar c = Calendar.getInstance();
		c.set(2008, 10, 15, 13, 30, 0);
		p.setDatetime(c.getTime());
		p.setDatastream("GoogleScannedWF");
		p.setDruid("dr:123");
		p.setId(5);
		p.setAttempts(1);
		p.setElapsed(1.1);
		p.setLifecycle("accessioning-started");
        p.setRepository("dor");
				
		Workflow gwf = new Workflow();
		gwf.setId("GoogleScannedWF");
        gwf.setObjectId("druid:1234");
        gwf.setRepository("dor");
		gwf.addProcess(p);
		
		p = new Process();
		p.setName("conversion");
		p.setStatus("done");
		p.setDatetime(c.getTime());
		p.setDatastream("GoogleScannedWF");
		p.setDruid("dr:123");
		p.setId(5);
		p.setAttempts(1);
		p.setElapsed(1.2);
        p.setRepository("dor");
		gwf.addProcess(p);
		
		
		String xml = marshall(jaxbContext, gwf);
        System.out.println(xml);
		String expected = "<workflow id=\"GoogleScannedWF\" objectId=\"druid:1234\" repository=\"dor\"><process name=\"download\" status=\"waiting\" datetime=\"2008-11-15T13:30:00-0800\" attempts=\"1\" lifecycle=\"accessioning-started\" elapsed=\"1.1\"/>" +
								"<process name=\"conversion\" status=\"done\" datetime=\"2008-11-15T13:30:00-0800\" attempts=\"1\" elapsed=\"1.2\"/></workflow>";
		XMLAssert.assertXMLEqual(expected, xml);
		Diff diff = new Diff(xml, expected);
		XMLAssert.assertXMLIdentical(diff, true);
		
	}
	
	@Test
	public void unMarshallGwfXml() throws JAXBException, Exception {
		String xml = "<workflow id=\"GoogleScannedWF\" objectId=\"druid:1234\" repository=\"dor\"><process name=\"download\" status=\"waiting\" datetime=\"2008-11-15T13:30:00-0800\" attempts=\"1\" lifecycle=\"downloaded\" /></workflow>";

		JAXBContext jaxbContext = JAXBContext
				.newInstance(edu.stanford.sulair.dlss.dor.admin.Workflow.class);
		Unmarshaller um = jaxbContext.createUnmarshaller();		
		ByteArrayInputStream bis = new ByteArrayInputStream(xml.getBytes());
		Workflow gwf = (Workflow) um.unmarshal(bis);
		Process p = gwf.getProcesses().get(0);
		assertEquals("download", p.getName());
		assertEquals("waiting", p.getStatus());
		//assertEquals("GoogleScannedWF", p.getDatastream());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		Date d = sdf.parse("2008-11-15T13:30:00-0800");
		assertEquals(0, d.compareTo(p.getDatetime()));
		assertEquals(1, p.getAttempts());
		assertEquals("downloaded", p.getLifecycle());
        assertEquals("druid:1234", gwf.getObjectId());
        assertEquals("dor", gwf.getRepository());

	}
	
}
