package net.luminis.httpjca.util;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;

import java.util.logging.Logger;

/**
 * A base HttpServer implementation for different tests to use.
 */
public class HttpServerBase {
  private static final Logger LOG = Logger.getLogger(HttpServerBase.class.getName());

  private Undertow httpServer;

  protected static BasicHttpEntityEnclosingRequest createGetRequestEntity() {
    return new BasicHttpEntityEnclosingRequest(new RequestLine() {
      @Override
      public String getMethod() {
        return "GET";
      }

      @Override
      public ProtocolVersion getProtocolVersion() {
        return new ProtocolVersion("HTTP",1,1);
      }

      @Override
      public String getUri() {
        return "/";
      }
    });
  }

  protected static BasicHttpEntityEnclosingRequest createBadGetRequestEntity() {
    return new BasicHttpEntityEnclosingRequest(new RequestLine() {
      @Override
      public String getMethod() {
        return "GET";
      }

      @Override
      public ProtocolVersion getProtocolVersion() {
        return new ProtocolVersion("HTTP",1,1);
      }

      @Override
      public String getUri() {
        return "/tyoyto";
      }
    });
  }

  protected void startHttpServer() {
    LOG.info("Starting HTTP Server");
    httpServer.start();
  }

  protected void stopHttpServer() {
    if (httpServer != null) {
      LOG.info("Stopping HTTP Server");
      httpServer.stop();
    }
  }
  
  protected void buildHttpServer(final String host, final int port) {
    LOG.info("Building HTTP Server on host " + host + " port " + port);

    httpServer = Undertow.builder()
        .addHttpListener(port, host)
        .setHandler(new HttpHandler() {
          @Override
          public void handleRequest(final HttpServerExchange exchange) throws Exception {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            exchange.getResponseSender().send("Hello World");
          }
        }).build();
  }

}
