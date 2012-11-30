package retrofit.http.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import retrofit.http.Header;
import retrofit.io.TypedBytes;

/** A {@link Client} which uses {@link HttpURLConnection} for request execution. */
public class UrlConnectionClient implements Client {
  @Override public Response execute(Request request) throws IOException {
    HttpURLConnection connection = createRequest(request);
    prepareConnection(connection);
    return parseResponse(connection);
  }

  /** Callback for additional preparation of the request before execution. */
  protected void prepareConnection(HttpURLConnection connection) {
  }

  private static HttpURLConnection createRequest(Request request) throws IOException {
    URL url = new URL(request.getUrl());
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(request.getMethod().name());

    // Add headers to the request.
    List<Header> headers = request.getHeaders();
    for (Header header : headers) {
      connection.addRequestProperty(header.getName(), header.getValue());
    }

    // Add the request body, if present.
    TypedBytes body = request.getBody();
    if (body != null) {
      connection.setDoOutput(true);
      connection.setRequestProperty("Content-Type", body.mimeType().mimeName());
      body.writeTo(connection.getOutputStream());
    }

    return connection;
  }

  private static Response parseResponse(HttpURLConnection urlConnection) throws IOException {
    int status = urlConnection.getResponseCode();
    String reason = urlConnection.getResponseMessage();

    final InputStream bodyStream;
    if (status >= 400) {
      bodyStream = urlConnection.getErrorStream();
    } else {
      bodyStream = urlConnection.getInputStream();
    }
    byte[] body = Streams.readFully(bodyStream);

    List<Header> headers = new ArrayList<Header>();
    Map<String, List<String>> headerFields = urlConnection.getHeaderFields();
    for (String name : headerFields.keySet()) {
      for (String value : headerFields.get(name)) {
        headers.add(new Header(name, value));
      }
    }

    return new Response(status, reason, headers, body);
  }
}
