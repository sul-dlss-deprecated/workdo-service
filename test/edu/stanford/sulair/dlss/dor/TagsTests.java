package edu.stanford.sulair.dlss.dor;

import org.junit.Test;
import org.junit.Assert;
import org.custommonkey.xmlunit.XMLAssert;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: wmene
 * Date: Dec 3, 2009
 * Time: 2:18:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class TagsTests extends AbstractXmlMarhallingTest {

    @Test
	public void dumpTagsXml() throws Exception {
		JAXBContext jaxbContext = JAXBContext.newInstance(Tags.class);
		Tag t1 = new Tag("my tag");
        Tag t2 = new Tag("other tag");

        Tags tags = new Tags();
        tags.addTag(t1);
        tags.addTag(t2);

		String xml = marshall(jaxbContext, tags);
		System.out.println(xml);
		String expected = "<tags><tag>my tag</tag><tag>other tag</tag></tags>";
		XMLAssert.assertXMLEqual(expected, xml);


	}

    @Test
    public void unMarshallTagsXml() throws Exception{
        String xml = "<tags><tag>my tag</tag><tag>other tag</tag></tags>";

		JAXBContext jaxbContext = JAXBContext
				.newInstance(edu.stanford.sulair.dlss.dor.Tags.class);
		Unmarshaller um = jaxbContext.createUnmarshaller();
		ByteArrayInputStream bis = new ByteArrayInputStream(xml.getBytes());
		Tags tags = (Tags) um.unmarshal(bis);
        HashSet<String> expectedTags = new HashSet<String>();
        expectedTags.add("my tag");
        expectedTags.add("other tag");

        for(Tag t: tags){
            Assert.assertTrue(expectedTags.contains(t.value()));
        }
    }

    @Test
    public void addAllTest(){
        Tags t1 = new Tags();
        t1.addTag("tag1");
        t1.addTag("tag2");

        Tags t2 = new Tags();
        t2.addTag("tag2");
        t2.addTag("tag1");
        Assert.assertFalse(t1.addAll(t2));

        Tag t3 = new Tag("tag3");
        t2.addTag(t3);
        Assert.assertTrue(t1.addAll(t2));
        Assert.assertTrue(t1.getTags().contains(t3));

        Tags t4 = new Tags();
        t4.addTag("tag1");
        Assert.assertFalse(t1.addAll(t4));


    }
}
