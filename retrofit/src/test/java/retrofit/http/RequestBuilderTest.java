// Copyright 2012 Square, Inc.
package retrofit.http;

import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.UUID;
import javax.inject.Named;
import org.junit.Test;
import retrofit.http.client.Request;

import static org.fest.assertions.api.Assertions.assertThat;
import static retrofit.http.RestAdapter.MethodDetails;

/** @author Eric Denman (edenman@squareup.com) */
public class RequestBuilderTest {
  private static final Gson GSON = new Gson();
  private static final String API_URL = "http://taqueria.com/lengua/taco";

  @Test public void testNormalGet() throws Exception {
    Method method = getTestMethod("normalGet");
    String expectedId = UUID.randomUUID().toString();
    Object[] args = new Object[] { expectedId, new MyCallback() };
    Request request = build(method, args);

    assertThat(request.getMethod()).isEqualTo("GET");

    // Make sure the url param got translated.
    assertThat(request.getUrl()).isEqualTo(API_URL + "/foo/bar?id=" + expectedId);
  }

  @Test public void testGetWithPathParam() throws Exception {
    Method method = getTestMethod("getWithPathParam");
    String expectedId = UUID.randomUUID().toString();
    String category = UUID.randomUUID().toString();
    Object[] args = new Object[] { expectedId, category, new MyCallback() };
    Request request = build(method, args);

    assertThat(request.getMethod()).isEqualTo("GET");

    // Make sure the url param got translated.
    assertThat(request.getUrl()) //
        .isEqualTo(API_URL + "/foo/" + expectedId + "/bar?category=" + category);
  }

  @Test public void testGetWithPathParamAndWhitespaceValue() throws Exception {
    Method method = getTestMethod("getWithPathParam");
    String expectedId = "I have spaces buddy";
    String category = UUID.randomUUID().toString();
    Object[] args = new Object[] { expectedId, category, new MyCallback() };
    Request request = build(method, args);

    assertThat(request.getMethod()).isEqualTo("GET");

    // Make sure the url param got translated.
    assertThat(request.getUrl()).isEqualTo(
        API_URL + "/foo/" + URLEncoder.encode(expectedId, "UTF-8") + "/bar?category=" + category);
  }

  @Test public void testSingleEntityWithPathParams() throws Exception {
    Method method = getTestMethod("singleEntityPut");
    String expectedId = UUID.randomUUID().toString();
    String bodyText = UUID.randomUUID().toString();
    Object[] args = new Object[] { new MyJsonObj(bodyText), expectedId, new MyCallback() };
    Request request = build(method, args);

    assertThat(request.getMethod()).isEqualTo("PUT");

    // Make sure the url param got translated.
    assertThat(request.getUrl()).isEqualTo(API_URL + "/foo/bar/" + expectedId);

    // Make sure the request body has the json string.
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    request.getBody().writeTo(out);
    final String requestBody = out.toString();
    assertThat(requestBody).isEqualTo("{\"bodyText\":\"" + bodyText + "\"}");
  }

  @Test public void testNormalPutWithPathParams() throws Exception {
    Method method = getTestMethod("normalPut");
    String expectedId = UUID.randomUUID().toString();
    String bodyText = UUID.randomUUID().toString();
    Object[] args = new Object[] { expectedId, bodyText, new MyCallback() };
    Request request = build(method, args);

    assertThat(request.getMethod()).isEqualTo("PUT");

    // Make sure the url param got translated.
    assertThat(request.getUrl()).isEqualTo(API_URL + "/foo/bar/" + expectedId);

    // Make sure the request body has the json string.
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    request.getBody().writeTo(out);
    String requestBody = out.toString();
    assertThat(requestBody).isEqualTo("{\"body\":\"" + bodyText + "\"}");
  }

  @Test(expected = IllegalStateException.class)
  public void testSingleEntityWithTooManyParams() throws Exception {
    Method method = getTestMethod("tooManyParams");
    String expectedId = UUID.randomUUID().toString();
    String bodyText = UUID.randomUUID().toString();
    Object[] args = new Object[] { new MyJsonObj(bodyText), expectedId, "EXTRA", new MyCallback() };
    build(method, args);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSingleEntityWithNoPathParam() throws Exception {
    Method method = getTestMethod("singleEntityNoPathParam");
    String bodyText = UUID.randomUUID().toString();
    Object[] args = new Object[] { new MyJsonObj(bodyText), new MyCallback() };
    build(method, args);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRegularWithNoPathParam() throws Exception {
    Method method = getTestMethod("regularNoPathParam");
    String otherParam = UUID.randomUUID().toString();
    Object[] args = new Object[] { otherParam, new MyCallback() };
    build(method, args);
  }

  @SuppressWarnings({ "UnusedDeclaration" }) // Methods are accessed by reflection.
  private interface MyService {
    @GET("foo/bar") void normalGet(@Named("id") String id, Callback<SimpleResponse> callback);

    @GET("foo/{id}/bar")
    void getWithPathParam(@Named("id") String id, @Named("category") String category,
        Callback<SimpleResponse> callback);

    @PUT("foo/bar/{id}") void singleEntityPut(@SingleEntity MyJsonObj card, @Named("id") String id,
        Callback<SimpleResponse> callback);

    @PUT("foo/bar/{id}") void normalPut(@Named("id") String id, @Named("body") String body,
        Callback<SimpleResponse> callback);

    @PUT("foo/bar/{id}") void tooManyParams(@SingleEntity MyJsonObj card, @Named("id") String id,
        @Named("extra") String extraParam, Callback<SimpleResponse> callback);

    @PUT("foo/bar/{id}")
    void singleEntityNoPathParam(@SingleEntity MyJsonObj card, Callback<SimpleResponse> callback);

    @PUT("foo/bar/{id}")
    void regularNoPathParam(@Named("other") String other, Callback<SimpleResponse> callback);
  }

  private static Method getTestMethod(String name) {
    Method[] methods = MyService.class.getDeclaredMethods();
    for (Method method : methods) {
      if (method.getName().equals(name)) {
        return method;
      }
    }
    throw new IllegalArgumentException("Unknown method '" + name + "' on MyService");
  }

  private Request build(Method method, Object[] args) throws URISyntaxException {
    MethodDetails methodDetails = new MethodDetails(method);
    methodDetails.init();
    return new RequestBuilder(new GsonConverter(GSON)) //
        .setMethod(methodDetails) //
        .setArgs(args) //
        .setApiUrl(API_URL) //
        .build();
  }

  private static class MyJsonObj {
    @SuppressWarnings({ "UnusedDeclaration" }) // Accessed by json serialization.
    private String bodyText;

    public MyJsonObj(String bodyText) {
      this.bodyText = bodyText;
    }
  }

  private static class SimpleResponse {}

  private class MyCallback implements Callback<SimpleResponse> {
    @Override public void success(SimpleResponse simpleResponse) {}
    @Override public void failure(RetrofitError error) {}
  }
}
