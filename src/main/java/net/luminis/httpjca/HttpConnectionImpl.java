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

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

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
   public HttpConnectionImpl(HttpManagedConnection mc, HttpManagedConnectionFactory mcf) {
      this.mc = mc;
      this.mcf = mcf;
   }

   /**
    * Close
    */
   public void close() {
     LOG.fine("Closing HttpConnection");
     if (httpResponse != null) {
       EntityUtils.consumeQuietly(httpResponse.getEntity());
     }
     mc.closeHandle(this);
   }

  /**
   * @return the {@link HttpResponse} so it can be consumed if necessary.
   */
  HttpResponse getHttpResponse() {
    return httpResponse;
  }

  @Override
  public HttpParams getParams() {
    return mc.getHttpClient().getParams();
  }

  @Override
  public ClientConnectionManager getConnectionManager() {
    return mc.getHttpClient().getConnectionManager();
  }

  @Override
  public HttpResponse execute(HttpUriRequest request) throws IOException, ClientProtocolException {
    httpResponse = mc.getHttpClient().execute(request);
    return httpResponse;
  }

  @Override
  public HttpResponse execute(HttpUriRequest request, HttpContext context)
      throws IOException, ClientProtocolException {
    httpResponse = mc.getHttpClient().execute(request, context);
    return httpResponse;
  }

  @Override
  public HttpResponse execute(HttpHost target, HttpRequest request) throws IOException, ClientProtocolException {
    httpResponse = mc.getHttpClient().execute(target, request);
    return httpResponse;
  }

  @Override
  public HttpResponse execute(HttpHost target, HttpRequest request, HttpContext context)
      throws IOException, ClientProtocolException {
    httpResponse = mc.getHttpClient().execute(target, request, context);
    return httpResponse;
  }

  @Override
  public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler)
      throws IOException, ClientProtocolException {
    HttpConnectionResponseHandler<T> handler = new HttpConnectionResponseHandler<T>(responseHandler);
    return mc.getHttpClient().execute(request, handler);
  }

  @Override
  public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context)
      throws IOException, ClientProtocolException {
    HttpConnectionResponseHandler<T> handler = new HttpConnectionResponseHandler<T>(responseHandler);
    return mc.getHttpClient().execute(request, handler, context);
  }

  @Override
  public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler)
      throws IOException, ClientProtocolException {
    HttpConnectionResponseHandler<T> handler = new HttpConnectionResponseHandler<T>(responseHandler);
    return mc.getHttpClient().execute(target, request, handler);
  }

  @Override
  public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler,
                       HttpContext context) throws IOException, ClientProtocolException {

    HttpConnectionResponseHandler<T> handler = new HttpConnectionResponseHandler<T>(responseHandler);
    return mc.getHttpClient().execute(target, request, handler, context);
  }

  /**
   * Wrap the given response handler in our own so we can keep a reference to
   * the HTTP response so it can be consumed if required.
   *
   * @param <T> the type used.
   */
  class HttpConnectionResponseHandler<T>  implements ResponseHandler<T> {

    private ResponseHandler<? extends T> responseHandler;

    public HttpConnectionResponseHandler(ResponseHandler<? extends T> responseHandler) {
      this.responseHandler = responseHandler;
    }

    @Override
    public T handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
      httpResponse = response;
      return responseHandler.handleResponse(response);
    }
  }
}
