package edu.stanford.sulair.dlss.dor.admin;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="workflow")
public class Workflow {
	
	private List<Process> procs = new ArrayList<Process>();
	
	private String id;
    private String objId;
    private String repository;

    @XmlAttribute
	public String getId(){
		return id;
	}
	
	public void setId(String id){
		this.id = id;
	}

    @XmlAttribute
    public String getObjectId(){
        return this.objId;
    }

    public void setObjectId(String objId) {
        this.objId = objId;
    }

    @XmlAttribute
    public String getRepository(){
        return this.repository;
    }

    public void setRepository(String name){
        this.repository = name;
    }
	
	@XmlElement(name="process")
	public List<Process> getProcesses(){
		return procs;
	}
	
	public void setProcesses(List<Process> procList){
		procs = procList;
	}
	
	public void addProcess(Process p) {
		procs.add(p);
	}

	public void initilizeProcesses(String repository, String druid, String datastream) {
		if(procs != null){
			for(Process p: procs){
				p.setDruid(druid);
				p.setDatastream(datastream);
                p.setRepository(repository);
			}
		}
		
	}

}
