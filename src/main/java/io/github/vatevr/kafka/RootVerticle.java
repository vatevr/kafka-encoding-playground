package io.github.vatevr.kafka;

import io.vertx.core.*;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

@Slf4j
public class RootVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) {
    CompositeFuture
        .join(this.deploy(ProducerVerticle::new), this.deploy(ConsumerVerticle::new))
        .compose(v -> Future.<Void>succeededFuture())
        .setHandler(startPromise);
  }

  private Future<Void> deploy(Supplier<Verticle> verticle) {
    Promise<String> whenDeployed = Promise.promise();
    this.vertx.deployVerticle(verticle, new DeploymentOptions().setInstances(1), whenDeployed);
    return whenDeployed.future().mapEmpty();
  }
}
