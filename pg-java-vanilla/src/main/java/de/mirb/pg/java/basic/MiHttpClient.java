package de.mirb.pg.java.basic;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MiHttpClient {

    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_XML = "application/xml";
    public static final String APPLICATION_ATOM_XML = "application/atom+xml";
    public static final String METADATA = "$metadata";
    public static final int DEFAULT_BUFFER = 8192;
    //
    private Proxy.Type proxyProtocol;
    private String proxyHostname;
    private int proxyPort;
    private boolean useProxy;
    private String username;
    private String password;
    private boolean useAuthentication;

    private String url;
    private String httpMethod;
    private InputStream body;

    private MiHttpClient() {
      this.useProxy = false;
      this.useAuthentication = false;
      this.proxyProtocol = Proxy.Type.HTTP;
      this.proxyPort = 80;
    }

    public boolean isProxy() {
      return useProxy;
    }

    public boolean isAuthentication() {
      return useAuthentication;
    }

    public static class MiHttpClientBuilder {
      private MiHttpClient client = new MiHttpClient();
      private Map<String, List<String>> headers = new LinkedHashMap<>();

      private MiHttpClientBuilder(String method, String url) {
        client.httpMethod = method;
        client.url = url;
      }

      public MiHttpClientBuilder addAuthentication(String user, String password) {
        client.setAuthentication(user, password);
        return this;
      }

      public MiHttpClientBuilder addProxy(Proxy.Type type, String host, int port) {
        client.setProxy(type, host, port);
        return this;
      }

      public MiHttpClientBuilder addHeader(String name, String value) {
        List<String> values = headers.get(name);
        if (values == null) {
          values = new LinkedList<>();
          headers.put(name, values);
        }
        values.add(value);
        return this;
      }

      public MiHttpClientBuilder setBody(InputStream body) {
        client.body = body;
        return this;
      }

      public MiHttpClient create() {
        return client;
      }

      public ClientResponse execute() throws IOException {
        return client.execute();
      }

      public ClientResponse executeGet() throws IOException {
        return client.executeGet();
      }

      public ClientResponse executePost() throws IOException {
        return client.executePost();
      }
    }

    public static class ClientResponse {
      private final HttpURLConnection urlConnection;

      public ClientResponse(HttpURLConnection connection) {
        this.urlConnection = connection;
      }

      public InputStream getBody() throws IOException {
        return urlConnection.getInputStream();
      }

      public Map<String, List<String>> getHeaders() {
        return urlConnection.getHeaderFields();
      }
    }

    private void setAuthentication(String username, String password) {
      this.username = username;
      this.password = password;
      this.useAuthentication = true;
    }

    private void setProxy(Proxy.Type type, String host, int port) {
      this.proxyHostname = host;
      this.proxyPort = port;
      this.proxyProtocol = type;
      this.useProxy = true;
    }

    public static MiHttpClientBuilder get(String url) {
      return new MiHttpClientBuilder("GET", url);
    }

    public static MiHttpClientBuilder with(String httpMethod, String url) {
      return new MiHttpClientBuilder(httpMethod, url);
    }

    public ClientResponse execute() throws IOException {
      if ("GET".equals(httpMethod)) {
        return executeGet();
      } else if ("POST".equals(httpMethod)) {
        return executePost();
      } else {
        throw new UnsupportedOperationException("HttpMethod '" + httpMethod + "' is not supported yet.");
      }
    }

    public ClientResponse executeGet() throws IOException {
      //      MiHttpClient client = new MiHttpClient();
      return new ClientResponse(getRequest(url, getContentType(), httpMethod));
    }

    public ClientResponse executePost() throws IOException {
      //      MiHttpClient client = new MiHttpClient();
      if (body == null) {
        throw new IllegalArgumentException("Body must not be null for post request.");
      }
      return new ClientResponse(postRequest(url, body, getContentType(), httpMethod));
    }

    public String getMethod() {
      return httpMethod;
    }

    public String getUrl() {
      return url;
    }

    /**
     * private methods
     */

    private String getContentType() {
      // TODO: change
      return APPLICATION_JSON;
    }

    private void checkStatus(HttpURLConnection connection) throws IOException {
      if (400 <= connection.getResponseCode() && connection.getResponseCode() <= 599) {
        HttpStatusCode httpStatusCode = HttpStatusCode.fromStatusCode(connection.getResponseCode());
        throw new IOException(httpStatusCode.getStatusCode() + " " + httpStatusCode.toString());
      }
    }

    public static InputStream getRawHttpEntity(String relativeUri, String contentType) throws IOException {
      MiHttpClient client = new MiHttpClient();
      return (InputStream) client.getRequest(relativeUri, contentType, "GET").getContent();
    }

    private HttpURLConnection getRequest(String relativeUri, String contentType, String httpMethod)
      throws IOException {
      HttpURLConnection connection = initializeConnection(relativeUri, contentType, httpMethod);

      connection.connect();

      checkStatus(connection);

      return connection;
    }

    private HttpURLConnection postRequest(String relativeUri, InputStream is, String contentType, String httpMethod)
      throws IOException {
      HttpURLConnection connection = initializeConnection(relativeUri, contentType, httpMethod);
      byte[] buffer = new byte[DEFAULT_BUFFER];
      int size = is.read(buffer);

      connection.setDoOutput(true);
      //
      Logger.getLogger(MiHttpClient.class.getName()).log(Level.INFO, "\n" + new String(buffer, 0, size) + "\n");
      //
      connection.getOutputStream().write(buffer, 0, size);
      connection.connect();
      checkStatus(connection);

      return connection;
    }

    private HttpURLConnection initializeConnection(String url, String contentType, String httpMethod)
      throws IOException {
      URL requestUrl = new URL(url);
      HttpURLConnection connection;
      if (useProxy) {
        Proxy proxy = new Proxy(proxyProtocol, new InetSocketAddress(proxyHostname, proxyPort));
        connection = (HttpURLConnection) requestUrl.openConnection(proxy);
      } else {
        connection = (HttpURLConnection) requestUrl.openConnection();
      }
      // TODO: do better
      connection.setRequestMethod(httpMethod);
      connection.setRequestProperty("Accept", contentType);
      connection.setRequestProperty(HttpHeader.CONTENT_TYPE.asString(), contentType);
      //

      if (useAuthentication) {
        String authorization = "Basic ";
        authorization += new String(Base64.getEncoder().encode((username + ":" + password).getBytes()));
        connection.setRequestProperty("Authorization", authorization);
      }

      return connection;
    }

    public enum HttpHeader {
      ACCEPT("Accept"),
      ACCEPT_CHARSET("Accept-Charset"),
      ACCEPT_ENCODING("Accept-Encoding"),
      ACCEPT_LANGUAGE("Accept-Language"),
      ALLOW("Allow"),
      AUTHORIZATION("Authorization"),
      CACHE_CONTROL("Cache-Control"),
      CONTENT_ENCODING("Content-Encoding"),
      CONTENT_LANGUAGE("Content-Language"),
      CONTENT_LENGTH("Content-Length"),
      CONTENT_LOCATION("Content-Location"),
      CONTENT_TYPE("Content-Type"),
      DATE("Date"),
      ETAG("ETag"),
      EXPIRES("Expires"),
      HOST("Host"),
      IF_MATCH("If-Match"),
      IF_MODIFIED_SINCE("If-Modified-Since"),
      IF_NONE_MATCH("If-None-Match"),
      IF_UNMODIFIED_SINCE("If-Unmodified-Since"),
      LAST_MODIFIED("Last-Modified"),
      LOCATION("Location"),
      LINK("Link"),
      RETRY_AFTER("Retry-After"),
      USER_AGENT("User-Agent"),
      VARY("Vary"),
      WWW_AUTHENTICATE("WWW-Authenticate"),
      COOKIE("Cookie"),
      SET_COOKIE("Set-Cookie");

      private final String name;

      HttpHeader(String name) {
        this.name = name;
      }

      public String asString() {
        return name;
      }
    }

    /**
     * Created by michael on 01.08.15.
     */
    public enum HttpStatusCode {
      OK(200, "OK"),
      CREATED(201, "Created"),
      ACCEPTED(202, "Accepted"),
      NO_CONTENT(204, "No Content"),
      RESET_CONTENT(205, "Reset Content"),
      PARTIAL_CONTENT(206, "Partial Content"),
      MOVED_PERMANENTLY(301, "Moved Permanently"),
      FOUND(302, "Found"),
      SEE_OTHER(303, "See Other"),
      NOT_MODIFIED(304, "Not Modified"),
      USE_PROXY(305, "Use Proxy"),
      TEMPORARY_REDIRECT(307, "Temporary Redirect"),
      BAD_REQUEST(400, "Bad Request"),
      UNAUTHORIZED(401, "Unauthorized"),
      PAYMENT_REQUIRED(402, "Payment Required"),
      FORBIDDEN(403, "Forbidden"),
      NOT_FOUND(404, "Not Found"),
      METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
      NOT_ACCEPTABLE(406, "Not Acceptable"),
      PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
      REQUEST_TIMEOUT(408, "Request Timeout"),
      CONFLICT(409, "Conflict"),
      GONE(410, "Gone"),
      LENGTH_REQUIRED(411, "Length Required"),
      PRECONDITION_FAILED(412, "Precondition Failed"),
      REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"),
      REQUEST_URI_TOO_LONG(414, "Request-URI Too Long"),
      UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
      REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable"),
      EXPECTATION_FAILED(417, "Expectation Failed"),
      PRECONDITION_REQUIRED(428, "Precondition Required"),
      INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
      NOT_IMPLEMENTED(501, "Not Implemented"),
      BAD_GATEWAY(502, "Bad Gateway"),
      SERVICE_UNAVAILABLE(503, "Service Unavailable"),
      GATEWAY_TIMEOUT(504, "Gateway Timeout"),
      HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported");

      private final int code;
      private final String info;

      HttpStatusCode(int statusCode, String info) {
        this.code = statusCode;
        this.info = info;
      }

      public static HttpStatusCode fromStatusCode(int statusCode) {
        HttpStatusCode[] values = values();
        int len = values.length;

        for(int i = 0; i < values.length; ++i) {
          HttpStatusCode s = values[i];
          if(s.code == statusCode) {
            return s;
          }
        }

        return null;
      }

      public int getStatusCode() {
        return code;
      }

      public String getInfo() {
        return info;
      }

      public String toString() {
        return getInfo();
      }
    }
  }

