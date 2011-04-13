package edu.stanford.sulair.dlss.dor;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: wmene
 * Date: Dec 3, 2009
 * Time: 2:18:11 PM
 * To change this template use File | Settings | File Templates.
 */

@XmlRootElement
public class Tags implements Iterable<Tag>{
    private Set<Tag> tags = new HashSet<Tag>();

    @XmlElement(name="tag")
    public Set<Tag> getTags(){
        return tags;
    }

    public Iterator<Tag> iterator(){
        return tags.iterator();
    }

    public void setTags(List<Tag> tagList){
        tags.addAll(tagList);
    }

    public void addTag(Tag t){
        tags.add(t);
    }

    public void addTag(String s){
        tags.add(new Tag(s));
    }

    public boolean addAll(Tags newTags){
        return this.tags.addAll(newTags.tags);
    }
    
}
