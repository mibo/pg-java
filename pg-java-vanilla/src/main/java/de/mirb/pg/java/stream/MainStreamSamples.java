package de.mirb.pg.java.stream;

import java.util.stream.Stream;

public class MainStreamSamples {

  public static void main(String ... args) {
    int counter = 1;
    Stream.of("Hello", "Hello 2", "Hello 3", "Hello 23")
        .filter(str -> str.contains("2"))
        .map(str -> str + "::" + counter+1)
        .forEach(System.out::println);
  }

}
