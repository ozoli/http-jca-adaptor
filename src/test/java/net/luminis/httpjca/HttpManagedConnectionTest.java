package net.luminis.httpjca;

import net.luminis.httpjca.util.HttpServerBase;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionEvent;
import javax.resource.spi.ConnectionEventListener;
import javax.resource.spi.ManagedConnectionMetaData;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Unit test for {@link HttpManagedConnection}.
 */
public class HttpManagedConnectionTest extends HttpServerBase {
  
  private HttpManagedConnection managedConnection;
  
  private HttpConnection connection;
  private CloseableHttpClient client = HttpClients.custom().setConnectionManager(
      new BasicHttpClientConnectionManager()).build();

  @BeforeClass
  public static void start() {
    buildHttpServer();
    startHttpServer();
  }
     
  @Before
  public void setup() throws Exception {
    managedConnection = new HttpManagedConnection(new HttpManagedConnectionFactory(), client);
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
  public void testGet() throws Exception {
    Object object = managedConnection.getConnection(null, null);
    assertTrue("incorrect HttpConnection class", object instanceof HttpConnection);
    assertTrue("incorrect HttpConnectionImpl class", object instanceof HttpConnectionImpl);
    connection = (HttpConnectionImpl) object;
    assertNotNull("http connection should not be null", connection);

    HttpHost target = new HttpHost(host, port);
    HttpResponse response = connection.execute(target, createGetRequestEntity());

    assertEquals("expected 200 OK", 200, response.getStatusLine().getStatusCode());
    assertTrue("expected Hello World",
        IOUtils.toString(response.getEntity().getContent()).contains("Hello World"));

    connection.close();
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
  
  @AfterClass
  public static void stop() {
    stopHttpServer();
  }
}
