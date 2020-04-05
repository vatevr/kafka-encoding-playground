package io.github.vatevr.kafka;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.function.Consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsumerVerticle extends AbstractVerticle {

  private KafkaConsumer<String, String> consumer;

  @Override
  public void start(Promise<Void> started) {
    initConsumer();
    started.complete();

    log.info("producing inputs from {}", this.deploymentID());

    vertx.executeBlocking(this::consume, ar -> {
      if (ar.failed()) {
        log.error("failed producing due to", ar.cause());
      } else {
        log.info("succesfully produced 10 entries");
      }
    });
  }

  private <T> void consume(final Promise<T> tPromise) {
    final int giveUp = 100;
    int idleCounter = 0;

    while (true) {
      final ConsumerRecords<String, String> records = this.consumer.poll(Duration.ofMillis(1000));

      if (records.count() == 0) {
        idleCounter++;
        if (idleCounter > giveUp)
          break;
        else
          continue;
      }

      records.forEach(consume());
      consumer.commitAsync();
    }
    consumer.close();
    log.info("finished consuming");
  }

  private Consumer<ConsumerRecord<String, String>> consume() {
    return record -> log.info("consuming record {} {} {} {}", record.key(), record.value(), record.partition(), record.offset());
  }

  private void initConsumer() {
    Properties config = new Properties();
    config.put("client.id", "127.0.0.1");
    config.put("bootstrap.servers", "localhost:9093");
    config.put("key.deserializer", StringDeserializer.class.getCanonicalName());
    config.put("value.deserializer", StringDeserializer.class.getCanonicalName());
    config.put("group.id", ConsumerVerticle.class.getCanonicalName());
    config.put("acks", "all");
    this.consumer = new KafkaConsumer<>(config);
    this.consumer.subscribe(Collections.singletonList(Globals.topic()));
  }
}
