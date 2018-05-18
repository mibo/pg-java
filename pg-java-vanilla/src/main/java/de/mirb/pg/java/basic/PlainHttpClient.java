package de.mirb.pg.java.basic;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

public class PlainHttpClient {

  private int BUFFER_SIZE = 1024 * 64;

  public String post(String url, String content, Map<String, String> additionalHeaders) throws IOException {
//    Proxy proxy = new Proxy();
//    HttpURLConnection con = new HttpURLConnection(new URL(""), proxy);
    HttpURLConnection con = openConnection(url);
    con.setRequestMethod("POST");
    con.setRequestProperty("Content-Type", "text/plain");
    additionalHeaders.forEach(con::setRequestProperty);

    con.setDoOutput(true);
    WritableByteChannel outChannel = Channels.newChannel(con.getOutputStream());
    ByteBuffer outBuffer = ByteBuffer.wrap(content.getBytes(StandardCharsets.ISO_8859_1));
    int wrote = outChannel.write(outBuffer);
    if(wrote < 0) {
      throw new IOException("No data written");
    }
    // is this necessary?
    con.getOutputStream().flush();
    con.getOutputStream().close();

    return readResponse(con);
  }

  private HttpURLConnection openConnection(String url) throws IOException {
    URL urly = new URL(url);
    HttpURLConnection con = (HttpURLConnection) urly.openConnection();
    if(con instanceof HttpsURLConnection) {
        // do ssl stuff
    }
    return con;
  }

  public String get(String url, Map<String, String> additionalHeaders) throws IOException {
    //    Proxy proxy = new Proxy();
    //    HttpURLConnection con = new HttpURLConnection(new URL(""), proxy);
    HttpURLConnection con = openConnection(url);
    con.setRequestMethod("GET");
    additionalHeaders.forEach(con::setRequestProperty);

    return readResponse(con);
  }

  private String readResponse(HttpURLConnection con) throws IOException {
    int responseCode = con.getResponseCode();
    if(!is2xx(responseCode)) {
      throw new IOException("Unexpected response code");
    }

    Charset responseCharset = getCharset(con);
    ReadableByteChannel inChannel = Channels.newChannel(con.getInputStream());
    ByteBuffer inBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    byte[] tmp = new byte[BUFFER_SIZE];
    StringBuilder response = new StringBuilder();
    int read = inChannel.read(inBuffer);
    while(read > 0) {
      inBuffer.flip();
      inBuffer.get(tmp, 0, read);
      response.append(new String(tmp, 0, read, responseCharset));
      inBuffer.clear();
      read = inChannel.read(inBuffer);
    }
    con.getInputStream().close();

    return response.toString();
  }

  private Charset getCharset(HttpURLConnection connection) {
    // TODO: fix this
    String contentTypeHeader = connection.getHeaderField("Content-Type").toLowerCase(Locale.US);
    if(contentTypeHeader.contains("utf-8")) {
      return StandardCharsets.UTF_8;
    } else if(contentTypeHeader.contains("iso-8859-1")) {
      return StandardCharsets.ISO_8859_1;
    }
    return StandardCharsets.US_ASCII;
  }

  private boolean is2xx(int responseCode) {
    return responseCode >= 200 && responseCode < 300;
  }
}
