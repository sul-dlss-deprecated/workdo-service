package edu.stanford.sulair.dlss.dor.dao.mock;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.stanford.sulair.dlss.dor.admin.Process;
import edu.stanford.sulair.dlss.dor.dao.ProcessDao;

public class ProcessDaoMock implements ProcessDao {

	public void deleteProcessesByRepoDruidAndWorkflowName(String repository, String druid, String workflowName) {
		// TODO Auto-generated method stub

	}

    // Used by WorkflowResourceTests.testGetWorkflowProcessStep
    // dor/objects/pid:123/workflows/googleScannedWF/convert
    // <process name="convert" status="waiting" datetime="2008.11.15 13:30:00 PST"/>
	public Process findProcess(String repository, String druid, String datastream, String name) {
	    if(name.equals("convert")){
            Process p = new Process();
            p.setRepository(repository);
            p.setDruid(druid);
            p.setDatastream(datastream);
            p.setName(name);
            p.setStatus("waiting");
            return p;
        }

        return null;
	}

	public List<Process> findProcessesByDruidAndDatastream(String repository, String druid, String datastream) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> findWaitingDruids(String repository, String datastream,
                                          String waitingProcessName) {
		List<String> list = createResponseList();
		return list;
	}

	private List<String> createResponseList() {
		List<String>list = new ArrayList<String>();
		list.add("dr:123");
		list.add("dr:abc");
		return list;
	}

	public List<String> findWaitingDruidsByDatastreamNameAndCompleted(
            String repository, String datastream, String waitingProcessName,
            String completedProcessName) {
        if(waitingProcessName.equals("no-objects-found"))
            return new ArrayList<String>();
        else
		    return createResponseList();
	}

	public void persistProcess(Process p) {
		// TODO Auto-generated method stub

	}

	public List<String> findCompletedDruids(String repository, String datastream,
                                            String completedProcessName) {
		List<String> list = createResponseList();
		return list;
	}

	//<object druid=\"dr:123\" url=\"http://localhost:9999/jersey-spring/objects/dr:123\" errorMessage=\"NullPointer\" errorText=\"some trace\" />
	//<object druid=\"dr:abc\" url=\"http://localhost:9999/jersey-spring/objects/dr:abc\" errorMessage=\"NullPointer\" />
	public List<Process> findErrorProcessesByDatastreamAndName(
            String repository, String datastream, String errorProcessName) {
		if(datastream.equals("noError") || errorProcessName.equals("noError")){
			return new ArrayList<Process>();
		}
		Process e1 = new Process();
		e1.setDruid("dr:123");
		e1.setErrorMessage("NullPointer");
		e1.setErrorText("some trace");
		List<Process> l = new ArrayList<Process>();
		l.add(e1);
		e1 = new Process();
		e1.setDruid("dr:abc");
		e1.setErrorMessage("NullPointer");
		l.add(e1);
		return l;
	}

	public List<String> findWaitingDruidsWithTwoCompletedProcesses(
            String repository, String datastream, String waitingProcessName, String completed1,
            String completed2) {
		return createResponseList();
	}

    public List<Process> findLifecycleCompletedProcesses(String repository, String druid) {
        try {
            List<Process> procs = new ArrayList<Process>();
            if(druid.equals("dr:nolifecycle123")){
                return procs;
            }
            
            Process p = new Process();
            p.setDruid("dr:123");
            p.setLifecycle("registered");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss z");
            Date d = sdf.parse("2010.11.15 13:30:00 PST");
            p.setDatetime(d);
            procs.add(p);
            p = new Process();
            p.setDruid("dr:123");
            p.setLifecycle("inprocess");
            d = sdf.parse("2010.11.16 13:30:00 PST");
            p.setDatetime(d);
            procs.add(p);
            p = new Process();
            p.setDruid("dr:123");
            p.setLifecycle("released");
            d = sdf.parse("2010.11.17 13:30:00 PST");
            p.setDatetime(d);
            procs.add(p);

            return procs;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public List<String> findQueuedDruids(String repository, String datastream, String queuedProcessName) {
        List<String> list = createResponseList();
		return list;
    }

    public int countWaitingDruids(String repository, String datastream, String waitingProcessName) {
        return 2;
    }

    public int countWaitingDruidsByDatastreamNameAndCompleted(String repository, String datastream, String waitingProcessName, String completedProcessName) {
        if(waitingProcessName.equals("no-objects-found"))
            return 0;
        else
            return 3;
    }

    public int countWaitingDruidsWithTwoCompletedProcesses(String repository, String datastream, String waitingProcessName, String completed1, String completed2) {
        return 2;
    }

    public int countCompletedDruids(String repository, String datastream, String completedProcessName) {
        return 2;
    }

    public int countErrorProcessesByDatastreamAndName(String repository, String datastream, String errorProcessName) {
        return 2;
    }

    public int countQueuedDruids(String repository, String datastream, String queuedProcessName) {
        return 3;  //To change body of implemented methods use File | Settings | File Templates.
    }


    /***
     * "  <milestone date=\"somedate\">registered</milestone>" +
                          "  <milestone date=\"anotherdate\">inprocess</milestone>" +
                          "  <milesteon date=\"anotherone\">released</milestone>"
     */
}
