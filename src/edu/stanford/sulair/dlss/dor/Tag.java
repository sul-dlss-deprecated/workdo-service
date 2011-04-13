package edu.stanford.sulair.dlss.dor;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * Created by IntelliJ IDEA.
 * User: wmene
 * Date: Dec 3, 2009
 * Time: 2:05:10 PM
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement
public class Tag {


    private String tag;

    public Tag(){}

    public Tag(String t){
        tag = t;
    }

    @XmlValue
    public String getTag(){
        return tag;
    }

    public void setTag(String t){
        tag = t;
    }

    public String value(){
        return tag;
    }

    public String toString(){
        return tag;
    }



    @Override
    public boolean equals(Object o){
        if(o == this)
            return true;
        if(!(o instanceof Tag))
            return false;
        Tag otherTag = (Tag)o;
        return this.tag.equals(otherTag.tag);
    }

    @Override
    public int hashCode(){
        return tag.hashCode();
    }
}
