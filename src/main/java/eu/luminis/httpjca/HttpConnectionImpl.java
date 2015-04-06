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
   /** The logger */
   private static Logger log = Logger.getLogger(HttpConnectionImpl.class.getName());

   /** ManagedConnection */
   private HttpManagedConnection mc;

   /** ManagedConnectionFactory */
   private HttpManagedConnectionFactory mcf;

   private org.apache.http.impl.DefaultHttpClientConnection httpClientConnection;
   /**
    * Default constructor
    * @param mc HttpManagedConnection
    * @param mcf HttpManagedConnectionFactory
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
      mc.closeHandle(this);
   }

  @Override
  public boolean isOpen() {
    return httpClientConnection.isOpen();
  }

  @Override
  public boolean isStale() {
    return httpClientConnection.isStale();
  }

  @Override
  public void setSocketTimeout(int i) {
    httpClientConnection.setSocketTimeout(i);
  }

  @Override
  public int getSocketTimeout() {
    return httpClientConnection.getSocketTimeout();
  }

  @Override
  public void shutdown() throws IOException {
    httpClientConnection.shutdown();
  }

  @Override
  public HttpConnectionMetrics getMetrics() {
    return httpClientConnection.getMetrics();
  }

  @Override
  public boolean isResponseAvailable(int i) throws IOException {
    return httpClientConnection.isResponseAvailable(i);
  }

  @Override
  public void sendRequestHeader(HttpRequest httpRequest) throws HttpException, IOException {
    httpClientConnection.sendRequestHeader(httpRequest);
  }

  @Override
  public void sendRequestEntity(HttpEntityEnclosingRequest httpEntityEnclosingRequest) 
      throws HttpException, IOException {
    httpClientConnection.sendRequestEntity(httpEntityEnclosingRequest);
  }

  @Override
  public HttpResponse receiveResponseHeader() throws HttpException, IOException {
    return httpClientConnection.receiveResponseHeader();
  }

  @Override
  public void receiveResponseEntity(HttpResponse httpResponse) throws HttpException, IOException {
    httpClientConnection.receiveResponseEntity(httpResponse);
  }

  @Override
  public void flush() throws IOException {
    httpClientConnection.flush();
  }
}
