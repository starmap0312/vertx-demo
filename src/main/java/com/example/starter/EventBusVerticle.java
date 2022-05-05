package com.example.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;

public class EventBusVerticle extends AbstractVerticle  {

  // synchronous start(): there should be no blocking the even loop inside the start() method
  // called when verticle is deployed
  public void start() throws Exception {
    // event bus: it allows different parts of your application to communicate with each other
    EventBus eventBus = vertx.eventBus();
    // 1) register a handler to a given address, ex. example.address
    // ex1. eventBus.consumer([address], [handler])
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

    // 2) publish/send a message to a given address
    // ex1. publish a message
    eventBus.publish("example.address", "publish to example.address");
    // the message will be delivered to all handlers registered against the example.address

    // ex2. send a message
    // send a message to a single handler of a given address (the handler is chosen in a non-strict round-robin fashion)
    eventBus.send("example.address", "send to example.address");

    // 3) acknowledge messages, i.e. request-response pattern
    // ex1. message.reply: consumer to register a handler to a given address
    consumer.handler(message -> {
      System.out.println("consumer receives a message & reply: " + message.body() + "\n");
      message.reply(message.body() + " is processed"); // use reply to acknowledge the sender the message is processed
    });

    // ex2. sender: use request method, not publish/send, and specify a reply handler
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
