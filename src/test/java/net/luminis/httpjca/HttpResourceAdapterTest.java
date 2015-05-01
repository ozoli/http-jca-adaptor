package net.luminis.httpjca;

import org.junit.Before;
import org.junit.Test;

import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.InvalidPropertyException;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;

import static org.junit.Assert.*;

/**
 * Unit test for the {@link HttpResourceAdapter} class.
 */
public class HttpResourceAdapterTest {

  private final HttpResourceAdapter resourceAdapter = new HttpResourceAdapter();

  @Before
  public void setup() {
    resourceAdapter.setHostUrl("http://www.luminis.eu");
    resourceAdapter.setHostPort(8080);
  }

  @Test
  public void testEquals() {
    assertEquals("expected to be equal", resourceAdapter, resourceAdapter);
  }

  @Test
  public void testNotEquals() {
    assertNotEquals("expected not to be equal", null, resourceAdapter);
    assertNotEquals("expected not to be equal", resourceAdapter, null);
    assertNotEquals("expected not to be equal", 4, resourceAdapter);
    assertNotEquals("expected not to be equal", new HttpResourceAdapter(), resourceAdapter);
  }

  @Test
  public void testHashcode() {
    assertEquals("expected equal", resourceAdapter.hashCode(), resourceAdapter.hashCode());
    assertNotEquals("expected not to be equal", 4, resourceAdapter.hashCode());
    assertNotEquals("expected not to be equal", new HttpResourceAdapter(), resourceAdapter.hashCode());
  }
  
  @Test
  public void basicOperation() throws ResourceAdapterInternalException {
    resourceAdapter.start(null);
    resourceAdapter.stop();
  }
  
  @Test
  public void testXAException() throws ResourceException {
    assertNull("expected null", resourceAdapter.getXAResources(null));
  }

  @Test
  public void testEndpointActivationDecactivateion() throws ResourceException {
    ActivationSpecTest specTest = new ActivationSpecTest();
    resourceAdapter.endpointActivation(null, specTest);
    resourceAdapter.endpointDeactivation(null, specTest);
    assertNotNull("expected not null", specTest);
  }

  class ActivationSpecTest implements ActivationSpec {
    @Override
    public void validate() throws InvalidPropertyException {
    }

    @Override
    public ResourceAdapter getResourceAdapter() {
      return null;
    }

    @Override
    public void setResourceAdapter(ResourceAdapter ra) throws ResourceException {
    }
  }
}
