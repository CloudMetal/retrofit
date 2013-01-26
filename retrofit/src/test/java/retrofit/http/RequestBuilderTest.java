// Copyright 2013 Square, Inc.
package retrofit.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Test;
import retrofit.http.client.Request;
import retrofit.io.TypedBytes;

import static org.fest.assertions.api.Assertions.assertThat;
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
    assertThat(request.getBodyParameters()).isNull();
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
    assertThat(request.getBodyParameters()).isNull();
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
    assertThat(request.getBodyParameters()).isNull();
  }

  @Test public void getWithPathAndQueryParam() throws Exception {
    Request request = new RequestBuilderHelper() //
        .setMethod("GET") //
        .setPath("foo/bar/{ping}/") //
        .addNamedParam("ping", "pong") //
        .addNamedParam("kit", "kat") //
        .addNamedParam("riff", "raff") //
        .build();
    assertThat(request.getMethod()).isEqualTo("GET");
    assertThat(request.getHeaders()).isEmpty();
    assertThat(request.getUrl()).isEqualTo(URL + "foo/bar/pong/?kit=kat&riff=raff");
    assertThat(request.getBody()).isNull();
    assertThat(request.getBodyParameters()).isNull();
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
    assertThat(request.getBodyParameters()).isNull();
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
    assertThat(request.getBodyParameters()).isNull();
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
    assertThat(request.getBodyParameters()).isNull();
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
    assertTypedBytes(request.getBody(), "{\"ping\":\"pong\"}");
    assertThat(request.getBodyParameters()).isNull();
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
    assertTypedBytes(request.getBody(),
        "{\"kit\":\"kat\",\"answer\":42,\"boom\":[\"goes\",\"the\",\"dynamite\"]}");
    assertThat(request.getBodyParameters()).isNull();
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
    assertTypedBytes(request.getBody(), "{\"kit\":\"kat\"}");
    assertThat(request.getBodyParameters()).isNull();
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
    assertTypedBytes(request.getBody(), "{\"kit\":\"kat\"}");
    assertThat(request.getBodyParameters()).isNull();
  }

  @Test public void singleEntity() throws Exception {
    Request request = new RequestBuilderHelper() //
        .setMethod("POST") //
        .setHasBody() //
        .setPath("foo/bar/") //
        .addSingleEntityParam(Arrays.asList("quick", "brown", "fox")) //
        .build();
    assertThat(request.getMethod()).isEqualTo("POST");
    assertThat(request.getHeaders()).isEmpty();
    assertThat(request.getUrl()).isEqualTo(URL + "foo/bar/");
    assertTypedBytes(request.getBody(), "[\"quick\",\"brown\",\"fox\"]");
    assertThat(request.getBodyParameters()).isNull();
  }

  @Test public void singleEntityWithPathParams() throws Exception {
    Request request = new RequestBuilderHelper() //
        .setMethod("POST") //
        .setHasBody() //
        .setPath("foo/bar/{ping}/{kit}/") //
        .addNamedParam("ping", "pong") //
        .addSingleEntityParam(Arrays.asList("quick", "brown", "fox")) //
        .addNamedParam("kit", "kat") //
        .build();
    assertThat(request.getMethod()).isEqualTo("POST");
    assertThat(request.getHeaders()).isEmpty();
    assertThat(request.getUrl()).isEqualTo(URL + "foo/bar/pong/kat/");
    assertTypedBytes(request.getBody(), "[\"quick\",\"brown\",\"fox\"]");
    assertThat(request.getBodyParameters()).isNull();
  }

  @Test public void singleEntityWithPathParamsAsync() throws Exception {
    Request request = new RequestBuilderHelper() //
        .setMethod("POST") //
        .setHasBody() //
        .setPath("foo/bar/{ping}/{kit}/") //
        .addNamedParam("ping", "pong") //
        .addSingleEntityParam(Arrays.asList("quick", "brown", "fox")) //
        .addNamedParam("kit", "kat") //
        .setAsynchronous() //
        .build();
    assertThat(request.getMethod()).isEqualTo("POST");
    assertThat(request.getHeaders()).isEmpty();
    assertThat(request.getUrl()).isEqualTo(URL + "foo/bar/pong/kat/");
    assertTypedBytes(request.getBody(), "[\"quick\",\"brown\",\"fox\"]");
    assertThat(request.getBodyParameters()).isNull();
  }

  @Test public void simpleMultipart() throws Exception {
    Request request = new RequestBuilderHelper() //
        .setMethod("POST") //
        .setHasBody() //
        .setPath("foo/bar/") //
        .addNamedParam("ping", "pong") //
        .addNamedParam("kit", new StringTypedBytes("kat")) //
        .setMultipart() //
        .build();
    assertThat(request.getMethod()).isEqualTo("POST");
    assertThat(request.getHeaders()).isEmpty();
    assertThat(request.getUrl()).isEqualTo(URL + "foo/bar/");
    assertThat(request.getBody()).isNull();
    assertThat(request.getBodyParameters()).hasSize(2);
    assertTypedBytes(request.getBodyParameters().get("ping"), "pong");
    assertTypedBytes(request.getBodyParameters().get("kit"), "kat");
  }

  @Test public void simpleHeaders() throws Exception {
    Request request = new RequestBuilderHelper() //
        .setMethod("GET") //
        .setPath("foo/bar/") //
        .addHeader("ping", "pong") //
        .addHeader("kit", "kat") //
        .build();
    assertThat(request.getMethod()).isEqualTo("GET");
    assertThat(request.getHeaders()) //
        .containsExactly(new Header("ping", "pong"), new Header("kit", "kat"));
    assertThat(request.getUrl()).isEqualTo(URL + "foo/bar/");
    assertThat(request.getBody()).isNull();
    assertThat(request.getBodyParameters()).isNull();
  }

  private static void assertTypedBytes(TypedBytes bytes, String expected) throws IOException {
    assertThat(bytes).isNotNull();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    bytes.writeTo(baos);
    assertThat(new String(baos.toByteArray(), "UTF-8")).isEqualTo(expected);
  }
}
