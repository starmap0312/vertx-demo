package com.example.starter;

import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpServer;

public class MainVerticle extends AbstractVerticle {

  // asynchronous start(): you want to do something at the verticle start-up which takes some time, and you don’t want the verticle to be considered deployed until it's done
  //                       ex. start an HTTP server but don't want to wait, and once the bind is done, signal that the promise is completed & the verticle can be considered as deployed
  @Override
  public void start(Promise<Void> promise) throws Exception {
    // deploy a verticle
    Verticle eventBusVerticle = new EventBusVerticle();
    vertx.deployVerticle(eventBusVerticle, res -> { // add a handler
      if (res.succeeded()) { // handler will be passed a result containing the deployment ID string, if deployment succeeded, which can be used laterto undeploy the deployment
        System.out.println("eventBusVerticle deployed successfully & deployment id is " + res.result());
      } else {
        System.out.println("Deployment failed!");
      }
    });
    // vertx.deployVerticle(eventBusVerticle);

    // create an HTTP server & register the request handler
    HttpServer server = this.vertx.createHttpServer();
    server.requestHandler(req -> {
      req.response().putHeader("content-type", "text/plain").end("Hello from Vert.x!");
    });
    // bind the server & once the bind is done, signal that the promise is completed
    server.listen(8888, http -> {
      if (http.succeeded()) {
        promise.complete(); // complete the promise with success
        System.out.println("HTTP server started at: http://localhost:8888");
      } else {
        promise.fail(http.cause()); // fail the promise
      }
    });
  }

  // synchronous start(): there should be no blocking the even loop inside the start() method
  // called when verticle is deployed
  public void start() throws Exception {}

  // synchronous stop(): there should be no blocking the even loop inside the stop() method
  // (optional) called when verticle is undeployed
  public void stop() {}
}
