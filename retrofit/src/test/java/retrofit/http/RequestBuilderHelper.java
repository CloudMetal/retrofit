// Copyright 2013 Square, Inc.
package retrofit.http;

import com.google.gson.Gson;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import retrofit.http.client.Request;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static retrofit.http.RestMethodInfo.NO_SINGLE_ENTITY;

public class RequestBuilderHelper {
  public static final Converter GSON = new GsonConverter(new Gson());
  public static final String URL = "http://example.com/";

  private boolean isSynchronous = true;
  private boolean isMultipart = false;
  private String method;
  private boolean hasBody = false;
  private String path;
  private Set<String> pathParams;
  private final List<QueryParam> queryParams = new ArrayList<QueryParam>();
  private final List<String> namedParams = new ArrayList<String>();
  private final List<Object> args = new ArrayList<Object>();
  private final List<Header> headers = new ArrayList<Header>();
  private int singleEntityArgumentIndex = NO_SINGLE_ENTITY;

  public RequestBuilderHelper setAsynchronous() {
    isSynchronous = false;
    return this;
  }

  public RequestBuilderHelper setMethod(String method) {
    this.method = method;
    return this;
  }

  public RequestBuilderHelper setHasBody() {
    hasBody = true;
    return this;
  }

  public RequestBuilderHelper setPath(String path) {
    this.path = path;
    pathParams = RestMethodInfo.parsePathParameters(path);
    return this;
  }

  public RequestBuilderHelper addQueryParam(String name, String value) {
    QueryParam queryParam = mock(QueryParam.class);
    when(queryParam.name()).thenReturn(name);
    when(queryParam.value()).thenReturn(value);
    queryParams.add(queryParam);
    return this;
  }

  public RequestBuilderHelper addNamedParam(String name, Object value) {
    if (name == null) {
      throw new IllegalArgumentException("Name can not be null.");
    }
    namedParams.add(name);
    args.add(value);
    return this;
  }

  public RequestBuilderHelper addSingleEntityParam(Object value) {
    if (singleEntityArgumentIndex != NO_SINGLE_ENTITY) {
      throw new IllegalStateException("Single entity param already added.");
    }
    singleEntityArgumentIndex = namedParams.size(); // Relying on the fact that this is already less one.
    namedParams.add(null);
    args.add(value);
    return this;
  }

  public RequestBuilderHelper addHeader(String name, String value) {
    headers.add(new Header(name, value));
    return this;
  }

  public RequestBuilderHelper setMultipart() {
    isMultipart = true;
    return this;
  }

  public Request build() throws NoSuchMethodException, URISyntaxException {
    if (method == null) {
      throw new IllegalStateException("Method must be set.");
    }
    if (path == null) {
      throw new IllegalStateException("Path must be set.");
    }

    final Method method;
    if (isSynchronous) {
      method = getClass().getDeclaredMethod("dummySync");
    } else {
      method = getClass().getDeclaredMethod("dummyAsync", Callback.class);
      args.add(mock(Callback.class));
    }

    // Create a fake rest method annotation based on set values.
    RestMethod restMethod = mock(RestMethod.class);
    when(restMethod.hasBody()).thenReturn(hasBody);
    when(restMethod.value()).thenReturn(this.method);

    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.restMethod = restMethod;
    methodInfo.path = path;
    methodInfo.pathParams = pathParams;
    methodInfo.pathQueryParams = queryParams.toArray(new QueryParam[queryParams.size()]);
    methodInfo.namedParams = namedParams.toArray(new String[namedParams.size()]);
    methodInfo.singleEntityArgumentIndex = singleEntityArgumentIndex;
    methodInfo.isMultipart = isMultipart;
    methodInfo.loaded = true;

    return new RequestBuilder(GSON)
        .setApiUrl(URL)
        .setHeaders(headers)
        .setArgs(args.toArray(new Object[args.size()]))
        .setMethodInfo(methodInfo)
        .build();
  }

  private Object dummySync() {
    return null;
  }

  private void dummyAsync(Callback<Object> cb) {
  }
}
