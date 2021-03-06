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

import java.util.logging.Logger;

import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ConfigProperty;
import javax.resource.spi.Connector;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.TransactionSupport;
import javax.resource.spi.endpoint.MessageEndpointFactory;

import javax.transaction.xa.XAResource;

/**
 * HttpResourceAdapter impldmenation of the {@link ResourceAdapter} interface.
 *
 * @version $Revision: $
 */
@Connector(
    description = "HTTP Resource Adapter using the Apache HttpConnection API",
    displayName = "HttpResourceAdapter",
    vendorName = "Luminis",
    eisType = "HTTP",
    version = "0.0.1",
    reauthenticationSupport = false,
    transactionSupport = TransactionSupport.TransactionSupportLevel.NoTransaction)
public class HttpResourceAdapter implements ResourceAdapter, java.io.Serializable {
   private static final long serialVersionUID = 1L;

   private static final Logger LOG = Logger.getLogger(HttpResourceAdapter.class.getName());

   @ConfigProperty(defaultValue = "localhost")
   private String hostUrl;

   @ConfigProperty(defaultValue = "8080")
   private Integer hostPort;

   /**
    * Default constructor, needed for JCA specification compliance.
    */
   public HttpResourceAdapter() {
   }

   /** 
    * Set hostUrl
    * @param hostUrl The host URL to use
    */
   public void setHostUrl(String hostUrl) {
      this.hostUrl = hostUrl;
   }

   /** 
    * Get hostUrl
    * @return The host URL
    */
   public String getHostUrl() {
      return hostUrl;
   }

   /** 
    * Set hostPort
    * @param hostPort The HTTP port
    */
   public void setHostPort(Integer hostPort) {
      this.hostPort = hostPort;
   }

   /** 
    * Get hostPort
    * @return The HTTP port
    */
   public Integer getHostPort() {
      return hostPort;
   }

   /**
    * This is called during the activation of a message endpoint.
    *
    * @param endpointFactory A message endpoint factory instance.
    * @param spec An activation spec JavaBean instance.
    * @throws ResourceException generic exception 
    */
   public void endpointActivation(MessageEndpointFactory endpointFactory,
      ActivationSpec spec) throws ResourceException {
      LOG.finest("endpointActivation()");
   }

   /**
    * This is called when a message endpoint is deactivated. 
    *
    * @param endpointFactory A message endpoint factory instance.
    * @param spec An activation spec JavaBean instance.
    */
   public void endpointDeactivation(MessageEndpointFactory endpointFactory,
      ActivationSpec spec) {
      LOG.finest("endpointDeactivation()");
   }

   /**
    * This is called when a resource adapter instance is bootstrapped.
    *
    * @param ctx A bootstrap context containing references 
    * @throws ResourceAdapterInternalException indicates bootstrap failure.
    */
   public void start(BootstrapContext ctx)
      throws ResourceAdapterInternalException {
      LOG.info("start()");
   }

   /**
    * This is called when a resource adapter instance is undeployed or
    * during application server shutdown. 
    */
   public void stop() {
      LOG.info("stop()");
   }

   /**
    * This method is called by the application server during crash recovery.
    *
    * @param specs An array of ActivationSpec JavaBeans 
    * @throws ResourceException generic exception 
    * @return An array of XAResource objects
    */
   public XAResource[] getXAResources(ActivationSpec[] specs)
      throws ResourceException {
      LOG.warning("getXAResources() returning NULL");
      return null;
   }

   /** 
    * Returns a hash code value for the object.
    * @return A hash code value for this object.
    */
   @Override
   public int hashCode() {
     return new HashCodeBuilder().append(hostUrl).append(hostPort).hashCode();
   }

   /** 
    * Indicates whether some other object is equal to this one.
    * @param other The reference object with which to compare.
    * @return true if this object is the same as the obj argument, false otherwise.
    */
   @Override
   public boolean equals(final Object other) {
     if (other == null) {
       return false;
     } else if (other == this) {
       return true;
     } else if (!(other instanceof HttpResourceAdapter)) {
        return false;
     } else {
       HttpResourceAdapter otherHttpRA = (HttpResourceAdapter) other;
       return new EqualsBuilder().append(getHostUrl(), otherHttpRA.getHostUrl())
                                 .append(getHostPort(), otherHttpRA.getHostPort()).isEquals();
     }
   }
}
