package edu.stanford.sulair.dlss.dor;

import edu.stanford.sulair.dlss.dor.admin.*;
import edu.stanford.sulair.dlss.dor.admin.Process;
import edu.stanford.sulair.dlss.dor.dao.ProcessDao;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

/**
 * User: wmene
 * Date: Jun 14, 2010
 * Time: 2:08:09 PM
 */
public abstract class AbstractProcessDatabaseTest extends AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    protected ProcessDao dao;
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

    protected void createAndPersistProcess(String repository, String druid, String ds, String name, String status){
		edu.stanford.sulair.dlss.dor.admin.Process p = createProcess(repository, druid, ds, name, status);
		dao.persistProcess(p);
	}

    protected void createAndPersistProcess(String repository, String druid, String ds, String name, String status, String lifecycle){
		Process p = new Process();
		p.setDruid(druid);
		p.setDatastream(ds);
		p.setName(name);
		p.setStatus(status);
        p.setRepository(repository);
        p.setLifecycle(lifecycle);
		dao.persistProcess(p);
	}

    protected Process createProcess(String repository, String druid, String ds, String name, String status){
        edu.stanford.sulair.dlss.dor.admin.Process p = new Process();
		p.setDruid(druid);
		p.setDatastream(ds);
		p.setName(name);
		p.setStatus(status);
        p.setRepository(repository);
        return p;
    }

}
