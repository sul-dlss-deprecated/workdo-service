package edu.stanford.sulair.dlss.dor.dao;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import edu.stanford.sulair.dlss.dor.AbstractProcessDatabaseTest;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import edu.stanford.sulair.dlss.dor.admin.Process;

@ContextConfiguration
public class ProcessDaoTests extends AbstractProcessDatabaseTest {


    @Test
	public void insertWorkflowRow() throws ParseException {
		Process p = new Process();
		p.setDruid("dr:123");
		p.setDatastream("GoogleWorkflow");
		p.setName("download");
		p.setStatus("pending");
		p.setAttempts(1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss z");
		Date originalDate = sdf.parse("2008.11.15 13:30:00 PST");
		p.setDatetime(originalDate);
		p.setErrorMessage("message");
		p.setErrorText("text");
		p.setLifecycle("shelved");
		p.setElapsed(1.323);
        p.setRepository("dor");
		dao.persistProcess(p);
		
		Date beforeQuery = new Date();
		Process p2 = dao.findProcess("dor", "dr:123", "GoogleWorkflow", "download");
		assertEquals("GoogleWorkflow", p2.getDatastream());
		assertEquals(1, p2.getAttempts());
		assertEquals(1, this.countRowsInTable("WORKFLOW"));
		assertTrue(p2.getDatetime().before(beforeQuery));
		assertTrue(p2.getDatetime().equals(originalDate));
		assertEquals("message", p2.getErrorMessage());
		assertEquals("text", p2.getErrorText());
		assertEquals("shelved", p2.getLifecycle());
		assertEquals(1.323, p2.getElapsed(), 0.01);
        assertEquals("dor", p2.getRepository());
		
	}
	
	@Test 
	public void insertWorkflowAndFindByAttributes() {
		Process p = new Process();
		p.setDruid("dr:1234");
		p.setDatastream("GoogleWorkflow");
		p.setName("download");
		p.setStatus("pending");
        p.setRepository("dor");
		dao.persistProcess(p);
		
		p = dao.findProcess("dor", "dr:1234", "GoogleWorkflow", "download");

		assertEquals("GoogleWorkflow", p.getDatastream());
        assertEquals("dor", p.getRepository());
		assertEquals(1, this.countRowsInTable("WORKFLOW"));
	}
	
	@Test 
	public void insertWorkflowsAndFindByDruidAndDatastream() {
		Process p = new Process();
		p.setDruid("dr:1234");
		p.setDatastream("GoogleWorkflow");
		p.setName("download");
		p.setStatus("pending");
        p.setRepository("dor");
		dao.persistProcess(p);
		
		p = dao.findProcess("dor", "dr:1234", "GoogleWorkflow", "download");
		
		p = new Process();
		p.setDruid("dr:1234");
		p.setDatastream("GoogleWorkflow");
		p.setName("convert");
		p.setStatus("pending");
        p.setRepository("dor");
		dao.persistProcess(p);
		
		List<Process> procs = dao.findProcessesByDruidAndDatastream("dor", "dr:1234", "GoogleWorkflow");

		assertEquals(2, procs.size());
		assertEquals(2, this.countRowsInTable("WORKFLOW"));
	}
	
	@Test
	public void updateExistingWorkflowRow() throws Exception {
		Process p = new Process();
		p.setDruid("dr:1234");
		p.setDatastream("GoogleWorkflow");
		p.setName("download");
		p.setStatus("pending");
		p.setAttempts(1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss z");
		Date originalDate = sdf.parse("2008.11.15 13:30:00 PST");
		p.setDatetime(originalDate);
		p.setLifecycle("ingested");
        p.setRepository("dor");
		dao.persistProcess(p);	
		p = dao.findProcess("dor", "dr:1234", "GoogleWorkflow", "download");
		assertEquals(1, this.countRowsInTable("WORKFLOW"));
		assertEquals(originalDate, p.getDatetime());
		
		Process p2 = new Process();
		p2.setId(p.getId());
		p2.setDruid("dr:1234");
		p2.setDatastream("GoogleWorkflow");
		p2.setName("download");
		p2.setStatus("done");
		p2.setAttempts(2);
		p2.setLifecycle("downloaded");
        p2.setRepository("dor");
		dao.persistProcess(p2);
		assertTrue(p2.getDatetime().after(originalDate));
		Date secondDate = (Date) p2.getDatetime().clone();
		Process p3 = dao.findProcess("dor", "dr:1234", "GoogleWorkflow", "download");
		assertEquals(1, this.countRowsInTable("WORKFLOW"));
		assertEquals("done", p3.getStatus());
		assertEquals("downloaded", p3.getLifecycle());
		assertEquals(2, p3.getAttempts());
		assertTrue(p3.getDatetime().equals(secondDate));
		assertTrue(p3.getDatetime().after(originalDate));
		
	}
	
	@Test
	public void deleteAllWorkflowRowsByDruidandRepository() {
		ArrayList<Process> procs = new ArrayList<Process>();
		Process p = new Process();
		p.setDruid("dr:1234");
		p.setDatastream("GoogleWorkflow");
		p.setName("download");
		p.setStatus("pending");
        p.setRepository("dor");
		dao.persistProcess(p);
		Process p2 = new Process();
		p2.setDruid("dr:1234");
		p2.setDatastream("GoogleWorkflow");
		p2.setName("convert");
		p2.setStatus("done");
        p2.setRepository("dor");
		dao.persistProcess(p2);
		Process p3 = new Process();
		p3.setDruid("dr:5678");
		p3.setDatastream("GoogleWorkflow");
		p3.setName("convert");
		p3.setStatus("done");
        p3.setRepository("dor");
		dao.persistProcess(p3);
		p = dao.findProcess("dor", "dr:1234", "GoogleWorkflow", "download");
		assertEquals(3, this.countRowsInTable("WORKFLOW"));
		
		dao.deleteProcessesByRepoDruidAndWorkflowName("dor", "dr:1234", "GoogleWorkflow");
		assertEquals(1, this.countRowsInTable("WORKFLOW"));
		
	}

    private void setupWaitingDruidsByDatastreamAndName() {
        Process p = new Process();
        p.setDruid("dr:1234");
        p.setDatastream("GoogleBooksWF");
        p.setName("register-object");
        p.setStatus("completed");
        p.setRepository("dor");
        dao.persistProcess(p);
        Process p2 = new Process();
        p2.setDruid("dr:1234");
        p2.setDatastream("GoogleBooksWF");
        p2.setName("descriptive-metadata");
        p2.setStatus("waiting");
        p2.setRepository("dor");
        dao.persistProcess(p2);
        Process p3 = new Process();
        p3.setDruid("dr:5678");
        p3.setDatastream("GoogleBooksWF");
        p3.setName("google-convert");
        p3.setStatus("waiting");
        p3.setRepository("dor");
        dao.persistProcess(p3);
    }
	
	@Test
	public void findWaitingDruidsByDatastreamAndName() {
        setupWaitingDruidsByDatastreamAndName();
		
		List<String> druids = dao.findWaitingDruids("dor", "GoogleBooksWF", "descriptive-metadata");
		assertEquals(1, druids.size());
		assertEquals("dr:1234", druids.get(0));
	}

    @Test
	public void countWaitingDruidsByDatastreamAndName() {
        setupWaitingDruidsByDatastreamAndName();

		int count = dao.countWaitingDruids("dor", "GoogleBooksWF", "descriptive-metadata");
		assertEquals(1, count);
	}

    @Test
	public void findWaitingDruidsWithStatusesOtherThanWaiting() {
		Process p = new Process();
		p.setDruid("dr:1234");
		p.setDatastream("GoogleBooksWF");
		p.setName("register-object");
		p.setStatus("completed");
        p.setRepository("dor");
		dao.persistProcess(p);
        p.setDruid("dr:1234");
		p.setDatastream("GoogleBooksWF");
		p.setName("register-object");
		p.setStatus("completed");
        p.setRepository("sdr");
		dao.persistProcess(p);
		Process p2 = new Process();
		p2.setDruid("dr:1234");
		p2.setDatastream("GoogleBooksWF");
		p2.setName("descriptive-metadata");
		p2.setStatus("waiting");
        p2.setRepository("dor");
		dao.persistProcess(p2);
		Process p3 = new Process();
		p3.setDruid("dr:5678");
		p3.setDatastream("GoogleBooksWF");
		p3.setName("google-convert");
		p3.setStatus("waiting");
        p3.setRepository("dor");
		dao.persistProcess(p3);
        Process p4 = new Process();
		p4.setDruid("dr:4321");
		p4.setDatastream("GoogleBooksWF");
		p4.setName("descriptive-metadata");
		p4.setStatus("INPROCESS");
        p4.setRepository("dor");
		dao.persistProcess(p4);

		List<String> druids = dao.findWaitingDruids("dor", "GoogleBooksWF", "descriptive-metadata");
		assertEquals(2, druids.size());
		assertTrue(druids.contains("dr:1234"));
        assertTrue(druids.contains("dr:4321"));

        // Test count query
        int count = dao.countWaitingDruids("dor", "GoogleBooksWF", "descriptive-metadata");
        assertEquals(2, count);
	}
	
	@Test
	public void findWaitingDruidsByDatastreamNameAndCompleted() {
		Process p = new Process();
		p.setDruid("dr:1234");
		p.setDatastream("GoogleBooksWF");
		p.setName("register-object");
		p.setStatus("completed");
        p.setRepository("dor");
		dao.persistProcess(p);
		Process p2 = new Process();
		p2.setDruid("dr:1234");
		p2.setDatastream("GoogleBooksWF");
		p2.setName("descriptive-metadata");
		p2.setStatus("completed");
        p2.setRepository("dor");
		dao.persistProcess(p2);
		Process p3 = new Process();
		p3.setDruid("dr:1234");
		p3.setDatastream("GoogleBooksWF");
		p3.setName("google-convert");
		p3.setStatus("waiting");
        p3.setRepository("dor");
		dao.persistProcess(p3);
		
		List<String> druids = dao.findWaitingDruidsByDatastreamNameAndCompleted("dor", "GoogleBooksWF", "google-convert", "descriptive-metadata");
		assertEquals(1, druids.size());
		assertEquals("dr:1234", druids.get(0));

        // Test count query
        assertEquals(1, dao.countWaitingDruidsByDatastreamNameAndCompleted("dor", "GoogleBooksWF", "google-convert", "descriptive-metadata"));
	}

    @Test
	public void findWaitingDruidsByDatastreamNameAndCompletedWithoutWaitingStep() {
		Process p = new Process();
		p.setDruid("dr:1234");
		p.setDatastream("GoogleBooksWF");
		p.setName("register-object");
		p.setStatus("completed");
        p.setRepository("dor");
		dao.persistProcess(p);
		Process p2 = new Process();
		p2.setDruid("dr:1234");
		p2.setDatastream("GoogleBooksWF");
		p2.setName("descriptive-metadata");
		p2.setStatus("completed");
        p2.setRepository("dor");
		dao.persistProcess(p2);
		Process p3 = new Process();
		p3.setDruid("dr:1234");
		p3.setDatastream("GoogleBooksWF");
		p3.setName("google-convert");
		p3.setStatus("waiting");
        p3.setRepository("dor");
		dao.persistProcess(p3);

        p2 = new Process();
		p2.setDruid("dr:4321");
		p2.setDatastream("GoogleBooksWF");
		p2.setName("descriptive-metadata");
		p2.setStatus("completed");
        p2.setRepository("dor");
		dao.persistProcess(p2);
		p3 = new Process();
		p3.setDruid("dr:4321");
		p3.setDatastream("GoogleBooksWF");
		p3.setName("google-convert");
		p3.setStatus("INPROCESS");
        p3.setRepository("dor");
		dao.persistProcess(p3);

		List<String> druids = dao.findWaitingDruidsByDatastreamNameAndCompleted("dor", "GoogleBooksWF", "google-convert", "descriptive-metadata");
		assertEquals(2, druids.size());
		assertTrue(druids.contains("dr:1234"));
        assertTrue(druids.contains("dr:4321"));

        // Test count query
        assertEquals(2, dao.countWaitingDruidsByDatastreamNameAndCompleted("dor", "GoogleBooksWF", "google-convert", "descriptive-metadata"));
	}

    @Test
	public void findWaitingDruidsByDatastreamNameAndCompletedWithoutWaitingStepIgnoringQueued() {
		Process p = new Process();
		p.setDruid("dr:1234");
		p.setDatastream("GoogleBooksWF");
		p.setName("register-object");
		p.setStatus("completed");
        p.setRepository("dor");
		dao.persistProcess(p);
		Process p2 = new Process();
		p2.setDruid("dr:1234");
		p2.setDatastream("GoogleBooksWF");
		p2.setName("descriptive-metadata");
		p2.setStatus("completed");
        p2.setRepository("dor");
		dao.persistProcess(p2);
		Process p3 = new Process();
		p3.setDruid("dr:1234");
		p3.setDatastream("GoogleBooksWF");
		p3.setName("google-convert");
		p3.setStatus("waiting");
        p3.setRepository("dor");
		dao.persistProcess(p3);

        p2 = new Process();
		p2.setDruid("dr:4321");
		p2.setDatastream("GoogleBooksWF");
		p2.setName("descriptive-metadata");
		p2.setStatus("completed");
        p2.setRepository("dor");
		dao.persistProcess(p2);
		p3 = new Process();
		p3.setDruid("dr:4321");
		p3.setDatastream("GoogleBooksWF");
		p3.setName("google-convert");
		p3.setStatus("INPROCESS");
        p3.setRepository("dor");
		dao.persistProcess(p3);

        //This druid should not show up in the query results
        p2 = new Process();
		p2.setDruid("dr:5678");
		p2.setDatastream("GoogleBooksWF");
		p2.setName("descriptive-metadata");
		p2.setStatus("completed");
        p2.setRepository("dor");
		dao.persistProcess(p2);
		p3 = new Process();
		p3.setDruid("dr:5678");
		p3.setDatastream("GoogleBooksWF");
		p3.setName("google-convert");
		p3.setStatus("queued");
        p3.setRepository("dor");
		dao.persistProcess(p3);

		List<String> druids = dao.findWaitingDruidsByDatastreamNameAndCompleted("dor", "GoogleBooksWF", "google-convert", "descriptive-metadata");
		assertEquals(2, druids.size());
		assertTrue(druids.contains("dr:1234"));
        assertTrue(druids.contains("dr:4321"));

        // Test count query
        assertEquals(2, dao.countWaitingDruidsByDatastreamNameAndCompleted("dor", "GoogleBooksWF", "google-convert", "descriptive-metadata"));
	}
	
	@Test
	public void findWaitingProcessWithTwoCompletedSteps() {
		createAndPersistProcess("dor", "dr:1234", "googleScannedBookWF", "ingest", "completed");
		createAndPersistProcess("dor", "dr:1234", "googleScannedBookWF", "shelve", "completed");
		createAndPersistProcess("dor", "dr:1234", "googleScannedBookWF", "cleanup", "waiting");
		
		List<String> druids = dao.findWaitingDruidsWithTwoCompletedProcesses("dor", "googleScannedBookWF", "cleanup", "ingest", "shelve");
		assertEquals(1, druids.size());
		assertEquals("dr:1234", druids.get(0));

        // Test count query
        assertEquals(1, dao.countWaitingDruidsWithTwoCompletedProcesses("dor", "googleScannedBookWF", "cleanup", "ingest", "shelve"));
	}

    @Test
	public void findWaitingProcessWithTwoCompletedStepsAndNoProcessWithStatusOfWaiting() {
		createAndPersistProcess("dor", "dr:1234", "googleScannedBookWF", "ingest", "completed");
		createAndPersistProcess("dor", "dr:1234", "googleScannedBookWF", "shelve", "completed");
		createAndPersistProcess("dor", "dr:1234", "googleScannedBookWF", "cleanup", "waiting");

        createAndPersistProcess("dor", "dr:4321", "googleScannedBookWF", "ingest", "completed");
		createAndPersistProcess("dor", "dr:4321", "googleScannedBookWF", "shelve", "completed");
		createAndPersistProcess("dor", "dr:4321", "googleScannedBookWF", "cleanup", "WORKING_ON_IT");

		List<String> druids = dao.findWaitingDruidsWithTwoCompletedProcesses("dor", "googleScannedBookWF", "cleanup", "ingest", "shelve");
		assertEquals(2, druids.size());
		assertTrue(druids.contains("dr:1234"));
        assertTrue(druids.contains("dr:4321"));

        // Test count query
        assertEquals(2, dao.countWaitingDruidsWithTwoCompletedProcesses("dor", "googleScannedBookWF", "cleanup", "ingest", "shelve"));
	}

    @Test
	public void findWaitingProcessWithTwoCompletedStepsAndDoNotCountQueued() {
		createAndPersistProcess("dor", "dr:1234", "googleScannedBookWF", "ingest", "completed");
		createAndPersistProcess("dor", "dr:1234", "googleScannedBookWF", "shelve", "completed");
		createAndPersistProcess("dor", "dr:1234", "googleScannedBookWF", "cleanup", "waiting");

        createAndPersistProcess("dor", "dr:4321", "googleScannedBookWF", "ingest", "completed");
		createAndPersistProcess("dor", "dr:4321", "googleScannedBookWF", "shelve", "completed");
		createAndPersistProcess("dor", "dr:4321", "googleScannedBookWF", "cleanup", "WORKING_ON_IT");

        //This druid should not show up in the query results
        createAndPersistProcess("dor", "dr:6789", "googleScannedBookWF", "ingest", "completed");
		createAndPersistProcess("dor", "dr:6789", "googleScannedBookWF", "shelve", "completed");
		createAndPersistProcess("dor", "dr:6789", "googleScannedBookWF", "cleanup", "queued");

		List<String> druids = dao.findWaitingDruidsWithTwoCompletedProcesses("dor", "googleScannedBookWF", "cleanup", "ingest", "shelve");
		assertEquals(2, druids.size());
		assertTrue(druids.contains("dr:1234"));
        assertTrue(druids.contains("dr:4321"));

        // Test count query
        assertEquals(2, dao.countWaitingDruidsWithTwoCompletedProcesses("dor", "googleScannedBookWF", "cleanup", "ingest", "shelve"));
	}

    @Test
	public void findWaitingProcessWithTwoCompletedStepsAndAllStepsComplete() {
		createAndPersistProcess("dor", "dr:1234", "googleScannedBookWF", "ingest", "completed");
		createAndPersistProcess("dor", "dr:1234", "googleScannedBookWF", "shelve", "completed");
		createAndPersistProcess("dor", "dr:1234", "googleScannedBookWF", "cleanup", "completed");

        createAndPersistProcess("dor", "dr:4321", "googleScannedBookWF", "ingest", "completed");
		createAndPersistProcess("dor", "dr:4321", "googleScannedBookWF", "shelve", "completed");
		createAndPersistProcess("dor", "dr:4321", "googleScannedBookWF", "cleanup", "completed");

		List<String> druids = dao.findWaitingDruidsWithTwoCompletedProcesses("dor", "googleScannedBookWF", "cleanup", "ingest", "shelve");
		assertEquals(0, druids.size());

        // Test count query
        assertEquals(0, dao.countWaitingDruidsWithTwoCompletedProcesses("dor", "googleScannedBookWF", "cleanup", "ingest", "shelve"));
	}
	
	@Test
	public void findCompletedDruids() {        
        createAndPersistProcess("dor", "dr:1234", "googleScannedBookWF", "register-object", "completed");
		createAndPersistProcess("dor", "dr:1234", "googleScannedBookWF", "descriptive-metadata", "completed");
		createAndPersistProcess("dor", "dr:1234", "googleScannedBookWF", "google-convert", "waiting");
		
		List<String> druids = dao.findCompletedDruids("dor", "googleScannedBookWF", "register-object");
		assertEquals(1, druids.size());
		assertEquals("dr:1234", druids.get(0));

        // Test count query
        assertEquals(1, dao.countCompletedDruids("dor", "googleScannedBookWF", "register-object"));
	}

    @Test
	public void findQueuedDruids() {
        createAndPersistProcess("dor", "dr:1234", "googleScannedBookWF", "register-object", "completed");
		createAndPersistProcess("dor", "dr:1234", "googleScannedBookWF", "descriptive-metadata", "completed");
		createAndPersistProcess("dor", "dr:1234", "googleScannedBookWF", "process-content", "queued");

        createAndPersistProcess("dor", "dr:3456", "googleScannedBookWF", "process-content", "queued");

		List<String> druids = dao.findQueuedDruids("dor", "googleScannedBookWF", "process-content");
		assertEquals(2, druids.size());
		assertTrue(druids.contains("dr:1234"));
        assertTrue(druids.contains("dr:3456"));

        // Test count query
        assertEquals(2, dao.countQueuedDruids("dor", "googleScannedBookWF", "process-content"));
	}
	
	@Test
	public void findErrorDruids() {
		Process p = new Process();
		p.setDruid("dr:1234");
		p.setDatastream("GoogleBooksWF");
		p.setName("register-object");
		p.setStatus("completed");
        p.setRepository("dor");
		dao.persistProcess(p);
		Process p2 = new Process();
		p2.setDruid("dr:1234");
		p2.setDatastream("GoogleBooksWF");
		p2.setName("descriptive-metadata");
		p2.setStatus("completed");
        p2.setRepository("dor");
		dao.persistProcess(p2);
		Process p3 = new Process();
		p3.setDruid("dr:1234");
		p3.setDatastream("GoogleBooksWF");
		p3.setName("google-convert");
		p3.setStatus("waiting");
        p3.setRepository("dor");
		dao.persistProcess(p3);
		Process p4 = new Process();
		p4.setDruid("dr:1234");
		p4.setDatastream("GoogleBooksWF");
		p4.setName("ingest");
		p4.setStatus("error");
        p4.setRepository("dor");
		dao.persistProcess(p4);
		p4 = new Process();
		p4.setDruid("dr:6789");
		p4.setDatastream("GoogleBooksWF");
		p4.setName("ingest");
		p4.setStatus("error");
        p4.setRepository("dor");
		dao.persistProcess(p4);
		
		List<Process> procs = dao.findErrorProcessesByDatastreamAndName("dor", "GoogleBooksWF", "ingest");
		assertEquals(2, procs.size());
		HashSet<String> druids = new HashSet<String>();
		for(Process proc: procs){
			druids.add(proc.getDruid());
		}
		assertTrue(druids.contains("dr:1234"));
		assertTrue(druids.contains("dr:6789"));

        // Test count method
        int count = dao.countErrorProcessesByDatastreamAndName("dor", "GoogleBooksWF", "ingest");
        assertEquals(2, count);
	}
	
	@Test
	public void findNoErrorDruids() {
		Process p = new Process();
		p.setDruid("dr:1234");
		p.setDatastream("GoogleBooksWF");
		p.setName("register-object");
		p.setStatus("completed");
        p.setRepository("dor");
		dao.persistProcess(p);
		
		List<Process> procs = dao.findErrorProcessesByDatastreamAndName("dor", "GoogleBooksWF", "ingest");
		assertEquals(0, procs.size());

        // Test count method
        int count = dao.countErrorProcessesByDatastreamAndName("dor", "GoogleBooksWF", "ingest");
        assertEquals(0, count);
	}

    @Test
	public void findCompletedLifecycleSteps() {
		createAndPersistProcess("dor", "dr:1234", "googleScannedBookWF", "ingest", "completed", "archived");
		createAndPersistProcess("dor", "dr:1234", "googleScannedBookWF", "shelve", "completed", "released");
		createAndPersistProcess("dor", "dr:1234", "googleScannedBookWF", "index",  "waiting");
        createAndPersistProcess("dor", "dr:1234", "googleScannedBookWF", "cleanup","waiting",   "accessioned");

		List<Process> procs = dao.findLifecycleCompletedProcesses("dor", "dr:1234");
		assertEquals(2, procs.size());
		assertEquals("dr:1234", procs.get(0).getDruid());
        assertEquals("archived", procs.get(0).getLifecycle());
        assertEquals("released", procs.get(1).getLifecycle());
	}

    @Test
	public void findNoLifecycleSteps() {
		createAndPersistProcess("dor", "dr:1234", "googleScannedBookWF", "ingest", "waiting", "archived");
		createAndPersistProcess("dor", "dr:1234", "googleScannedBookWF", "shelve", "waiting", "released");
		createAndPersistProcess("dor", "dr:1234", "googleScannedBookWF", "index",  "waiting");
        createAndPersistProcess("dor", "dr:1234", "googleScannedBookWF", "cleanup","waiting", "accessioned");

		List<Process> procs = dao.findLifecycleCompletedProcesses("dor", "dr:1234");
		assertEquals(0, procs.size());
	}

    // The WorkflowService.delete method should only delete the named workflow (datastream column)
    @Test
	public void deleteOnlyWorkflowThatIsSpecified() {
		createAndPersistProcess("dor", "dr:1234", "googleScannedBookWF", "ingest", "completed", "archived");
		createAndPersistProcess("dor", "dr:1234", "googleScannedBookWF", "shelve", "completed", "released");
		createAndPersistProcess("dor", "dr:1234", "googleScannedBookWF", "index",  "waiting");
        createAndPersistProcess("dor", "dr:1234", "googleScannedBookWF", "cleanup","waiting",   "accessioned");

        createAndPersistProcess("dor", "dr:1234", "etdAccessionWF", "pre-register","waiting",   "accessioned");


        dao.deleteProcessesByRepoDruidAndWorkflowName("dor", "dr:1234", "etdAccessionWF");

		List<Process> procs = dao.findProcessesByDruidAndDatastream("dor", "dr:1234", "googleScannedBookWF");
		assertEquals(4, procs.size());

        procs = dao.findProcessesByDruidAndDatastream("dor", "dr:1234", "etdAccessionWF");
        assertEquals(0, procs.size());
	}
	

}
