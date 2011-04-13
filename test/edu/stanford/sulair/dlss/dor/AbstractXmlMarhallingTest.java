package edu.stanford.sulair.dlss.dor;

import org.junit.Before;
import org.custommonkey.xmlunit.XMLUnit;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: wmene
 * Date: Dec 3, 2009
 * Time: 2:45:22 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractXmlMarhallingTest {
    protected String marshall(JAXBContext jaxbContext, Object o)
			throws JAXBException, PropertyException {
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		marshaller.marshal(o, bos);
		return bos.toString();
	}

    @Before
	public void setUp() {
		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setIgnoreComments(true);
		XMLUnit.setIgnoreAttributeOrder(false);
	}
}
