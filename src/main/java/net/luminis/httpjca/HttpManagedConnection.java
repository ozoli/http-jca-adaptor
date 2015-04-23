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


import org.apache.http.HttpClientConnection;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionEvent;
import javax.resource.spi.ConnectionEventListener;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.LocalTransaction;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionMetaData;

import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;

/**
 * HttpManagedConnection
 *
 * @version $Revision: $
 */
public class HttpManagedConnection implements ManagedConnection
{
   private static final Logger LOG = Logger.getLogger(HttpManagedConnection.class.getName());

   private PrintWriter logwriter;

   /** ManagedConnectionFactory */
   private HttpManagedConnectionFactory mcf;

   /** Listeners */
   private List<ConnectionEventListener> listeners;

   /** Connection */
   private HttpConnectionImpl connection;

   /**
    * The underlying {@link HttpClientConnection}.
    */
   private CloseableHttpClient httpClient;

   /**
    * Default constructor
    * @param mcf mcf
    * @param httpClient HTTP Client underlying
    */
   public HttpManagedConnection(HttpManagedConnectionFactory mcf, CloseableHttpClient httpClient)
       throws ResourceException
   {
      this.mcf = mcf;
      this.logwriter = null;
      this.listeners = Collections.synchronizedList(new ArrayList<ConnectionEventListener>(1));
      this.httpClient = httpClient;
      this.connection = new HttpConnectionImpl(this, mcf);
   }

   /**
    * @return the underlying {@link org.apache.http.HttpClientConnection}
    */
   HttpClient getHttpClient() {
      return httpClient;
   }
   
   /**
    * Creates a new connection handle for the underlying physical connection 
    * represented by the ManagedConnection instance. 
    *
    * @param subject Security context as JAAS subject
    * @param cxRequestInfo ConnectionRequestInfo instance
    * @return generic Object instance representing the connection handle. 
    * @throws ResourceException generic exception if operation fails
    */
   @Override
   public Object getConnection(Subject subject,
      ConnectionRequestInfo cxRequestInfo) throws ResourceException
   {
      LOG.finest("getConnection()");
      connection = new HttpConnectionImpl(this, mcf);
      return connection;
   }

   /**
    * Used by the container to change the association of an 
    * application-level connection handle with a ManagedConneciton instance.
    *
    * @param connection Application-level connection handle
    * @throws ResourceException generic exception if operation fails
    */
   @Override
   public void associateConnection(final Object connection) throws ResourceException
   {
      LOG.finest("associateConnection()");

      if (connection == null) {
         LOG.severe("NULL connection passed to associateConnection()");
         throw new ResourceException("Null connection handle");
      } else if (!(connection instanceof HttpConnectionImpl)) {
         LOG.severe("Wrong type of connection passed to associateConnection");
         throw new ResourceException("Wrong connection handle");
      }
      this.connection = (HttpConnectionImpl) connection;
   }

   /**
    * Application server calls this method to force any cleanup on the ManagedConnection instance.
    *
    * @throws ResourceException generic exception if operation fails
    */
   public void cleanup() throws ResourceException
   {
      LOG.finest("cleanup()");
      if (connection != null) {
          consumeHttpResponseEntity();
      }
   }

   /**
    * Destroys the physical connection to the underlying resource manager.
    *
    * @throws ResourceException generic exception if operation fails
    */
   public void destroy() throws ResourceException
   {
      LOG.finest("destroy()");
      consumeHttpResponseEntity();
      try {
         httpClient.close();
      } catch (IOException e) {
         LOG.throwing(HttpManagedConnection.class.getName(), "destroy()", e);
         throw new ResourceException(e);
      }
   }

   private void consumeHttpResponseEntity() {
      if (connection.getHttpResponse() != null) {
         EntityUtils.consumeQuietly(connection.getHttpResponse().getEntity());
      }
   }

   /**
    * Adds a connection event listener to the ManagedConnection instance.
    *
    * @param listener A new ConnectionEventListener to be registered
    */
   public void addConnectionEventListener(ConnectionEventListener listener)
   {
      LOG.finest("addConnectionEventListener()");
      if (listener == null) {
         throw new IllegalArgumentException("Listener is null");
      }
      listeners.add(listener);
   }

   /**
    * Removes an already registered connection event listener from the ManagedConnection instance.
    *
    * @param listener already registered connection event listener to be removed
    */
   public void removeConnectionEventListener(ConnectionEventListener listener)
   {
      LOG.finest("removeConnectionEventListener()");
      if (listener == null) {
         LOG.severe("ConnectionEventListener is NULL");
         throw new IllegalArgumentException("Listener is null");
      }
      listeners.remove(listener);
   }

   /**
    * Close connection
    *
    * @param connection The connection
    */
   void closeHandle(HttpConnection connection)
   {
      ConnectionEvent event = new ConnectionEvent(this, ConnectionEvent.CONNECTION_CLOSED);
      event.setConnectionHandle(connection);
      for (final ConnectionEventListener cel : listeners)
      {
         cel.connectionClosed(event);
      }
   }

   /**
    * Gets the log writer for this ManagedConnection instance.
    *
    * @return Character output stream associated with this Managed-Connection instance
    * @throws ResourceException generic exception if operation fails
    */
   public PrintWriter getLogWriter() throws ResourceException
   {
      LOG.finest("getLogWriter()");
      return logwriter;
   }

   /**
    * Sets the log writer for this ManagedConnection instance.
    *
    * @param out Character Output stream to be associated
    * @throws ResourceException  generic exception if operation fails
    */
   public void setLogWriter(PrintWriter out) throws ResourceException
   {
      LOG.finest("setLogWriter()");
      logwriter = out;
   }

   /**
    * Returns an <code>javax.resource.spi.LocalTransaction</code> instance.
    *
    * @return LocalTransaction instance
    * @throws ResourceException generic exception if operation fails
    */
   public LocalTransaction getLocalTransaction() throws ResourceException
   {
      throw new NotSupportedException("getLocalTransaction() not supported");
   }

   /**
    * Returns an <code>javax.transaction.xa.XAresource</code> instance. 
    *
    * @return XAResource instance
    * @throws ResourceException generic exception if operation fails
    */
   public XAResource getXAResource() throws ResourceException
   {
      throw new NotSupportedException("getXAResource() not supported");
   }

   /**
    * Gets the metadata information for this connection's underlying EIS resource manager instance. 
    *
    * @return ManagedConnectionMetaData instance
    * @throws ResourceException generic exception if operation fails
    */
   public ManagedConnectionMetaData getMetaData() throws ResourceException
   {
      LOG.finest("getMetaData()");
      return new HttpManagedConnectionMetaData();
   }

}
