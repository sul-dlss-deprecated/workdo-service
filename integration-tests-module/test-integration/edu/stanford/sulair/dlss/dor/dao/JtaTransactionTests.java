package edu.stanford.sulair.dlss.dor.dao;

import java.net.URI;

import javax.transaction.Transaction;
import javax.transaction.xa.XAResource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mulgara.connection.Connection;
import org.mulgara.connection.ConnectionFactory;
import org.mulgara.itql.TqlInterpreter;
import org.mulgara.query.operation.Command;
import org.mulgara.server.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.jta.JtaTransactionManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class JtaTransactionTests {
	private static final Log LOG = LogFactory.getLog( JtaTransactionTests.class );
	
	@Autowired
	private JtaTransactionManager jtam;
	
	@Test
	public void jtaTransaction() throws Exception{

		javax.transaction.TransactionManager tm = jtam.getTransactionManager();
		Connection conn = null;
		Session session = null;
		boolean rollback = false; 
		try {
			tm.setTransactionTimeout ( 60 );
			//GENERIC: begin and retrieve tx 
			tm.begin(); 
			Transaction tx = tm.getTransaction(); 
			//SPECIFIC FOR JDBC: get the XAResourc from the JDBC connection 


			URI serverURI = new URI("rmi://localhost/server1");

			// Create a factory, and connect to the server
			ConnectionFactory factory = new ConnectionFactory();
			conn = factory.newConnection(serverURI);
			session = conn.getSession();



			XAResource xares = session.getXAResource(); 
			//GENERIC: enlist the resource with the transaction 
			//NOTE: this will only work if you set the configuration parameter: 
			//com.atomikos.icatch.automatic_resource_registration=true 
			//or, alternatively, if you use the UserTransactionService 
			//integration mode explained later 
			tx.enlistResource ( xares ); 


			String insertTql = "insert <http://something.com/IDabc000> <dc:title> 'spring exception' into <dor:data> ;";
			LOG.debug("Storing: " + insertTql);

			// parse the string into a Command object
			TqlInterpreter interpreter = new TqlInterpreter();
			Command cmd = interpreter.parseCommand(insertTql);

			// execute the command
			String a = conn.execute(cmd);

			// print the results
			System.out.println("Result: " + cmd.getResultMessage());
			System.out.println("Answer: " + a);

			System.out.println("---");

			stepTwoFails();


			

			//GENERIC: delist the resource 
			tx.delistResource ( xares , XAResource.TMSUCCESS ); 
		} 
		catch ( Exception e ) { 
			rollback = true; 
			throw e; 
		} 
		finally { 
			//GENERIC: ALWAYS terminate the tx 
			if ( rollback ) tm.rollback(); 
			else tm.commit(); 
			//SPECIFIC FOR JDBC: only now close the connection 
			//i.e., not until AFTER commit or rollback! 
			// clean up the server connection
			if(session != null)
				session.close();
			if(conn!=null)
				conn.dispose(); 
		} 

	}

	private void stepTwoFails() throws Exception {
		Thread.sleep(10000);
		
	}
}
