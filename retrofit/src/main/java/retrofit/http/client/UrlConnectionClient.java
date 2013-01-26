package retrofit.http.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import retrofit.http.Header;
import retrofit.io.TypedBytes;

/** A {@link Client} which uses {@link HttpURLConnection} for request execution. */
public class UrlConnectionClient implements Client {
  private static final String CRLF = "\r\n";

  @Override public Response execute(Request request) throws IOException {
    HttpURLConnection connection = createRequest(request);
    prepareConnection(connection);
    return parseResponse(connection);
  }

  /** Callback for additional preparation of the request before execution. */
  protected void prepareConnection(HttpURLConnection connection) {
  }

  static HttpURLConnection createRequest(Request request) throws IOException {
    URL url = new URL(request.getUrl());
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(request.getMethod());

    // Add headers to the request.
    List<Header> headers = request.getHeaders();
    for (Header header : headers) {
      connection.addRequestProperty(header.getName(), header.getValue());
    }

    // Add the request body, if present.
    TypedBytes body = request.getBody();
    Map<String, TypedBytes> bodyParameters = request.getBodyParameters();
    if (body != null) {
      connection.setDoOutput(true);
      connection.setRequestProperty("Content-Type", body.mimeType().mimeName());
      body.writeTo(connection.getOutputStream());
    } else if (bodyParameters != null) {
      String boundary = Long.toHexString(System.nanoTime());
      connection.setDoOutput(true);
      connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
      OutputStream outputStream = connection.getOutputStream();
      PrintWriter writer = new PrintWriter(outputStream, true);
      for (Map.Entry<String, TypedBytes> entry : bodyParameters.entrySet()) {
        String key = entry.getKey();
        TypedBytes value = entry.getValue();
        writer.append("--").append(boundary);
        writer.append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"").append(key).append("\"");
        writer.append(CRLF);
        writer.append("Content-Type: ").append(value.mimeType().mimeName());
        writer.append(CRLF).append(CRLF).flush();
        value.writeTo(outputStream);
        writer.append(CRLF);
      }
      writer.append("--").append(boundary).append("--").flush();
      writer.close();
    }

    return connection;
  }

  static Response parseResponse(HttpURLConnection urlConnection) throws IOException {
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
