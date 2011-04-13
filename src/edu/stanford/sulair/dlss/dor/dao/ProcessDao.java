package edu.stanford.sulair.dlss.dor.dao;

import java.util.List;

import edu.stanford.sulair.dlss.dor.admin.Process;

public interface ProcessDao {
	List<Process> findProcessesByDruidAndDatastream(String repository, String druid, String datastream);
	
	void persistProcess(Process p);
	
	Process findProcess(String repository, String druid, String datastream, String name);
	
	void deleteProcessesByRepoDruidAndWorkflowName(String repository, String druid, String workflowName);
	
	List<String> findWaitingDruids(String repository, String datastream, String waitingProcessName);

	List<String> findWaitingDruidsByDatastreamNameAndCompleted(String repository, String datastream,
                                                               String waitingProcessName, String completedProcessName);
	
	List<String> findCompletedDruids(String repository, String datastream, String completedProcessName);

	List<Process> findErrorProcessesByDatastreamAndName(String repository, String datastream, String errorProcessName);

	List<String> findWaitingDruidsWithTwoCompletedProcesses(String repository, String datastream,
                                                            String waitingProcessName, String completed1, String completed2);

    List<Process> findLifecycleCompletedProcesses(String repository, String druid);

    List<String> findQueuedDruids(String repository, String datastream, String queuedProcessName);

    int countWaitingDruids(String repository, String datastream, String waitingProcessName);

    int countWaitingDruidsByDatastreamNameAndCompleted(String repository, String datastream,
                                                               String waitingProcessName, String completedProcessName);

    int countWaitingDruidsWithTwoCompletedProcesses(String repository, String datastream,
                                                            String waitingProcessName, String completed1, String completed2);

    int countCompletedDruids(String repository, String datastream, String completedProcessName);

    int countErrorProcessesByDatastreamAndName(String repository, String datastream, String errorProcessName);

    int countQueuedDruids(String repository, String datastream, String queuedProcessName);
}
