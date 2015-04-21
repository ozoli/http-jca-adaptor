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

import static org.junit.Assert.*;

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
  public void testGetConnection() throws ResourceException, IOException, HttpException {
    org.apache.http.HttpConnection connection =
        (org.apache.http.HttpConnection) managedConnection.getConnection(null, null);
    assertNotNull("HTTP Connection should not be null", connection);

    HttpConnection httpConnection = (HttpConnection) managedConnection.getConnection(null, null);
    assertNotNull("HTTP Connection should not be null", httpConnection);
    assertNotNull("HTTP Connection Metrics should not be null", httpConnection.getMetrics());
  }

  @Test(expected = ResourceException.class)
  public void associateConnectionNull() throws ResourceException {
    managedConnection.associateConnection(null);
  }

  @Test(expected = ResourceException.class)
  public void associateConnectionInteger() throws ResourceException {
    managedConnection.associateConnection(Integer.valueOf(2));
  }

  @Test
  public void associateConnection() throws ResourceException {
    HttpConnection httpConnection = (HttpConnection) managedConnection.getConnection(null, null);
    managedConnection.associateConnection(httpConnection);
  }

  @Test
  public void testCleanup() throws ResourceException {
    managedConnection.cleanup();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddNulConnectionListener() throws IllegalArgumentException {
    managedConnection.addConnectionEventListener(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRemoveNulConnectionListener() throws IllegalArgumentException {
    managedConnection.removeConnectionEventListener(null);
  }
  
  @Test
  public void testConnectionEventListener() throws IOException, ResourceException {
    ConnectionEventListener listener = new TestConnectionEventListener();
    managedConnection.addConnectionEventListener(listener);
    assertFalse("connection should not be open",
        managedConnection.getHttpConnection().isOpen());

    HttpConnection httpConnection = (HttpConnection) managedConnection.getConnection(null, null);
    managedConnection.closeHandle(httpConnection);

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
