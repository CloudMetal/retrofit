// Copyright 2013 Square, Inc.
package retrofit.http;

import java.util.Arrays;
import org.junit.Test;
import retrofit.http.client.Request;
import retrofit.io.TypedBytes;

import static org.fest.assertions.api.Assertions.assertThat;
import static retrofit.http.GsonConverter.JsonTypedBytes;
import static retrofit.http.RequestBuilderHelper.URL;

public class RequestBuilderTest {
  @Test public void normalGet() throws Exception {
    Request request = new RequestBuilderHelper() //
        .setMethod("GET") //
        .setPath("foo/bar/") //
        .build();
    assertThat(request.getMethod()).isEqualTo("GET");
    assertThat(request.getHeaders()).isEmpty();
    assertThat(request.getUrl()).isEqualTo(URL + "foo/bar/");
    assertThat(request.getBody()).isNull();
  }

  @Test public void getWithPathParam() throws Exception {
    Request request = new RequestBuilderHelper() //
        .setMethod("GET") //
        .setPath("foo/bar/{ping}/") //
        .addNamedParam("ping", "pong") //
        .build();
    assertThat(request.getMethod()).isEqualTo("GET");
    assertThat(request.getHeaders()).isEmpty();
    assertThat(request.getUrl()).isEqualTo(URL + "foo/bar/pong/");
    assertThat(request.getBody()).isNull();
  }

  @Test public void getWithQueryParam() throws Exception {
    Request request = new RequestBuilderHelper() //
        .setMethod("GET") //
        .setPath("foo/bar/") //
        .addNamedParam("ping", "pong") //
        .build();
    assertThat(request.getMethod()).isEqualTo("GET");
    assertThat(request.getHeaders()).isEmpty();
    assertThat(request.getUrl()).isEqualTo(URL + "foo/bar/?ping=pong");
    assertThat(request.getBody()).isNull();
  }

  @Test public void getWithPathAndQueryParam() throws Exception {
    Request request = new RequestBuilderHelper() //
        .setMethod("GET") //
        .setPath("foo/bar/{ping}/") //
        .addNamedParam("ping", "pong") //
        .addNamedParam("kit", "kat") //
        .build();
    assertThat(request.getMethod()).isEqualTo("GET");
    assertThat(request.getHeaders()).isEmpty();
    assertThat(request.getUrl()).isEqualTo(URL + "foo/bar/pong/?kit=kat");
    assertThat(request.getBody()).isNull();
  }

  @Test public void getWithPathAndQueryParamAsync() throws Exception {
    Request request = new RequestBuilderHelper() //
        .setMethod("GET") //
        .setPath("foo/bar/{ping}/") //
        .addNamedParam("ping", "pong") //
        .addNamedParam("kit", "kat") //
        .setAsynchronous() //
        .build();
    assertThat(request.getMethod()).isEqualTo("GET");
    assertThat(request.getHeaders()).isEmpty();
    assertThat(request.getUrl()).isEqualTo(URL + "foo/bar/pong/?kit=kat");
    assertThat(request.getBody()).isNull();
  }

  @Test public void normalPost() throws Exception {
    Request request = new RequestBuilderHelper() //
        .setMethod("POST") //
        .setHasBody() //
        .setPath("foo/bar/") //
        .build();
    assertThat(request.getMethod()).isEqualTo("POST");
    assertThat(request.getHeaders()).isEmpty();
    assertThat(request.getUrl()).isEqualTo(URL + "foo/bar/");
    assertThat(request.getBody()).isNull();
  }

  @Test public void normalPostWithPathParam() throws Exception {
    Request request = new RequestBuilderHelper() //
        .setMethod("POST") //
        .setHasBody() //
        .setPath("foo/bar/{ping}/") //
        .addNamedParam("ping", "pong") //
        .build();
    assertThat(request.getMethod()).isEqualTo("POST");
    assertThat(request.getHeaders()).isEmpty();
    assertThat(request.getUrl()).isEqualTo(URL + "foo/bar/pong/");
    assertThat(request.getBody()).isNull();
  }

  @Test public void normalPostWithBodyParam() throws Exception {
    Request request = new RequestBuilderHelper() //
        .setMethod("POST") //
        .setHasBody() //
        .setPath("foo/bar/") //
        .addNamedParam("ping", "pong") //
        .build();
    assertThat(request.getMethod()).isEqualTo("POST");
    assertThat(request.getHeaders()).isEmpty();
    assertThat(request.getUrl()).isEqualTo(URL + "foo/bar/");
    assertBody(request, "{\"ping\":\"pong\"}");
  }

  @Test public void normalPostWithMultipleBodyParam() throws Exception {
    Request request = new RequestBuilderHelper() //
        .setMethod("POST") //
        .setHasBody() //
        .setPath("foo/bar/") //
        .addNamedParam("kit", "kat") //
        .addNamedParam("answer", 42) //
        .addNamedParam("boom", Arrays.asList("goes", "the", "dynamite")) //
        .build();
    assertThat(request.getMethod()).isEqualTo("POST");
    assertThat(request.getHeaders()).isEmpty();
    assertThat(request.getUrl()).isEqualTo(URL + "foo/bar/");
    assertBody(request, "{\"kit\":\"kat\",\"answer\":42,\"boom\":[\"goes\",\"the\",\"dynamite\"]}");
  }

  @Test public void normalPostWithPathAndBodyParam() throws Exception {
    Request request = new RequestBuilderHelper() //
        .setMethod("POST") //
        .setHasBody() //
        .setPath("foo/bar/{ping}/") //
        .addNamedParam("ping", "pong") //
        .addNamedParam("kit", "kat") //
        .build();
    assertThat(request.getMethod()).isEqualTo("POST");
    assertThat(request.getHeaders()).isEmpty();
    assertThat(request.getUrl()).isEqualTo(URL + "foo/bar/pong/");
    assertBody(request, "{\"kit\":\"kat\"}");
  }

  @Test public void normalPostWithPathAndBodyParamAsync() throws Exception {
    Request request = new RequestBuilderHelper() //
        .setMethod("POST") //
        .setHasBody() //
        .setPath("foo/bar/{ping}/") //
        .addNamedParam("ping", "pong") //
        .addNamedParam("kit", "kat") //
        .setAsynchronous() //
        .build();
    assertThat(request.getMethod()).isEqualTo("POST");
    assertThat(request.getHeaders()).isEmpty();
    assertThat(request.getUrl()).isEqualTo(URL + "foo/bar/pong/");
    assertBody(request, "{\"kit\":\"kat\"}");
  }

  // TODO test single entities

  // TODO test multi-part

  ////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////

  private static void assertBody(Request request, String expected) {
    TypedBytes body = request.getBody();
    assertThat(body).isNotNull().isInstanceOf(JsonTypedBytes.class);
    JsonTypedBytes jsonBody = (JsonTypedBytes) body;
    assertThat(new String(jsonBody.jsonBytes)).isEqualTo(expected);
  }
}
