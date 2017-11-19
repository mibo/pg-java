package de.mirb.pg.java.stream;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class MainStreamSamplesTest {


  @Test
  public void for2Stream() {
    List<String> data = createData("Hello::", 20);

    List<String> filtered = new ArrayList<>();
    for (String datum : data) {
      if(datum.contains("2")) {
        filtered.add(datum);
      }
    }
    System.out.println(filtered);

    System.out.println("------------------");

    data.stream()
        .filter(str -> str.contains("2"))
        .forEach(System.out::println);

    System.out.println("------------------");
    List<String> streamFiltered = data.stream()
        .filter(str -> str.contains("2"))
        .collect(Collectors.toList());
    System.out.println(streamFiltered);

  }

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

  @Test
  public void streamMap() {
    createData("Hello_", 20).stream()
        .map(str -> {
          int l = str.length();
          StringBuilder result = new StringBuilder();
          for (int i = l-1; i >= 0; i--) {
             result.append(str.charAt(i));
          }
          return result.toString();
        })
        .map(this::reverse)
//        .filter(this::unCheckedExceptionFor2)
        .forEach(System.out::println);
  }

  @Test
  @SuppressWarnings("all")
  public void streamFlatMap() {
    createData("Hello_", 20).stream()
        .map(this::reverse)
        .flatMap(str -> test(str))
        .forEach(System.out::println);
  }

  private Stream<Character> test(String in) {
    Stream.Builder<Character> builder = Stream.builder();
    for (int i = 0; i < in.length(); i++) {
       builder.accept(in.charAt(i));

    }
    return builder.build();
  }

  @Test
  @SuppressWarnings("all")
  public void streamException() throws UnsupportedEncodingException {
    List<String> charsetNames = Arrays.asList("utf-8", "iso-8859-1", "utf-16");//, "Hello 23");

//    List<byte[]> byteList = new ArrayList<>();
//    int iterByteCount = 0;
//    for (String charsetName : charsetNames) {
//      byte[] bytes = charsetName.getBytes(charsetName);
//      byteList.add(bytes);
//      iterByteCount += bytes.length;
//    }
//    System.out.println("Count '" + iterByteCount + "'; ByteList:: " + byteList);

    int iterByteCount = 0;
    for (String charsetName : charsetNames) {
      byte[] bytes = charsetName.getBytes(charsetName);
      iterByteCount += bytes.length;
    }
    System.out.println("Count '" + iterByteCount + "';");

    int bytesCount = charsetNames.stream()
        .map(name -> charsetBytes(name))
        .reduce(0, (result, bytArr) -> result + bytArr.length, Integer::sum);
    System.out.println("Stream count:: " + bytesCount);

//    int bytesCount = charsetNames.stream()
//        .map(name -> charsetBytes(name))
//        .map(bytes -> bytes.length)
//        .reduce(0, (a, b) -> a + b);
//    System.out.println("Stream count:: " + bytesCount);

//    int bytesCount = charsetNames.stream()
//        .map(name -> {
//          try {
//            return name.getBytes(name);
//          } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//            return new byte[0];
//          }
//        })
//        .map(bytes -> bytes.length)
//        .reduce(0, (a, b) -> a + b);
//    System.out.println("Stream count:: " + bytesCount);
  }

  private byte[] charsetBytes(String name) {
    try {
      return name.getBytes(name);
    } catch (UnsupportedEncodingException e) {
      //e.printStackTrace();
      return new byte[0];
    }
  }

  private String reverse(String in) {
    int l = in.length();
    StringBuilder result = new StringBuilder();
    for (int i = l - 1; i >= 0; i--) {
      result.append(in.charAt(i));
    }
    return result.toString();
  }

  private boolean unCheckedExceptionFor2(String str) {
    if(str.contains("2")) {
      throw new IllegalArgumentException("String contains a 2 => " + str);
    }
    return true;
  }

  private boolean checkedExceptionFor2(String str) throws IOException {
    if(str.contains("2")) {
      throw new IOException("String contains a 2 => " + str);
    }
    return true;
  }

  private List<String> createData(String prefix, int amount) {
    List<String> result = new ArrayList<>(amount);

    for (int i = 1; i <= amount; i++) {
      result.add(prefix + i);
    }
    return result;
  }
}