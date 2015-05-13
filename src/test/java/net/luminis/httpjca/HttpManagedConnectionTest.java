package net.luminis.httpjca;

import net.luminis.httpjca.util.HttpServerBase;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.spi.*;
import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Unit test for {@link HttpManagedConnection}.
 */
public class HttpManagedConnectionTest extends HttpServerBase {
  
  private HttpManagedConnection managedConnection;
  
  private HttpConnection connection;
  private CloseableHttpClient client = HttpClients.custom().setConnectionManager(
      new BasicHttpClientConnectionManager()).build();
  private final File logWritierFile = new File("aFile");
  private String host;
  private int port;

  @Before
  public void start() throws ResourceException {
    host = System.getProperty("UNDERTOW_HTTP_HOST");
    port = Integer.valueOf(System.getProperty("UNDERTOW_HTTP_PORT"));
    buildHttpServer(host, port);
    startHttpServer();
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

  @Test(expected = NotSupportedException.class)
  public void testLocalTransaction() throws ResourceException {
    managedConnection.getLocalTransaction();
  }

  @Test
  public void testLogWriter() throws FileNotFoundException, ResourceException {
    managedConnection.setLogWriter(new PrintWriter(logWritierFile));
    assertNotNull("expected a log writer", managedConnection.getLogWriter());
  }

  @Test
  public void testConnectionEventListener() throws IOException, ResourceException {
    ConnectionEventListener listener = new TestConnectionEventListener();
    managedConnection.addConnectionEventListener(listener);

    HttpConnection httpConnection = (HttpConnection) managedConnection.getConnection(null, null);
    managedConnection.closeHandle(httpConnection);

    managedConnection.removeConnectionEventListener(listener);
  }

  @Test
  public void testMatchConnections() throws ResourceException {
    Set<ManagedConnection> matchConnections = new HashSet<>();
    matchConnections.add(managedConnection);
    HttpManagedConnectionFactory factory = new HttpManagedConnectionFactory();
    assertNull("expect null", factory.matchManagedConnections(new HashSet<>(), null, null));

    ManagedConnection connection = factory.matchManagedConnections(matchConnections, null, null);
    assertNotNull("expect not null", connection);
    assertTrue("wrong instance", connection instanceof HttpManagedConnection);
    HttpManagedConnection httpManagedConnection = (HttpManagedConnection) connection;
    assertEquals("wrong instance", managedConnection, httpManagedConnection);

    matchConnections.add(new TesManagedConnection());
    connection = factory.matchManagedConnections(matchConnections, null, null);
    assertNotNull("expect not null", connection);
    assertTrue("wrong instance", connection instanceof HttpManagedConnection);
    httpManagedConnection = (HttpManagedConnection) connection;
    assertEquals("wrong instance", managedConnection, httpManagedConnection);
  }

  @Test
  public void testMatchConnectionsGoodAndBad() throws ResourceException {
    Set<ManagedConnection> matchConnections = new HashSet<>();
    matchConnections.add(managedConnection);
    matchConnections.add(new TesManagedConnection());
    HttpManagedConnectionFactory factory = new HttpManagedConnectionFactory();
    ManagedConnection connection = factory.matchManagedConnections(matchConnections, null, null);
    assertNotNull("expect not null", connection);
    // expdct a HttpManagedConnection since it is first in the collection.
    assertTrue("wrong instance", connection instanceof HttpManagedConnection);
  }

  @After
  public void testDestroy() throws ResourceException {
    managedConnection.destroy();
    FileUtils.deleteQuietly(logWritierFile);
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

  class TesManagedConnection implements ManagedConnection {

    @Override
    public Object getConnection(Subject subject, ConnectionRequestInfo cxRequestInfo) throws ResourceException {
      return null;
    }

    @Override
    public void destroy() throws ResourceException {

    }

    @Override
    public void cleanup() throws ResourceException {

    }

    @Override
    public void associateConnection(Object connection) throws ResourceException {

    }

    @Override
    public void addConnectionEventListener(ConnectionEventListener listener) {

    }

    @Override
    public void removeConnectionEventListener(ConnectionEventListener listener) {

    }

    @Override
    public XAResource getXAResource() throws ResourceException {
      return null;
    }

    @Override
    public LocalTransaction getLocalTransaction() throws ResourceException {
      return null;
    }

    @Override
    public ManagedConnectionMetaData getMetaData() throws ResourceException {
      return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws ResourceException {

    }

    @Override
    public PrintWriter getLogWriter() throws ResourceException {
      return null;
    }
  }

  @After
  public void stop() {
    stopHttpServer();
  }
}
