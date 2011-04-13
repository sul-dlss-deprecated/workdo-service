package edu.stanford.sulair.dlss.dor.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import edu.stanford.sulair.dlss.dor.admin.Process;

public class HibernateProcessDao implements ProcessDao {
    private Logger logger = Logger.getLogger(HibernateProcessDao.class);
		
	private HibernateTemplate hibernateTemplate;
    private static final String WAITING_PROCESSES_QUERY = "from Process process where process.repository=? and process.datastream=? and process.name=? and process.status!=? and process.status!=?";
    private static final String WAITING_DRUIDS_BY_DATASTREAM_AND_COMPLETED_QUERY = "from Process process where process.repository=:repository and process.datastream=:ds and process.name=:waiting and process.status!=:errorStatus and process.status!=:completedStatus and process.status!=:queuedStatus and process.druid in (" +
            "select p.druid from Process p where p.repository=:repository and p.datastream=:ds and p.name=:completed and p.status=:completedStatus ) ";
    private static final String WAITING_DRUIDS_WITH_TWO_COMPLETED_QUERY = "from Process process where process.repository=:repository and process.datastream=:ds and process.name=:waiting and process.status!=:errorStatus and process.status!=:completedStatus and process.status!=:queuedStatus and process.druid in (" +
            "select p1.druid from Process p1 where p1.repository=:repository and p1.datastream=:ds and p1.name=:c1 and p1.status=:completedStatus and p1.druid in (" +
            "select p2.druid from Process p2 where p2.repository=:repository and p2.datastream=:ds and p2.name=:c2 and p2.status=:completedStatus)) ";
    private static final String SIMPLE_PROCESS_BY_STATUS_QUERY = "from Process process where process.repository=? and process.datastream=? and process.name=? and process.status=?";

    @Autowired
	public void setSessionFactory(SessionFactory sf) {
		hibernateTemplate = new HibernateTemplate(sf);
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Process> findProcessesByDruidAndDatastream(String repository, String druid, String datastream) {
		String[] parms = {repository, druid, datastream};
		return hibernateTemplate.find("from Process process where process.repository=? and process.druid=? and process.datastream=?", parms);
	}

	@Transactional(propagation = Propagation.NESTED )
	public void persistProcess(Process p) {
		this.hibernateTemplate.merge(p);
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public Process findProcess(String repository, String druid, String datastream, String name) {
		String[] parms = {repository, druid, datastream, name};
		List<Process> list = hibernateTemplate.find("from Process process where process.repository=? and process.druid=? and process.datastream=? and process.name=?", parms);
		if(list.size() == 0){
			return null;
		}
		//TODO: what to do if there are more than 1 items.  Throw exception? Log warning?
		return list.get(0);
			

	}

	@Transactional(propagation = Propagation.NESTED )
	public void deleteProcessesByRepoDruidAndWorkflowName(final String repository, final String druid, final String workflowName) {
		this.hibernateTemplate.execute( new HibernateCallback() {
				public Object doInHibernate(Session session)
						throws HibernateException, SQLException {
					String hql = "delete from Process where repository = :repository and druid = :druid and datastream = :workflowName";
			        Query query = session.createQuery(hql);
                    query.setString("repository", repository);
			        query.setString("druid", druid);
                    query.setString("workflowName", workflowName);
			        return query.executeUpdate();
				}
				
			}
		);
		
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<String> findWaitingDruids(String repository, String datastream,
                                       String waitingProcessName) {
		String[] parms = {repository, datastream, waitingProcessName, Process.STATUS_COMPLETED, Process.STATUS_ERROR};
		List<String> druids = hibernateTemplate.find("select process.druid " + WAITING_PROCESSES_QUERY, parms  );
		return druids;
	}

    @Transactional(readOnly = true)
    public int countWaitingDruids(String repository, String datastream, String waitingProcessName) {
        String[] parms = {repository, datastream, waitingProcessName, Process.STATUS_COMPLETED, Process.STATUS_ERROR};
        return DataAccessUtils.intResult(hibernateTemplate.find("select count(*) " + WAITING_PROCESSES_QUERY, parms)) ;
    }

    @Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<String> findWaitingDruidsByDatastreamNameAndCompleted(
            String repository, String datastream, String waitingProcessName, String completedProcessName) {
        logger.debug("In findWaitingDruidsByDatastreamNameAndCompleted ....");
		String[] names =  {"repository",  "ds",         "waiting",          "errorStatus",        "completed",          "completedStatus",         "queuedStatus"};
		String[] values = { repository,    datastream,   waitingProcessName, Process.STATUS_ERROR, completedProcessName, Process.STATUS_COMPLETED,  Process.STATUS_QUEUED};
		List<String> druids = hibernateTemplate.findByNamedParam("select distinct process.druid " + WAITING_DRUIDS_BY_DATASTREAM_AND_COMPLETED_QUERY, names, values  );
		return druids;
	}

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public int countWaitingDruidsByDatastreamNameAndCompleted(String repository, String datastream, String waitingProcessName, String completedProcessName) {
        String[] names =  {"repository",  "ds",         "waiting",          "errorStatus",        "completed",          "completedStatus",          "queuedStatus"};
		String[] values = { repository,    datastream,   waitingProcessName, Process.STATUS_ERROR, completedProcessName, Process.STATUS_COMPLETED,   Process.STATUS_QUEUED};
        return DataAccessUtils.intResult(hibernateTemplate.findByNamedParam("select distinct count(*) " + WAITING_DRUIDS_BY_DATASTREAM_AND_COMPLETED_QUERY, names, values)) ;
    }

    @Transactional(readOnly = true)
	public List<String> findCompletedDruids(String repository, String datastream,
                                         String completedProcessName) {
		String[] parms = {repository, datastream, completedProcessName, Process.STATUS_COMPLETED};
		List<String> druids = hibernateTemplate.find("select process.druid " + SIMPLE_PROCESS_BY_STATUS_QUERY, parms  );
		return druids;
	}

    @Transactional(readOnly = true)
    public int countCompletedDruids(String repository, String datastream, String completedProcessName) {
        String[] parms = {repository, datastream, completedProcessName, Process.STATUS_COMPLETED};
		return DataAccessUtils.intResult(hibernateTemplate.find("select count(*) " + SIMPLE_PROCESS_BY_STATUS_QUERY, parms  ));
    }

    @Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Process> findErrorProcessesByDatastreamAndName(
            String repository, String datastream, String errorProcessName) {
		String[] parms = {repository, datastream, errorProcessName, Process.STATUS_ERROR};
		return hibernateTemplate.find(SIMPLE_PROCESS_BY_STATUS_QUERY, parms);
	}

    @Transactional(readOnly = true)
    public int countErrorProcessesByDatastreamAndName(String repository, String datastream, String errorProcessName) {
        String[] parms = {repository, datastream, errorProcessName, Process.STATUS_ERROR};
        return DataAccessUtils.intResult(hibernateTemplate.find("select count(*) " + SIMPLE_PROCESS_BY_STATUS_QUERY, parms  ));
    }

    @Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<String> findWaitingDruidsWithTwoCompletedProcesses(
            String repository, String datastream, String waitingProcessName, String completed1,
            String completed2) {
        logger.debug("In findWaitingDruidsWithTwoCompletedProcesses");
		String[] names =  {"repository",  "ds",         "waiting",          "errorStatus",        "c1",       "c2",       "completedStatus",          "queuedStatus"};
		String[] values = { repository,    datastream,   waitingProcessName, Process.STATUS_ERROR, completed1, completed2, Process.STATUS_COMPLETED,   Process.STATUS_QUEUED};
		List<String> druids = hibernateTemplate.findByNamedParam("select distinct process.druid " + WAITING_DRUIDS_WITH_TWO_COMPLETED_QUERY, names, values  );
		return druids;
	}

    @Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
    public int countWaitingDruidsWithTwoCompletedProcesses(String repository, String datastream, String waitingProcessName, String completed1, String completed2) {
        String[] names =  {"repository",  "ds",         "waiting",          "errorStatus",        "c1",       "c2",       "completedStatus",          "queuedStatus"};
		String[] values = { repository,    datastream,   waitingProcessName, Process.STATUS_ERROR, completed1, completed2, Process.STATUS_COMPLETED,   Process.STATUS_QUEUED};
        return DataAccessUtils.intResult(hibernateTemplate.findByNamedParam("select distinct count(*) " + WAITING_DRUIDS_WITH_TWO_COMPLETED_QUERY, names, values));
    }

    public List<Process> findLifecycleCompletedProcesses(String repository, String druid) {
        String[] parms = {repository, druid, Process.STATUS_COMPLETED};
        return hibernateTemplate.find("from Process process " +
                                      "where process.repository=? and " +
                                      "process.druid=? and " +
                                      "process.lifecycle is not null and " +
                                      "process.status=? " +
                                      "order by process.datetime", parms);
    }

    @Transactional(readOnly = true)
    public List<String> findQueuedDruids(String repository, String datastream, String queuedProcessName) {
        String[] parms = {repository, datastream, queuedProcessName, Process.STATUS_QUEUED};
		List<String> druids = hibernateTemplate.find("select process.druid " + SIMPLE_PROCESS_BY_STATUS_QUERY, parms  );
		return druids;
    }

    @Transactional(readOnly = true)
    public int countQueuedDruids(String repository, String datastream, String queuedProcessName) {
        String[] parms = {repository, datastream, queuedProcessName, Process.STATUS_QUEUED};
		return DataAccessUtils.intResult(hibernateTemplate.find("select count(*) " + SIMPLE_PROCESS_BY_STATUS_QUERY, parms  ));
    }

}
