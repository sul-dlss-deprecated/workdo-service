package edu.stanford.sulair.dlss.dor.service;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.config.ApacheHttpClientConfig;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;

import edu.stanford.sulair.dlss.dor.DorRuntimeExeption;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FedoraRepositoryService implements RepositoryService {

    private static final Log LOG = LogFactory.getLog( RepositoryService.class );
	
	private String fedoraHostUri;
	
	private String workflowHostUri;
	
	private String fedoraUser;
	
	private String fedoraPassword;
	
	private String truststore;
	
	private String truststorePassword;
	
	private String keystore;
	
	private String keystorePassword;

	private String xsltHostUri;

	private DefaultApacheHttpClientConfig clientConfig;
	
	public FedoraRepositoryService(){}
	
	public void setFedoraHostUri(String host) {
		this.fedoraHostUri = host;
	}
	
	public void setWorkflowHostUri(String dorHost) {
		this.workflowHostUri = dorHost;
	}
	
	public void setFedoraUser(String fedoraUser) {
		this.fedoraUser = fedoraUser;
	}

	public void setFedoraPassword(String fedoraPassword) {
		this.fedoraPassword = fedoraPassword;
	}
	
	public void setTruststore(String truststore) {
		this.truststore = truststore; 
	}
	
	public void setTruststorePassword(String pword) {
		this.truststorePassword = pword;
	}
	
	public void setXsltHostUri(String xsltHost){
		this.xsltHostUri = xsltHost;
	}
	
	public void setKeystore(String ks) {
		this.keystore = ks;
	}

	public void setKeystorePassword(String ksPw) {
		this.keystorePassword = ksPw;
	}
	
	/*
	 * Called by Spring after all properties are set
	 * 
	 */
	public void init() {
        String curDir = System.getProperty("user.dir");
        LOG.info("Current working directory is: " + curDir);

        System.setProperty("javax.net.ssl.trustStore", truststore);
        System.setProperty("javax.net.ssl.trustStorePassword", truststorePassword);
        String file = System.getProperty("javax.net.ssl.trustStore");
        LOG.info("init() truststore file: " +
                    (file == null ? "" : file) );

		System.setProperty("javax.net.ssl.keyStore", keystore);
		System.setProperty("javax.net.ssl.keyStorePassword", keystorePassword);

		clientConfig = new  DefaultApacheHttpClientConfig();
		clientConfig.getState().setCredentials(null, null, -1, fedoraUser, fedoraPassword);
		clientConfig.getProperties().put(ApacheHttpClientConfig.PROPERTY_PREEMPTIVE_AUTHENTICATION, Boolean.TRUE);
	}

    public void createWorkflowDatastream(String druid, String workflowName){
		
		try{
            ApacheHttpClient c = createHttpClient();
	    	String url = fedoraHostUri + "/fedora/objects/" + druid + "/datastreams/" + workflowName + 
	    			"?controlGroup=E&versionable=false&dsLabel=Workflow&dsLocation=" + workflowHostUri + "/objects/" + druid + "/workflows/" + workflowName;
			WebResource resource = c.resource(url);
	    	ClientResponse r = resource.entity(" ", MediaType.APPLICATION_XML_TYPE).post(ClientResponse.class);
	    	if(r.getStatus() != Status.CREATED.getStatusCode() &&
                    r.getStatus() != Status.OK.getStatusCode()){
	    		throw new DorRuntimeExeption(r.getEntity(String.class));
	    	}
		} catch(Exception e){
			throw new DorRuntimeExeption(e);
		}
	}

    private ApacheHttpClient createHttpClient() {
        String file = System.getProperty("javax.net.ssl.trustStore");
        if(file == null || !file.equals(truststore)){
            LOG.info("Setting truststore to: " + truststore);
            System.setProperty("javax.net.ssl.trustStore", truststore);
            System.setProperty("javax.net.ssl.trustStorePassword", truststorePassword);
        }

        ApacheHttpClient c = ApacheHttpClient.create(clientConfig);
        return c;
    }


}
