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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.DefaultRoutePlanner;
import org.apache.http.impl.conn.DefaultSchemePortResolver;
import org.apache.http.protocol.HttpContext;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;

import java.util.logging.Logger;

import javax.resource.ResourceException;
import javax.resource.spi.ConfigProperty;
import javax.resource.spi.ConnectionDefinition;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterAssociation;

import javax.security.auth.Subject;

/**
 * HttpManagedConnectionFactory
 *
 * @version $Revision: $
 */
@ConnectionDefinition(connectionFactory = HttpConnectionFactory.class,
   connectionFactoryImpl = HttpConnectionFactoryImpl.class,
   connection = HttpConnection.class,
   connectionImpl = HttpConnectionImpl.class)
public class HttpManagedConnectionFactory implements ManagedConnectionFactory, ResourceAdapterAssociation {
   private static final long serialVersionUID = 1L;

   private static final Logger LOG = Logger.getLogger(HttpManagedConnectionFactory.class.getName());

   private ResourceAdapter resourceAdapter;

   private PrintWriter logwriter;

   @ConfigProperty(defaultValue = "localhost")
   private String host;

   @ConfigProperty(defaultValue = "1400")
   private Integer port;

   /**
    * Basic Http Connection Pool for the {@link HttpManagedConnection}s.
    */
   private final BasicHttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager();

   // A default route planner if the host and port are not specified.
   private final HttpRoutePlanner routePlanner = new DefaultRoutePlanner(DefaultSchemePortResolver.INSTANCE) {
      @Override
      public HttpRoute determineRoute(final HttpHost target, final HttpRequest request, final HttpContext context)
          throws HttpException {
         return super.determineRoute(target, request, context);
      }
   };

   /**
    * Default constructor
    */
   public HttpManagedConnectionFactory() {
   }
   
   /**
    * Set host
    * @param host The value
    */
   public void setHost(final String host) {
      this.host = host;
   }

   /**
    * Get host
    * @return The value
    */
   public String getHost() {
      return host;
   }

   /**
    * Set port
    *
    * @param port The value
    */
   public void setPort(final Integer port) {
      this.port = port;
   }

   /**
    * Get port
    *
    * @return The value
    */
   public Integer getPort() {
      return port;
   }

   /**
    * Creates a Connection Factory instance.
    *
    * @param cxManager ConnectionManager to be associated with created EIS connection factory instance
    * @return EIS-specific Connection Factory instance or javax.resource.cci.ConnectionFactory instance
    * @throws ResourceException Generic exception
    */
   public Object createConnectionFactory(ConnectionManager cxManager) throws ResourceException {
      LOG.finest("createConnectionFactory()");
      return new HttpConnectionFactoryImpl(this, cxManager);
   }

   /**
    * Creates a Connection Factory instance.
    *
    * @return EIS-specific Connection Factory instance or javax.resource.cci.ConnectionFactory instance
    * @throws ResourceException Generic exception
    */
   public Object createConnectionFactory() throws ResourceException {
      throw new ResourceException("This resource adapter doesn't support non-managed environments");
   }

   /**
    * Creates a new physical connection to the underlying EIS resource manager.
    *
    * @param subject       Caller's security information
    * @param cxRequestInfo Additional resource adapter specific connection request information
    * @return ManagedConnection instance
    * @throws ResourceException generic exception
    */
   public ManagedConnection createManagedConnection(Subject subject,
                                                    ConnectionRequestInfo cxRequestInfo) throws ResourceException {
      LOG.finest("createManagedConnection()");
      CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(
          connectionManager).setRoutePlanner(routePlanner).build();
      return new HttpManagedConnection(this, httpClient);
   }

   /**
    * Returns a matched connection from the candidate set of connections.
    *
    * @param connectionSet Candidate connection set
    * @param subject       Caller's security information
    * @param cxRequestInfo Additional resource adapter specific connection request information
    * @return ManagedConnection if resource adapter finds an acceptable match otherwise null
    * @throws ResourceException generic exception
    */
   public ManagedConnection matchManagedConnections(Set connectionSet,
                                                    Subject subject, ConnectionRequestInfo cxRequestInfo) 
       throws ResourceException {
      LOG.finest("matchManagedConnections()");
      ManagedConnection result = null;
      Iterator it = connectionSet.iterator();
      while (result == null && it.hasNext()) {
         ManagedConnection mc = (ManagedConnection) it.next();
         if (mc instanceof HttpManagedConnection) {
            result = mc;
         }
      }
      return result;
   }

   /**
    * Get the log writer for this ManagedConnectionFactory instance.
    *
    * @return PrintWriter
    * @throws ResourceException generic exception
    */
   public PrintWriter getLogWriter() throws ResourceException {
      LOG.finest("getLogWriter()");
      return logwriter;
   }

   /**
    * Set the log writer for this ManagedConnectionFactory instance.
    *
    * @param out PrintWriter - an out stream for error logging and tracing
    * @throws ResourceException generic exception
    */
   public void setLogWriter(final PrintWriter out) throws ResourceException {
      LOG.finest("setLogWriter()");
      logwriter = out;
   }

   /**
    * Get the resource adapter
    *
    * @return The handle
    */
   public ResourceAdapter getResourceAdapter() {
      LOG.finest("getResourceAdapter()");
      return resourceAdapter;
   }

   /**
    * Set the resource adapter
    *
    * @param adapter The Resource Adapter
    */
   public void setResourceAdapter(final ResourceAdapter adapter) {
      LOG.finest("setResourceAdapter()");
      this.resourceAdapter = adapter;
   }

   /**
    * Returns a hash code value for the object.
    *
    * @return A hash code value for this object.
    */
   @Override
   public int hashCode() {
      return new HashCodeBuilder().append(resourceAdapter).append(logwriter)
                                  .append(host).append(port).hashCode();
   }

   /**
    * Indicates whether some other object is equal to this one.
    *
    * @param other The reference object with which to compare.
    * @return true if this object is the same as the obj argument, false otherwise.
    */
   @Override
   public boolean equals(final Object other) {
      if (other == null) {
         return false;
      } else if (other == this) {
         return true;
      } else if (!(other instanceof HttpManagedConnectionFactory)) {
         return false;
      } else {
         HttpManagedConnectionFactory managedConnection = (HttpManagedConnectionFactory) other;
         return new EqualsBuilder()
             .append(getHost(), managedConnection.getHost())
             .append(getPort(), managedConnection.getPort())
             .append(getResourceAdapter(), managedConnection.getResourceAdapter()).isEquals();
      }
   }

   /**
    * @return the {@link HttpClientConnectionManager} so connections can be released.
    */
   HttpClientConnectionManager getHttpClientConnectionManager() {
      return connectionManager;
   }
}
