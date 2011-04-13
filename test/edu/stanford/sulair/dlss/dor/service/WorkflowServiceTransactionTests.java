package edu.stanford.sulair.dlss.dor.service;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import edu.stanford.sulair.dlss.dor.admin.Workflow;
import edu.stanford.sulair.dlss.dor.admin.Process;
import edu.stanford.sulair.dlss.dor.dao.ProcessDao;

@ContextConfiguration
public class WorkflowServiceTransactionTests extends AbstractTransactionalJUnit4SpringContextTests{

	@Autowired
	WorkflowService workflowService;
	
	@Autowired
	ProcessDao processDao;
	
	@Autowired
	private AnnotationSessionFactoryBean sessionFactory;
	
	private static boolean tablesCreated = false;

	@Before
	public void createTables() {
		if(!tablesCreated) {
			sessionFactory.createDatabaseSchema();
			tablesCreated = true;
		}
	}
	
	@After
	public void cleanUp() {
		this.deleteFromTables("workflow");
	}
	
	@Test
    public void testUpdateWorkflowStatusThrowsException(){
    	Process p = new Process("dr:123", "googleworkflow", "pname","done", new Date());
        p.setRepository("dor");
    
    	try {
    		workflowService.persist(p, "dor");
    		fail();
    	}catch(Exception e){ }
    	Process p2 = processDao.findProcess("dor", "dr:123", "googleworkflow", "pname");
    	assertNull(p2);
    	assertEquals(0,this.countRowsInTable("WORKFLOW"));
    }
	
	@Test 
	public void persistGoogleWorkflowThrowsException(){
		//Persist one process
		//Create google workflow
		//Attempt to persist workflow, throws exception
		//Assert first process untouched
		//Insert 1 process row
		Process p = new Process();
		p.setDruid("dr:1234");
		p.setDatastream("GoogleWorkflow");
		p.setName("download");
		p.setStatus("pending");
        p.setRepository("dor");
		processDao.persistProcess(p);
		p = processDao.findProcess("dor", "dr:1234", "GoogleWorkflow", "download" );
    	assertEquals(1, this.countRowsInTable("WORKFLOW"));
		//Create googleworkflow
    	Process p2 = new Process();
    	p2.setDruid("dr:1234");
    	p2.setDatastream("GoogleWorkflow");
		p2.setName("download");
		p2.setStatus("done");
        p2.setRepository("dor");
		Process p3 = new Process();
    	p3.setDruid("dr:1234");
    	p3.setDatastream("GoogleWorkflow");
		p3.setName("conversion");
		p3.setStatus("pending");
        p3.setRepository("dor");
		ArrayList<Process>procs = new ArrayList<Process>();
		procs.add(p2);
		procs.add(p3);
		Workflow gwf = new Workflow();
		gwf.setProcesses(procs);
		//Persist googleworkflow
		try {
			workflowService.persist(gwf, "dor");
			fail();
		}catch(Exception e){}
		//Assert googleworkflow rows added
		Process p4 = processDao.findProcess("dor", "dr:1234", "GoogleWorkflow", "download");
		assertEquals("pending", p4.getStatus());
		//Assert existing workflow updated
		assertEquals(1, this.countRowsInTable("WORKFLOW"));
	}
}
