package edu.stanford.sulair.dlss.dor.service;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import edu.stanford.sulair.dlss.dor.AbstractProcessDatabaseTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import edu.stanford.sulair.dlss.dor.admin.Workflow;
import edu.stanford.sulair.dlss.dor.admin.Process;
import edu.stanford.sulair.dlss.dor.service.mock.MesssagingServiceMock;
import edu.stanford.sulair.dlss.dor.service.mock.RepositoryServiceMock;

@ContextConfiguration
public class WorkflowServiceTests extends AbstractProcessDatabaseTest {

	@Autowired
	WorkflowService workflowService;

	@Autowired
	MesssagingServiceMock msgService;
	
	@Autowired
	RepositoryServiceMock repoService;
	
	@Before
	public void createTables() {
		super.createTables();
		
		msgService.setUp();
		repoService.setUp();
	}

	
	@Test
    public void testUpdateExistingWorkflow(){
		Process p = new Process();
		p.setDruid("dr:1234");
		p.setDatastream("GoogleWorkflow");
		p.setName("download");
		p.setStatus("pending");
		p.setAttempts(1);
        p.setLifecycle("downloading");
        p.setRepository("dor");
		dao.persistProcess(p);
		p = dao.findProcess("dor", "dr:1234", "GoogleWorkflow", "download" );
    	assertEquals(1, this.countRowsInTable("WORKFLOW"));
    	assertEquals(1, p.getAttempts());
		
    	Process p2 = new Process();
		p2.setDruid("dr:1234");
		p2.setDatastream("GoogleWorkflow");
		p2.setName("download");
		p2.setStatus("done");
        p2.setRepository("dor");
    	workflowService.persist(p2, "dor");
    	
    	Process p3 = dao.findProcess("dor", "dr:1234", "GoogleWorkflow", "download");
    	assertEquals("done", p3.getStatus());
    	assertEquals(2, p3.getAttempts());
        assertEquals("downloading", p3.getLifecycle());
    	assertEquals(1, this.countRowsInTable("WORKFLOW"));
    	
    	
    }

    	@Test
    public void testUpdateExistingWorkflowWithNullLifecycle(){
		Process p = new Process();
		p.setDruid("dr:1234");
		p.setDatastream("GoogleWorkflow");
		p.setName("download");
		p.setStatus("pending");
		p.setAttempts(1);
        p.setRepository("dor");
		dao.persistProcess(p);
		p = dao.findProcess("dor", "dr:1234", "GoogleWorkflow", "download" );
    	assertEquals(1, this.countRowsInTable("WORKFLOW"));
    	assertEquals(1, p.getAttempts());

    	Process p2 = new Process();
		p2.setDruid("dr:1234");
		p2.setDatastream("GoogleWorkflow");
		p2.setName("download");
		p2.setStatus("done");
        p2.setRepository("dor");
    	workflowService.persist(p2, "dor");

    	Process p3 = dao.findProcess("dor", "dr:1234", "GoogleWorkflow", "download");
    	assertEquals("done", p3.getStatus());
    	assertEquals(2, p3.getAttempts());
        assertNull(p3.getLifecycle());
    	assertEquals(1, this.countRowsInTable("WORKFLOW"));


    }
	
	@Test
	public void putGoogleWorkflow() {
		//Insert 1 process row
		Process p = new Process();
		p.setDruid("dr:1234");
		p.setDatastream("GoogleWorkflow");
		p.setName("download");
		p.setStatus("pending");
        p.setRepository("dor");
		dao.persistProcess(p);
		p = dao.findProcess("dor", "dr:1234", "GoogleWorkflow", "download" );
    	assertEquals(1, this.countRowsInTable("WORKFLOW"));
		persistGoogleWorkflowWith2Processes();
		
		//Assert googleworkflow rows added
		assertNotNull(dao.findProcess("dor", "dr:1234", "GoogleWorkflow", "download"));
		assertNotNull(dao.findProcess("dor", "dr:1234", "GoogleWorkflow", "conversion"));
		//Assert existing workflow updated
		assertEquals(2, this.countRowsInTable("WORKFLOW"));
		assertTrue(msgService.wasServiceUsed());
		assertFalse(repoService.wasCreateWorkflowDatastreamUsed());
	}

	private void persistGoogleWorkflowWith2Processes() {
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
		workflowService.persist(gwf, "dor");
	}
	
	@Test
	public void findGoogleWorkflowByDruid() {
		persistGoogleWorkflowWith2Processes();
		
		Workflow gwf = workflowService.findWorkflow("dor", "dr:1234", "GoogleWorkflow");
		assertEquals(2, gwf.getProcesses().size());
		assertEquals("GoogleWorkflow", gwf.getId());
		assertEquals("dr:1234", gwf.getProcesses().get(0).getDruid());
        assertEquals("dr:1234", gwf.getObjectId());
		assertFalse(msgService.wasServiceUsed());
		assertTrue(repoService.wasCreateWorkflowDatastreamUsed());
	}

    // Two different workflows exist and create workflow gets called for one of the existing workflows.
    // It should not delete all the workflow rows
    @Test
	public void doNotClobberExistingProcessRowsFromADifferentWorkflow() {
        // Existing workflow rows
		createAndPersistProcess("dor", "dr:1234", "googleScannedBookWF", "ingest", "completed", "archived");
		createAndPersistProcess("dor", "dr:1234", "googleScannedBookWF", "shelve", "completed", "released");
		createAndPersistProcess("dor", "dr:1234", "googleScannedBookWF", "index",  "waiting");
        createAndPersistProcess("dor", "dr:1234", "googleScannedBookWF", "cleanup","waiting",   "accessioned");


        createAndPersistProcess("dor", "dr:1234", "etdAccessionWF", "pre-register","waiting",   "accessioned");

        //Incoming workflow
        Process p2 = createProcess("dor", "dr:1234", "etdAccessionWF", "register", "waiting");
        Process p3 = createProcess("dor", "dr:1234", "etdAccessionWF", "shelve", "waiting");

        ArrayList<Process>procs = new ArrayList<Process>();
		procs.add(p2);
		procs.add(p3);
		Workflow gwf = new Workflow();
		gwf.setProcesses(procs);

        workflowService.persist(gwf, "dor");

		gwf = workflowService.findWorkflow("dor", "dr:1234", "googleScannedBookWF");
		assertEquals(4, gwf.getProcesses().size());

        Workflow etd = workflowService.findWorkflow("dor", "dr:1234", "etdAccessionWF");
        assertEquals(2, etd.getProcesses().size());
        java.util.HashSet<String> steps = new java.util.HashSet<String>();
        for(Process p: etd.getProcesses()){
            steps.add(p.getName());
        }
        assertTrue(steps.contains("register"));
        assertTrue(steps.contains("shelve"));
	}
}
