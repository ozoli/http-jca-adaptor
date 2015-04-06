package eu.luminis.httpjca;

import org.junit.Before;
import org.junit.Test;

import javax.resource.ResourceException;
import javax.resource.spi.ResourceAdapterInternalException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

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
    assertNotEquals("expected not to be equal", 4, resourceAdapter);
    assertNotEquals("expected not to be equal", new HttpResourceAdapter(), resourceAdapter);
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
}
