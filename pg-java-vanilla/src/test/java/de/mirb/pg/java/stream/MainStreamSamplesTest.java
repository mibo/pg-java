package de.mirb.pg.java.stream;

import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

class MainStreamSamplesTest {

  @Test
  public void stream() {
    int counter = 1;
    Stream.of("Hello", "Hello 2", "Hello 3", "Hello 23")
        .filter(str -> str.contains("2"))
        .map(str -> str + "::" + counter + 1)
        .forEach(System.out::println);
  }

  @Test
  public void parallelStream() {
    Stream.of("Hello", "Hello 2", "Hello 3", "Hello 23").parallel()
        .forEach(System.out::println);
  }
}