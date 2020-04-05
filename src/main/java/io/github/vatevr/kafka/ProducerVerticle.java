package io.github.vatevr.kafka;

import io.github.vatevr.kafka.payloads.Table;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.stream.IntStream;

@Slf4j
public class ProducerVerticle extends AbstractVerticle {
  private KafkaProducer<String, String> producer;

  @Override
  public void start(Promise<Void> started) {
    initProducer();
    started.complete();

    log.info("producing inputs from {}", this.deploymentID());

    vertx.executeBlocking(this::produce, ar -> {
      if (ar.failed()) {
        log.error("failed producing due to", ar.cause());
      } else {
        log.info("succesfully produced 10 entries");
      }
    });
  }

  private void produce(Promise<Void> promise) {
    IntStream.of(10).forEach(i -> producer.send(record(i)));
    promise.complete();
  }

  private ProducerRecord<String, String> record(int i) {
    Table table = table(i);
    return new ProducerRecord<>(Globals.topic(), table.getId(), JsonObject.mapFrom(table).encode());
  }

  private Table table(int i) {
    return Table.random(this.deploymentID() + "_" +i);
  }

  private void initProducer() {
    Properties config = new Properties();
    config.put("client.id", "127.0.0.1");
    config.put("bootstrap.servers", "localhost:9093");
    config.put("key.serializer", StringSerializer.class.getCanonicalName());
    config.put("value.serializer", StringSerializer.class.getCanonicalName());
    config.put("acks", "all");
    this.producer = new KafkaProducer<>(config);
  }
}
