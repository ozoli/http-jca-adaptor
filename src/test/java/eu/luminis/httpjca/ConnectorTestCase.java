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
package eu.luminis.httpjca;

import java.io.IOException;
import java.util.UUID;

import javax.annotation.Resource;
import javax.resource.ResourceException;

import io.undetow.server.HttpServerBaseTest;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpException;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * ConnectorTestCase
 *
 * @version $Revision: $
 */
@RunWith(Arquillian.class)
public class ConnectorTestCase
{
  private static HttpServerBaseTest httpServerBaseTest;

  @BeforeClass
  public static void setup() {
    httpServerBaseTest = new HttpServerBaseTest("localhost", 8180);
    httpServerBaseTest.start();
  }
  
  /** Resource */
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
      ja.addPackages(true, Package.getPackage("eu.luminis.httpjca"));
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
  public void testGet() throws ResourceException, IOException, HttpException {
    HttpConnection connection = connectionFactory.getConnection();
    assertNotNull("http connection should not be null", connection);
    
    connection.sendRequestEntity(new BasicHttpEntityEnclosingRequest("GET", "http://localhost:8180"));
    
    HttpResponse response = connection.receiveResponseHeader();
    
    assertEquals("expected 200 OK", 200, response.getStatusLine().getStatusCode());
    assertTrue("expected Hello World",
          IOUtils.toString(response.getEntity().getContent()).contains("Hello World"));
  }
}
