package retrofit.http.client;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import retrofit.http.Header;
import retrofit.io.TypedBytes;

/** Encapsulates all of the information necessary to make an HTTP request. */
public final class Request {
  private final String method;
  private final String url;
  private final List<Header> headers;
  private final TypedBytes body;
  private Map<String, TypedBytes> bodyParameters;

  public Request(String method, String url, List<Header> headers, TypedBytes body,
      Map<String, TypedBytes> bodyParameters) {
    if (method == null) {
      throw new IllegalArgumentException("Method must not be null.");
    }
    if (url == null) {
      throw new IllegalArgumentException("Url must not be null.");
    }
    this.method = method;
    this.url = url;

    if (headers == null) {
      headers = Collections.emptyList();
    }
    this.headers = Collections.unmodifiableList(headers);

    this.body = body;
    if (bodyParameters != null) {
      if (bodyParameters.size() == 0) {
        bodyParameters = null;
      } else {
        bodyParameters = Collections.unmodifiableMap(bodyParameters);
      }
    }
    this.bodyParameters = bodyParameters;
  }

  /** HTTP method verb. */
  public String getMethod() {
    return method;
  }

  /** Target URL. */
  public String getUrl() {
    return url;
  }

  /**
   * An unmodifiable list of headers.
   * <p/>
   * May be empty, never {@code null}.
   */
  public List<Header> getHeaders() {
    return headers;
  }

  /** Request body. May be {@code null}. */
  public TypedBytes getBody() {
    return body;
  }

  /**
   * Unmodifiable map of additional body parameters. When not {@code null} this indicates a
   * multi-part request is required.  May be {@code null} but never empty.
   */
  public Map<String, TypedBytes> getBodyParameters() {
    return bodyParameters;
  }
}
