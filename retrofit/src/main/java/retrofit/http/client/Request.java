package retrofit.http.client;

import java.util.Collections;
import java.util.List;
import retrofit.http.Header;
import retrofit.io.TypedBytes;

/** Encapsulates all of the information necessary to make an HTTP request. */
public class Request {
  public enum Method {
    GET(false),
    POST(true),
    PUT(true),
    DELETE(false),
    HEAD(false);

    public final boolean hasBody;

    private Method(boolean hasBody) {
      this.hasBody = hasBody;
    }
  }

  private final Method method;
  private final String url;
  private final List<Header> headers;
  private final TypedBytes body;

  public Request(Method method, String url, List<Header> headers, TypedBytes body) {
    this.method = method;
    this.url = url;
    this.body = body;

    if (headers == null) {
      headers = Collections.emptyList();
    }
    this.headers = Collections.unmodifiableList(headers);
  }

  /** HTTP method verb. */
  public Method getMethod() {
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
