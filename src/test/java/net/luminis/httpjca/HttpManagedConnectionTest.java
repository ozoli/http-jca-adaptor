package net.luminis.httpjca;

import org.apache.http.HttpClientConnection;
import org.apache.http.HttpHost;
import org.apache.http.conn.ConnectionRequest;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.junit.Before;
import org.junit.Test;

import javax.resource.ResourceException;
import javax.resource.spi.ManagedConnectionMetaData;

import java.util.concurrent.TimeUnit;

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
}
