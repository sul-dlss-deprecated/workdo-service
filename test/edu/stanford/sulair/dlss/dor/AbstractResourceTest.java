package edu.stanford.sulair.dlss.dor;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.springframework.web.context.ContextLoaderServlet;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;


public class AbstractResourceTest {
    
    public static final String APPLICATION_CONTEXT_SPRING25_XML = "applicationContext-spring25.xml";

    private static final Log LOG = LogFactory.getLog( AbstractResourceTest.class );

    protected String _springConfig;
    protected String _resourcePackages;
    private final int _port;
    private final String _servletPath;
    
    private Server _server;
    
    public AbstractResourceTest() {
        //_springConfig = System.getProperty( "applicationContext", APPLICATION_CONTEXT_SPRING25_XML );
        _resourcePackages = System.getProperty( "resourcePackages", "edu.stanford.sulair.dlss.dor" );
        _port = 9999;
        _servletPath = "/jersey-spring";
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        startJetty( _port, _servletPath );
    }
    
    private void startJetty( int port, String servletPath ) throws Exception {
        LOG.info( "Starting jetty on port " + port + "..." );

         _server = new Server(port);
         final Context context = new Context(_server, "/", Context.SESSIONS);
         
         final Map<String,String> contextParams = new HashMap<String, String>();
         contextParams.put( "contextConfigLocation", "classpath:" + _springConfig );
         context.setInitParams( contextParams );
         
         
         final ServletHolder springServletHolder = new ServletHolder( ContextLoaderServlet.class );
         
         springServletHolder.setInitOrder( 1 );
         context.addServlet( springServletHolder, "/*" );
         
         
         final ServletHolder sh = new ServletHolder(SpringServlet.class);
         sh.setInitParameter( "com.sun.jersey.config.property.resourceConfigClass",
                 PackagesResourceConfig.class.getName() );
         sh.setInitParameter( PackagesResourceConfig.PROPERTY_PACKAGES,
                 _resourcePackages );
         sh.setInitOrder( 2 );
         context.addServlet(sh, servletPath + "/*");
         
         _server.start();
         LOG.info( "Successfully started jetty." );
    }
    
    private void stopJetty() throws Exception {
        try {
            _server.stop();
        } catch( Exception e ) {
            LOG.warn( "Could not stop jetty...", e );
        }
    }
    
    @After
    public void tearDown() throws Exception {
        LOG.info( "tearDown..." );
        stopJetty();
        LOG.info( "done..." );
    }

    public WebResource resource( final String path ) {
        final Client c = Client.create();
        final WebResource rootResource = c.resource( getResourcePath( path ) );
        return rootResource;
    }

    public String getResourcePath( final String path ) {
        return "http://localhost:" + _port + _servletPath + "/" + path;
    }

}
