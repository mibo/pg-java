package de.mirb.pg.java.basic;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PlainHttpClientTest {

  @Test
  public void testGetRequest() throws IOException {
    PlainHttpClient hc = new PlainHttpClient();
    String response = hc.get("http://localhost:7070", Collections.emptyMap());

    System.out.println(response);
  }


  @Test
  public void testPostRequest() throws IOException {
    PlainHttpClient hc = new PlainHttpClient();
    Map<String, String> headers = new HashMap<>();
    headers.put("Authorization", "Bearer 123");
    String response = hc.post("http://localhost:7070/post", "BoydContent", headers);

    System.out.println(response);
  }
}
