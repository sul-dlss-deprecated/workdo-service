package edu.stanford.sulair.dlss.dor.admin;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

//Jaxb Annotations
@XmlRootElement
@XmlType(propOrder={"name", "status", "datetime", "attempts"})
//JPA Annotations
@Entity
@Table(name="workflow")
public class Process {
	
	public static final String STATUS_WAITING = "waiting";
	
	public static final String STATUS_COMPLETED = "completed";
	
	public static final String STATUS_ERROR = "error";

    public static final String STATUS_QUEUED = "queued";
	
	//Used only for database persistance
	private long id;
	
	private String druid;
	
	private String datastream;

	private String name;

	private String status;
	
	private String errorMessage;
	
	private String errorText;

	private Date datetime = new Date();
	
	private int attempts;

	private String lifecycle;
	
	private double elapsed;

    private String repository;

    public Process(){}
	
	public Process(String druid, String datastream, String name, String status, Date dateTime){
		this.druid = druid;
		this.name = name;
		this.status = status;
		this.datetime = dateTime;
	}
	
	@XmlTransient
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO, generator="WORKFLOW_SEQ")
	@SequenceGenerator(name="WORKFLOW_SEQ", sequenceName="workflow_seq")
	public long getId() {
		return this.id;
	}
	
	public void setId(long id){
		this.id = id;
	}
	
	@XmlTransient
	@Column(length=256)
	public String getDruid() {
		return druid;
	}

	public void setDruid(String pid) {
		this.druid = pid;
	}
	
	@XmlTransient
	@Column(length=256)
	public String getDatastream() {
		return this.datastream;
	}
	
	public void setDatastream(String ds) {
		this.datastream = ds;
	}

    @XmlTransient
    @Column(length=256)
    public void setRepository(String name) {
        this.repository = name;
    }

    public String getRepository(){
        return this.repository;
    }

	@XmlAttribute
	@Column(name="process", length=256)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute
	@Column(length=256)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@XmlAttribute
	@XmlJavaTypeAdapter(DateAdapter.class)
	@Column
	public Date getDatetime() {
		return datetime;
	}
	
	public void setDatetime(Date completed) {
		this.datetime = completed;
	}

	@XmlAttribute
	@Column
	public int getAttempts() {
		return attempts;
	}

	public void setAttempts(int attempts) {
		this.attempts = attempts;
	}

	@XmlAttribute
	@Column(name="error_msg", length=256)
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String msg) {
		this.errorMessage = msg;
		
	}
	
	@XmlAttribute
	@Lob
	@Column(name="error_txt")
	public String getErrorText(){
		return this.errorText;
	}
	
	public void setErrorText(String text) {
		this.errorText = text;
	}

	public void setLifecycle(String lc) {
		this.lifecycle = lc;
	}
	
	@XmlAttribute
	@Column(name="lifecycle", length=256)
	public String getLifecycle() {
		return lifecycle;
	}
	
	
	public void setElapsed(double elapsed) {
		this.elapsed = elapsed;
		
	}

	@XmlAttribute
	@Column(name="elapsed")
	public double getElapsed() {
		return elapsed;
	}

}
