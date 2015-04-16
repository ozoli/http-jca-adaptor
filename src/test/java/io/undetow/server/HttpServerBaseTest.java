package io.undetow.server;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.util.logging.Logger;

/**
 * Created by ocarr on 09/04/15.
 */
public class HttpServerBaseTest {
  private static final Logger LOG = Logger.getLogger(HttpServerBaseTest.class.getName());

  private final Undertow server;

  private String host;
  private int port;

  public HttpServerBaseTest(final String host, final int port) {
    this.host = host;
    this.port = port;
    this.server = buildHttpServer();
  }

  public void start() {
    LOG.info("Starting HTTP Server on host " + host + " and port " + port);
    server.start();
  }

  public void stop() {
    server.stop();
  }
  
  private Undertow buildHttpServer() {
    return Undertow.builder()
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
