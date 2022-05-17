package com.example.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;

public class EventBusVerticle extends AbstractVerticle  {

  // synchronous start(): there should be no blocking the even loop inside the start() method
  // called when verticle is deployed
  public void start() throws Exception {
    // event bus:
    //   it allows different parts of your application to communicate with each other, whether they’re in the same or different Vert.x instance
    //   it supports publish/subscribe, point-to-point, and request-response messaging
    EventBus eventBus = vertx.eventBus();

    // consumer subscribes an event bus address

    // ex1. register a handler to a given address, ex. example.address
    //      eventBus.consumer([address], [handler])
    eventBus.consumer("example.address", message -> {
      System.out.println("Received a message for example.address: " + message.body() + "\n");
    });
    // ex2. eventBus.consumer([address]) & consumer.handler([handler])
    MessageConsumer<Object> consumer = eventBus.consumer("example.address");
    consumer.handler(message -> {
      System.out.println("Received a message for example.address: " + message.body() + "\n");
    });

    consumer.completionHandler(res -> {
      if (res.succeeded()) {
        System.out.println("The handler registration has reached all nodes\n");
      } else {
        System.out.println("Registration failed!\n");
      }
    });

    // producer publish/send a message to a given address

    // 1) publish/subscribe messaging: "publish" a message to all subscribers
    eventBus.publish("example.address", "publish to all subscribers of example.address");
    // the message will be delivered to all subscribers registered against the example.address

    // 2) point-to-point messaging: "send" a message to just one of the handlers registered at that address (the subscriber is chosen in a non-strict round-robin fashion)
    // send a message to a single subscriber of a given address
    eventBus.send("example.address", "send to a single subscriber of example.address");

    // 3) request-response messaging:

    // consumer acknowledge/reply a message to the producer (request-response pattern)
    //      message.reply: consumer to register a handler to a given address
    consumer.handler(message -> {
      System.out.println("consumer receives a message & reply: " + message.body() + "\n");
      message.reply(message.body() + " is processed"); // use reply to acknowledge the sender the message is processed
    });

    // producer requests with a message and expects a reply with a reply handler
    eventBus.request("example.address", "eventBus.request a message & reply on succeeded", res -> { // reply handler
      if (res.succeeded()) {
        System.out.println("Received reply/acknowledgement: " + res.result().body() + "\n");
      }
    });
  }

  // synchronous stop(): there should be no blocking the even loop inside the stop() method
  // (optional) called when verticle is undeployed
  public void stop() {}
}
