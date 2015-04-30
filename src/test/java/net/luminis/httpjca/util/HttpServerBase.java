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
public abstract class HttpServerBase {
  private static final Logger LOG = Logger.getLogger(HttpServerBase.class.getName());

  private static Undertow httpServer;

  protected static String host = System.getProperty("UNDERTOW_HTTP_HOST", "localhost");
  protected static int port = Integer.valueOf(System.getProperty("UNDERTOW_HTTP_PORT", "8180"));

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

  protected static void startHttpServer() {
    LOG.info("Starting HTTP Server host " + host + ":" + port);
    HttpServerBase.httpServer.start();
  }

  protected static void stopHttpServer() {
    LOG.info("Stopping HTTP Server host " + host + ":" + port);
    HttpServerBase.httpServer.stop();
  }
  
  protected static void buildHttpServer() {
    HttpServerBase.httpServer = Undertow.builder()
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
