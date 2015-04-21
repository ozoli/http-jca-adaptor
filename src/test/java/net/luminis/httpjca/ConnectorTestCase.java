/*
 * IronJacamar, a Java EE Connector Architecture implementation
 * Copyright 2013, Red Hat Inc, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package net.luminis.httpjca;

import java.util.UUID;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.resource.ResourceException;

import io.undetow.server.HttpServerBaseTest;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * ConnectorTestCase
 *
 * @version $Revision: $
 */
@RunWith(Arquillian.class)
public class ConnectorTestCase
{
  private static final Logger LOG = Logger.getLogger(ConnectorTestCase.class.getName());

  private static HttpServerBaseTest httpServerBaseTest;

  private static String host = System.getProperty("undertow.http.host", "localhost");
  private static int port = Integer.valueOf(System.getProperty("undertow.http.port", "8180"));

  @BeforeClass
  public static void setup() {
    LOG.info("Starting HTTP Server host " + host + ":" + port);
    httpServerBaseTest = new HttpServerBaseTest(host, port);
    httpServerBaseTest.start();
  }
  
  @Resource(mappedName = "java:/eis/HttpConnectionFactory")
  private HttpConnectionFactory connectionFactory;
  
  /**
    * Define the deployment
    *
    * @return The deployment archive
    */
   @Deployment
   public static EnterpriseArchive createDeployment()
   {
      ResourceAdapterArchive raa =
         ShrinkWrap.create(ResourceAdapterArchive.class, "ConnectorTestCase.rar");
      JavaArchive ja = ShrinkWrap.create(JavaArchive.class, UUID.randomUUID().toString() + ".jar");
      ja.addPackages(true, Package.getPackage(HttpConnection.class.getPackage().getName()));
      raa.addAsLibrary(ja);

      raa.addAsManifestResource("META-INF/ironjacamar.xml", "ironjacamar.xml");

      JavaArchive libjar = ShrinkWrap.create(JavaArchive.class, "lib.jar")
         .addClasses(ConnectorTestCase.class)
         .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

      return ShrinkWrap.create(EnterpriseArchive.class, "test.ear")
         .addAsModules(raa)
         .addAsLibraries(libjar)
         .addAsLibraries(
             Maven.resolver().resolve("org.apache.commons:commons-lang3:3.3.2")
                 .withTransitivity().asFile())
         .addAsLibraries(
             Maven.resolver().resolve("org.apache.httpcomponents:httpclient:4.4")
                 .withTransitivity().asFile())
         .addAsLibraries(
             Maven.resolver().resolve("org.apache.httpcomponents:httpcore:4.4")
                 .withTransitivity().asFile());
   }

  @Test
  public void testNotNullConnectionFactory() throws ResourceException {
    assertNotNull("connection factory should not be null", connectionFactory);
    assertNotNull("http connection should not be null", connectionFactory.getConnection());
  }
  
  @Test
  public void testGet() throws Exception {
    HttpConnection connection = connectionFactory.getConnection();
    assertNotNull("http connection should not be null", connection);
    
    connection.sendRequestEntity(createRequestEntity());
    HttpResponse response = connection.receiveResponseHeader();
    
    assertEquals("expected 200 OK", 200, response.getStatusLine().getStatusCode());
    assertTrue("expected Hello World",
        IOUtils.toString(response.getEntity().getContent()).contains("Hello World"));

    assertTrue("expect isOpen", connection.isOpen());
    assertFalse("expect isFalse", connection.isStale());

    assertNotNull("expect metrics not null", connection.getMetrics());
    assertTrue("expect at least one request",
        connection.getMetrics().getRequestCount() > 0);
    assertTrue("expect some received bytes",
        connection.getMetrics().getReceivedBytesCount() > 0);
    assertTrue("expect at least one response",
        connection.getMetrics().getResponseCount() > 0);
    assertTrue("expect some sent bytes",
        connection.getMetrics().getSentBytesCount() > 0);
    connection.flush();
    connection.close();
  }

  @Test
  public void testBadGet() throws Exception {
    HttpConnection connection = connectionFactory.getConnection();
    assertNotNull("http connection should not be null", connection);

    connection.sendRequestEntity(
        new BasicHttpEntityEnclosingRequest("GET", "http://silly.host:98"));
    HttpResponse response = connection.receiveResponseHeader();

    assertEquals("expected 404", 404, response.getStatusLine().getStatusCode());

    assertTrue("expect isOpen", connection.isOpen());
    assertFalse("expect isFalse", connection.isStale());

    assertNotNull("expect metrics not null", connection.getMetrics());
    assertTrue("expect at least one request",
        connection.getMetrics().getRequestCount() > 0);
    assertTrue("expect some received bytes",
        connection.getMetrics().getReceivedBytesCount() > 0);
    assertTrue("expect at least one response",
        connection.getMetrics().getResponseCount() > 0);
    assertTrue("expect some sent bytes",
        connection.getMetrics().getSentBytesCount() > 0);
    connection.flush();
    connection.close();
  }
  
  @Test
  public void testShutdown() throws Exception {
    HttpConnection connection = connectionFactory.getConnection();

    connection.sendRequestEntity(createRequestEntity());
    HttpResponse response = connection.receiveResponseHeader();

    assertEquals("expected 200 OK", 200, response.getStatusLine().getStatusCode());

    connection.shutdown();
  }
  
  @Test
  public void testSocketTimeout() throws Exception {
    HttpConnection connection = connectionFactory.getConnection();
    connection.setSocketTimeout(2000);
    assertEquals("incorrect socket timeout", 2000, connection.getSocketTimeout());
  }

  private BasicHttpEntityEnclosingRequest createRequestEntity() {
    return new BasicHttpEntityEnclosingRequest("GET", "http://" + host + ":" + port);
  }
}
