package net.luminis.httpjca;

import org.apache.http.*;
import org.apache.http.conn.ConnectionRequest;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.junit.Before;
import org.junit.Test;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionEvent;
import javax.resource.spi.ConnectionEventListener;
import javax.resource.spi.ManagedConnectionMetaData;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link HttpManagedConnection}.
 */
public class HttpManagedConnectionTest {
  
  private HttpManagedConnection managedConnection;
  
  private BasicHttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager();
  
  @Before
  public void setup() throws Exception {
    ConnectionRequest request = connectionManager.requestConnection(
        new HttpRoute(new HttpHost("localhost", 8080)), null);
    HttpClientConnection connection = request.get(100, TimeUnit.SECONDS);
    managedConnection = new HttpManagedConnection(new HttpManagedConnectionFactory(), connection);
  }

  @Test(expected = ResourceException.class)
  public void testGetXaResource() throws ResourceException {
    managedConnection.getXAResource();
  }
  
  @Test
  public void testMetaData() throws ResourceException {
    ManagedConnectionMetaData metaData = managedConnection.getMetaData();
    assertTrue("MetaData is of wrong type", metaData instanceof HttpManagedConnectionMetaData);
  }
  
  @Test
  public void testGetConnection() throws ResourceException {
    org.apache.http.HttpConnection connection = 
        (org.apache.http.HttpConnection) managedConnection.getConnection(null, null);
    assertNotNull("HTTP Connection should not be null", connection);

    HttpConnection httpConnection = (HttpConnection) managedConnection.getConnection(null, null);
    assertNotNull("HTTP Connection should not be null", httpConnection);
    httpConnection.close();
  }

  @Test
  public void testCleanup() throws ResourceException {
    managedConnection.cleanup();
  }
  
  @Test
  public void testConnectionEventListener() throws IOException {
    ConnectionEventListener listener = new TestConnectionEventListener();
    managedConnection.addConnectionEventListener(listener);
    assertFalse("connection should not be open",
        managedConnection.getHttpConnection().isOpen());
    managedConnection.removeConnectionEventListener(listener);
  }
  
  class TestConnectionEventListener implements ConnectionEventListener {

    @Override
    public void connectionClosed(ConnectionEvent event) {
      
    }

    @Override
    public void localTransactionStarted(ConnectionEvent event) {

    }

    @Override
    public void localTransactionCommitted(ConnectionEvent event) {

    }

    @Override
    public void localTransactionRolledback(ConnectionEvent event) {

    }

    @Override
    public void connectionErrorOccurred(ConnectionEvent event) {

    }
  }
}
