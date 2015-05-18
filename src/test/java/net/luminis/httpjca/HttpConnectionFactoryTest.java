package net.luminis.httpjca;

import org.junit.Test;

import javax.naming.NamingException;
import javax.naming.Reference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit test for {@link HttpConnectionFactoryImpl}.
 */
public class HttpConnectionFactoryTest {
  
  @Test
  public void defaultConstructor() {
    assertNotNull("default constructor null", new HttpConnectionFactoryImpl());
  }

  @Test
  public void reference() throws NamingException {
    HttpConnectionFactory connectionFactory = new HttpConnectionFactoryImpl();
    Reference ref = new Reference("ref");
    connectionFactory.setReference(ref);
    assertEquals("reference not equal", ref, connectionFactory.getReference());
  }

}
