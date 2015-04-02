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

import org.jboss.jca.test.eis.Handler;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

/**
 * HttpHandler
 *
 * @version $Revision: $
 */
/** Echo handler */
public class HttpHandler implements Handler
{
  private static Logger log = Logger.getLogger(HttpHandler.class.getName());

  /**
    * Default constructor
    */
   public HttpHandler()
   {
      log.info("Constructed HttpHandler");
   }

   /**
    * {@inheritDoc}
    */
   public void handle(InputStream is, OutputStream os)
   {
     log.info("handle called");
     log.info("Input " + is.toString());
     log.info("Output " + os.toString());
   }

}
