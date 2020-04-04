package io.github.vatevr.kafka;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class ConsumerVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) {
    startPromise.complete();
  }
}
