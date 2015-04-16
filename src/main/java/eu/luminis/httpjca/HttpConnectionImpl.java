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

import org.apache.http.HttpConnectionMetrics;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * HttpConnectionImpl
 *
 * @version $Revision: $
 */
public class HttpConnectionImpl implements HttpConnection
{
   private static final Logger LOG = Logger.getLogger(HttpConnectionImpl.class.getName());

   /** ManagedConnection */
   private HttpManagedConnection mc;

   /** ManagedConnectionFactory */
   private HttpManagedConnectionFactory mcf;

  /**
   * local reference top the {@link HttpResponse} to make sure it is consumed correctly. 
   */
  private HttpResponse httpResponse;
  
   /**
    * Default constructor
    * @param mc HttpManagedConnection
    */
   public HttpConnectionImpl(HttpManagedConnection mc, HttpManagedConnectionFactory mcf)
   {
      this.mc = mc;
      this.mcf = mcf;
   }

   /**
    * Close
    */
   public void close()
   {
     LOG.fine("Closing HttpConnection"); 
     mc.closeHandle(this);
   }

  @Override
  public boolean isOpen() {
    return mc.getHttpConnection().isOpen();
  }

  @Override
  public boolean isStale() {
    return mc.getHttpConnection().isStale();
  }

  @Override
  public void setSocketTimeout(int timeout) {
    mc.getHttpConnection().setSocketTimeout(timeout);
  }

  @Override
  public int getSocketTimeout() {
    return mc.getHttpConnection().getSocketTimeout();
  }

  @Override
  public void shutdown() throws IOException {
    LOG.fine("shutdown HttpConnection");
    mc.getHttpConnection().shutdown();
  }

  @Override
  public HttpConnectionMetrics getMetrics() {
    return mc.getHttpConnection().getMetrics();
  }

  @Override
  public boolean isResponseAvailable(int i) throws IOException {
    return mc.getHttpConnection().isResponseAvailable(i);
  }

  @Override
  public void sendRequestHeader(HttpRequest httpRequest) throws HttpException, IOException {
    mc.getHttpConnection().sendRequestHeader(httpRequest);
  }

  @Override
  public void sendRequestEntity(HttpEntityEnclosingRequest httpEntityEnclosingRequest) 
      throws HttpException, IOException {
    mc.getHttpConnection().sendRequestEntity(httpEntityEnclosingRequest);
  }

  @Override
  public HttpResponse receiveResponseHeader() throws HttpException, IOException {
    httpResponse = mc.getHttpConnection().receiveResponseHeader();
    return httpResponse;
  }

  @Override
  public void receiveResponseEntity(HttpResponse httpResponse) throws HttpException, IOException {
    mc.getHttpConnection().receiveResponseEntity(httpResponse);
  }

  @Override
  public void flush() throws IOException {
    mc.getHttpConnection().flush();
  }

  /**
   * @return the {@link HttpResponse} so it can be consumed if necessary.
   */
  HttpResponse getHttpResponse() {
    return httpResponse;
  }
}
