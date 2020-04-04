package io.github.vatevr.kafka;

import io.vertx.core.impl.launcher.VertxCommandLauncher;

import java.util.Arrays;

public class Launcher extends VertxCommandLauncher {
  public static void main(String[] args) {
    System.out.println("hello java11");

    new Launcher().dispatch(Arrays.asList("run", RootVerticle.class.getCanonicalName()).toArray(new String[0]));
  }
}
