package eu.luminis.httpjca;

import org.junit.Before;
import org.junit.Test;

import javax.resource.ResourceException;
import javax.resource.spi.ManagedConnectionMetaData;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link HttpManagedConnection}.
 */
public class HttpManagedConnectionTest {
  
  private HttpManagedConnection managedConnection;
  
  @Before
  public void setup() throws ResourceException {
    managedConnection = new HttpManagedConnection(new HttpManagedConnectionFactory());
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
