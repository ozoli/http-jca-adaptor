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

import javax.annotation.Resource;
import javax.resource.ResourceException;

import net.luminis.httpjca.util.ArquillianTestUtil;
import net.luminis.httpjca.util.HttpServerBase;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;

import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Test case for the {@link HttpConnection} class' execute methods except those using
 * a {@link ResponseHandler}.
 */
@RunWith(Arquillian.class)
public class HttpConnectionTestCase extends HttpServerBase
{
  @BeforeClass
  public static void setup() {
    buildHttpServer();
    startHttpServer();
  }
  
  @Resource(mappedName = "java:/eis/HttpConnectionFactory")
  private HttpConnectionFactory connectionFactory;

  @Resource
  private HttpManagedConnectionFactory managedConnectionFactory;

  /**
    * Define the deployment
    *
    * @return The deployment archive
    */
   @Deployment
   public static EnterpriseArchive createDeployment() {
     return ArquillianTestUtil.createDeployment();
   }

  @Test
  public void testNotNullConnectionFactory() throws ResourceException {
    assertNotNull("connection factory should not be null", connectionFactory);
  }

  @Test
  public void testManagedConnectionFactory() throws ResourceException {
    assertNotNull("managed connection factory should not be null", managedConnectionFactory);
    assertNotNull("expected ResourceAdapter", managedConnectionFactory.getResourceAdapter());
    assertTrue("expected HttpResourceAdapter",
        managedConnectionFactory.getResourceAdapter() instanceof HttpResourceAdapter);
  }

  @Test
  public void testHttpConnectionClass() throws ResourceException {
    HttpConnection connection = connectionFactory.getConnection();
    assertNotNull("http connection should not be null", connection);
    assertTrue("http connection should instance of HttpConnectionImpl",
        connection instanceof HttpConnectionImpl);
    connection.close();
  }

  @Test
  public void testExecuteHostRequest() throws Exception {
    HttpConnection connection = connectionFactory.getConnection();
    assertNotNull("http connection should not be null", connection);

    HttpHost target = new HttpHost(host, port);
    HttpResponse response = connection.execute(target, createGetRequestEntity());

    assertEquals("expected 200 OK", 200, response.getStatusLine().getStatusCode());
    assertTrue("expected Hello World",
        IOUtils.toString(response.getEntity().getContent()).contains("Hello World"));
    connection.close();
  }

  @Test
  public void testExecuteHostRequestNoConsume() throws Exception {
    HttpConnection connection = connectionFactory.getConnection();
    assertNotNull("http connection should not be null", connection);

    connection.execute(new HttpHost(host, port), createGetRequestEntity());
    connection.close();
  }

  @Test
  public void testExecuteHostRequestContext() throws Exception {
    HttpConnection connection = connectionFactory.getConnection();
    assertNotNull("http connection should not be null", connection);

    HttpHost target = new HttpHost(host, port);
    HttpResponse response = connection.execute(target, createGetRequestEntity(), new HttpClientContext());

    assertEquals("expected 200 OK", 200, response.getStatusLine().getStatusCode());
    assertTrue("expected Hello World",
        IOUtils.toString(response.getEntity().getContent()).contains("Hello World"));
    connection.close();
  }

  @Test
  public void testExecuteUriRequest() throws Exception {
    HttpConnection connection = connectionFactory.getConnection();
    assertNotNull("http connection should not be null", connection);

    HttpResponse response = connection.execute(new HttpGet("http://" + host + ":" + port));

    assertEquals("expected 200 OK", 200, response.getStatusLine().getStatusCode());
    assertTrue("expected Hello World",
        IOUtils.toString(response.getEntity().getContent()).contains("Hello World"));
    connection.close();
  }

  @Test
  public void testExecuteUriRequestContext() throws Exception {
    HttpConnection connection = connectionFactory.getConnection();
    assertNotNull("http connection should not be null", connection);

    HttpResponse response = connection.execute(new HttpGet("http://" + host + ":" + port), new HttpClientContext());

    assertEquals("expected 200 OK", 200, response.getStatusLine().getStatusCode());
    assertTrue("expected Hello World",
        IOUtils.toString(response.getEntity().getContent()).contains("Hello World"));
    connection.close();
  }

  @Test
  public void testBadExecute() throws Exception {
    HttpConnection connection = connectionFactory.getConnection();
    assertNotNull("http connection should not be null", connection);

    HttpHost target = new HttpHost(host, port);
    HttpResponse response = connection.execute(target, createBadGetRequestEntity());

    assertEquals("expected 404", 404, response.getStatusLine().getStatusCode());

    connection.close();
  }
  
  @Test
  public void testShutdown() throws Exception {
    HttpConnection connection = connectionFactory.getConnection();
    HttpHost target = new HttpHost(host, port);
    HttpResponse response = connection.execute(target, createBadGetRequestEntity());

    assertEquals("expected 200 OK", 200, response.getStatusLine().getStatusCode());

    connection.close();
  }

  @AfterClass
  public static void shutdown() {
    stopHttpServer();
  }
}
