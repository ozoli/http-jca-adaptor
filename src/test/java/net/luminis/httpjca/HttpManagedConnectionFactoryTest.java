package net.luminis.httpjca;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.resource.ResourceException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Unit test for the {@link HttpManagedConnectionFactory} class.
 */
public class HttpManagedConnectionFactoryTest {
  
  private final HttpManagedConnectionFactory connectionFactory = new HttpManagedConnectionFactory();
  
  private final File logWritierFile = new File("aFile");
  
  @Before
  public void setup() {
    connectionFactory.setHost("http://www.luminis.eu");
    connectionFactory.setPort(8080);
    connectionFactory.setResourceAdapter(new HttpResourceAdapter());
  }

  @Test
  public void testEquals() {
    assertEquals("expected equal", connectionFactory, connectionFactory);
    assertNotEquals("expected equal", null, connectionFactory);
    assertNotEquals("expected equal", 4, connectionFactory);
  }

  @Test
  public void testHashCode() {
    assertEquals("expected equal", connectionFactory.hashCode(), connectionFactory.hashCode());
    assertNotEquals("expected equal", new HttpManagedConnectionFactory().hashCode(), 
                                      connectionFactory.hashCode());
    assertNotEquals("expected equal", 4, connectionFactory.hashCode());
  }
  
  @Test(expected = ResourceException.class)
  public void testNonManageed() throws ResourceException {
    connectionFactory.createConnectionFactory();
  }
  
  @Test
  public void testLogWriter() throws ResourceException, IOException {
    PrintWriter writer = new PrintWriter(new FileWriter(logWritierFile));
    connectionFactory.setLogWriter(writer);
    assertEquals("expected same LogWriter", writer, connectionFactory.getLogWriter());
  }

  @After
  public void cleanup() throws IOException {
    FileUtils.deleteQuietly(logWritierFile);
  }
}