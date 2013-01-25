package retrofit.http.client;

import java.util.Collections;
import java.util.List;
import retrofit.http.Header;
import retrofit.io.TypedBytes;

/** Encapsulates all of the information necessary to make an HTTP request. */
public final class Request {
  private final String method;
  private final String url;
  private final List<Header> headers;
  private final TypedBytes body;

  public Request(String method, String url, List<Header> headers, TypedBytes body) {
    this.method = method;
    this.url = url;
    this.body = body;

    if (headers == null) {
      headers = Collections.emptyList();
    }
    this.headers = Collections.unmodifiableList(headers);
  }

  /** HTTP method verb. */
  public String getMethod() {
    return method;
  }

  /** Target URL. */
  public String getUrl() {
    return url;
  }

  /** An unmodifiable list of headers. */
  public List<Header> getHeaders() {
    return headers;
  }

  /** Request body. May be {@code null}. */
  public TypedBytes getBody() {
    return body;
  }
}
