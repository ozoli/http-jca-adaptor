package eu.luminis.httpjca;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import javax.resource.ResourceException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for the {@link HttpManagedConnectionMetaDataTest} class.
 */
public class HttpManagedConnectionMetaDataTest {
  
  private final HttpManagedConnectionMetaData metaData = new HttpManagedConnectionMetaData();
  
  @Test
  public void testProductName() throws ResourceException {
    assertTrue("expected not empty", StringUtils.isNotEmpty(metaData.getEISProductName()));
  }

  @Test
  public void testProductVersion() throws ResourceException {
    assertTrue("expected not empty", StringUtils.isNotEmpty(metaData.getEISProductVersion()));
  }
  
  @Test
  public void testUserName() throws ResourceException {
    assertNull("expected null", metaData.getUserName());
  }

  @Test
  public void testMaxConnections() throws ResourceException {
    assertEquals("expected 0", 0, metaData.getMaxConnections());
  }
}
