package retrofit.http.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.content.AbstractContentBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import retrofit.http.Header;
import retrofit.io.TypedBytes;

/** A {@link Client} which uses an implementation of Apache's {@link HttpClient}. */
public class ApacheClient implements Client {
  private final HttpClient client;

  /** Creates an instance backed by {@link DefaultHttpClient}. */
  public ApacheClient() {
    this(new DefaultHttpClient());
  }

  public ApacheClient(HttpClient client) {
    this.client = client;
  }

  @Override public Response execute(Request request) throws IOException {
    // Create and prepare the Apache request object.
    HttpUriRequest apacheRequest = createRequest(request);
    prepareRequest(apacheRequest);

    // Obtain and prepare the Apache response object.
    HttpResponse apacheResponse = client.execute(apacheRequest);
    prepareResponse(apacheResponse);

    return parseResponse(apacheResponse);
  }

  /** Callback for additional preparation of the request before execution. */
  protected void prepareRequest(HttpUriRequest request) {
  }

  /** Callback for additional preparation of the response before parsing. */
  protected void prepareResponse(HttpResponse response) {
  }

  private static HttpUriRequest createRequest(Request request) {
    String url = request.getUrl();
    switch (request.getMethod()) {
      case GET:
        HttpGet get = new HttpGet(url);
        addHeaders(request, get);
        return get;
      case DELETE:
        HttpDelete delete = new HttpDelete(url);
        addHeaders(request, delete);
        return delete;
      case HEAD:
        HttpHead head = new HttpHead(url);
        addHeaders(request, head);
        return head;
      case POST:
        HttpPost post = new HttpPost(url);
        addHeaders(request, post);
        addBody(request, post);
        return post;
      case PUT:
        HttpPut put = new HttpPut(url);
        addHeaders(request, put);
        addBody(request, put);
        return put;
      default:
        throw new IllegalArgumentException("Unknown request type: " + request.getMethod());
    }
  }

  private static void addBody(Request request, HttpEntityEnclosingRequest apacheRequest) {
    // Set the request body, if applicable.
    // TODO what do we do about multi-part?
    TypedBytes body = request.getBody();
    if (body != null) {
      apacheRequest.setEntity(new TypedBytesEntity(body));
    }
  }

  private static void addHeaders(Request request, HttpUriRequest apacheRequest) {
    List<Header> headers = request.getHeaders();
    for (Header header : headers) {
      apacheRequest.setHeader(new BasicHeader(header.getName(), header.getValue()));
    }
  }

  private static Response parseResponse(HttpResponse response) throws IOException {
    byte[] body = EntityUtils.toByteArray(response.getEntity());

    StatusLine statusLine = response.getStatusLine();
    int status = statusLine.getStatusCode();
    String reason = statusLine.getReasonPhrase();

    List<Header> headers = new ArrayList<Header>();
    for (org.apache.http.Header header : response.getAllHeaders()) {
      headers.add(new Header(header.getName(), header.getValue()));
    }

    return new Response(status, reason, headers, body);
  }

  /** Adapts ContentBody to TypedBytes. */
  private static class TypedBytesBody extends AbstractContentBody {
    private final TypedBytes typedBytes;
    private final String name;

    TypedBytesBody(TypedBytes typedBytes, String baseName) {
      super(typedBytes.mimeType().mimeName());
      this.typedBytes = typedBytes;

      String name = baseName;
      String ext = typedBytes.mimeType().extension();
      if (ext != null) {
        name += "." + ext;
      }
      this.name = name;
    }

    @Override public long getContentLength() {
      return typedBytes.length();
    }

    @Override public String getFilename() {
      return name;
    }

    @Override public String getCharset() {
      return null;
    }

    @Override public String getTransferEncoding() {
      return MIME.ENC_BINARY;
    }

    @Override public void writeTo(OutputStream out) throws IOException {
      // Note: We probably want to differentiate I/O errors that occur while reading a file from
      // network errors. Network operations can be retried. File operations will probably continue
      // to fail.
      //
      // In the case of photo uploads, we at least check that the file exists before we even try to
      // upload it.
      typedBytes.writeTo(out);
    }
  }

  /** Container class for passing an entire {@link TypedBytes} as an HTTP request. */
  private static class TypedBytesEntity extends AbstractHttpEntity {
    private final TypedBytes typedBytes;

    TypedBytesEntity(TypedBytes typedBytes) {
      this.typedBytes = typedBytes;
      setContentType(typedBytes.mimeType().mimeName());
    }

    @Override public boolean isRepeatable() {
      return true;
    }

    @Override public long getContentLength() {
      return typedBytes.length();
    }

    @Override public InputStream getContent() throws IOException {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      typedBytes.writeTo(out);
      return new ByteArrayInputStream(out.toByteArray());
    }

    @Override public void writeTo(OutputStream out) throws IOException {
      typedBytes.writeTo(out);
    }

    @Override public boolean isStreaming() {
      return false;
    }
  }
}
