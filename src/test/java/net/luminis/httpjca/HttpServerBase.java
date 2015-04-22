package net.luminis.httpjca;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;

import java.util.logging.Logger;

/**
 * A base HttpServer implementation for different tests to use.
 */
public abstract class HttpServerBase {
  private static final Logger LOG = Logger.getLogger(HttpServerBase.class.getName());

  private static Undertow httpServer;

  protected static String host = System.getProperty("undertow.http.host", "localhost");
  protected static int port = Integer.valueOf(System.getProperty("undertow.http.port", "8180"));

  protected static BasicHttpEntityEnclosingRequest createGetRequestEntity() {
    return new BasicHttpEntityEnclosingRequest("GET", "http://" + host + ":" + port);
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
