package edu.stanford.sulair.dlss.dor;

import org.junit.Test;
import org.junit.Assert;
import org.custommonkey.xmlunit.XMLAssert;

import javax.xml.bind.*;

import java.io.ByteArrayInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: wmene
 * Date: Dec 3, 2009
 * Time: 2:03:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class TagTests extends AbstractXmlMarhallingTest {

	@Test
	public void dumpTagXml() throws Exception {
		JAXBContext jaxbContext = JAXBContext.newInstance(Tag.class);
		Tag tag = new Tag("my tag");

		String xml = marshall(jaxbContext, tag);
		System.out.println(xml);
		String expected = "<tag>my tag</tag>";
		XMLAssert.assertXMLEqual(expected, xml);
	}

        @Test
    public void unMarshallTagXml() throws Exception{
        String xml = "<tag>my tag</tag>";

		JAXBContext jaxbContext = JAXBContext
				.newInstance(edu.stanford.sulair.dlss.dor.Tag.class);
		Unmarshaller um = jaxbContext.createUnmarshaller();
		ByteArrayInputStream bis = new ByteArrayInputStream(xml.getBytes());
		Tag tag = (Tag) um.unmarshal(bis);
        Assert.assertEquals("my tag", tag.value());

    }

    @Test
    public void equalsTest(){
        Tag t1 = new Tag("some value");
        Tag t2 = new Tag("some " +"value");
        Assert.assertEquals(t1, t2);
    }

    @Test
    public void hashCodeTest(){
        Tag t1 = new Tag("some value");
        Tag t2 = new Tag("some " +"value");
        Assert.assertEquals(t1.hashCode(), t2.hashCode());
    }
}
