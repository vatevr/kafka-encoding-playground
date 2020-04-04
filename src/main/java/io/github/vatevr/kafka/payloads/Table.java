package io.github.vatevr.kafka.payloads;

import com.github.javafaker.Faker;
import lombok.Value;

@Value
public class Table {
  private final String id;
  private final String name;
  private final WOOD wood;

  private enum WOOD {
    VENETIAN,
    CROATIAN,
    MDF
  }

  public static Table random(String id) {
    Faker faker = new Faker();

    return new Table(id, faker.funnyName().name(), WOOD.values()[faker.random().nextInt(WOOD.values().length - 1)]);
  }
}
