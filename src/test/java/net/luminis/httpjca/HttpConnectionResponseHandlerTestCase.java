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

import net.luminis.httpjca.util.ArquillianTestUtil;
import net.luminis.httpjca.util.HttpServerBase;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Resource;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Test case for the {@link HttpConnection} class' {@link ResponseHandler} execute methods.
 */
@RunWith(Arquillian.class)
public class HttpConnectionResponseHandlerTestCase extends HttpServerBase
{
  @BeforeClass
  public static void setup() {
    buildHttpServer();
    startHttpServer();
  }
  
  @Resource(mappedName = "java:/eis/HttpConnectionFactory")
  private HttpConnectionFactory connectionFactory;

  private ResponseHandler<Integer> responseHandler = new ResponseHandler<Integer>() {
    @Override
    public Integer handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
      return response.getStatusLine().getStatusCode();
    }
  };

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
  public void testExecuteResponseHandler() throws Exception {
    HttpConnection connection = connectionFactory.getConnection();
    assertNotNull("http connection should not be null", connection);

    Integer responseCode = connection.execute(new HttpGet("http://" + host + ":" + port), responseHandler);
    assertEquals("expected 200", Integer.valueOf(200), responseCode);
    connection.close();
  }

  @AfterClass
  public static void shutdown() {
    stopHttpServer();
  }
}
