// Copyright 2013 Square, Inc.
package retrofit.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Test;
import retrofit.http.client.Request;
import retrofit.io.TypedBytes;

import static org.fest.assertions.api.Assertions.assertThat;

public class RequestBuilderTest {
  @Test public void normalGet() throws Exception {
    Request request = new RequestBuilderHelper() //
        .setMethod("GET") //
        .setUrl("http://example.com") //
        .setPath("/foo/bar/") //
        .build();
    assertThat(request.getMethod()).isEqualTo("GET");
    assertThat(request.getHeaders()).isEmpty();
    assertThat(request.getUrl()).isEqualTo("http://example.com/foo/bar/");
    assertThat(request.getBody()).isNull();
    assertThat(request.getBodyParameters()).isEmpty();
  }

  @Test public void getWithPathParam() throws Exception {
    Request request = new RequestBuilderHelper() //
        .setMethod("GET") //
        .setUrl("http://example.com") //
        .setPath("/foo/bar/{ping}/") //
        .addNamedParam("ping", "pong") //
        .build();
    assertThat(request.getMethod()).isEqualTo("GET");
    assertThat(request.getHeaders()).isEmpty();
    assertThat(request.getUrl()).isEqualTo("http://example.com/foo/bar/pong/");
    assertThat(request.getBody()).isNull();
    assertThat(request.getBodyParameters()).isEmpty();
  }

  @Test public void getWithQueryParam() throws Exception {
    Request request = new RequestBuilderHelper() //
        .setMethod("GET") //
        .setUrl("http://example.com") //
        .setPath("/foo/bar/") //
        .addNamedParam("ping", "pong") //
        .build();
    assertThat(request.getMethod()).isEqualTo("GET");
    assertThat(request.getHeaders()).isEmpty();
    assertThat(request.getUrl()).isEqualTo("http://example.com/foo/bar/?ping=pong");
    assertThat(request.getBody()).isNull();
    assertThat(request.getBodyParameters()).isEmpty();
  }

  @Test public void getWithPathAndQueryParam() throws Exception {
    Request request = new RequestBuilderHelper() //
        .setMethod("GET") //
        .setUrl("http://example.com") //
        .setPath("/foo/bar/{ping}/") //
        .addNamedParam("ping", "pong") //
        .addNamedParam("kit", "kat") //
        .addNamedParam("riff", "raff") //
        .build();
    assertThat(request.getMethod()).isEqualTo("GET");
    assertThat(request.getHeaders()).isEmpty();
    assertThat(request.getUrl()).isEqualTo("http://example.com/foo/bar/pong/?kit=kat&riff=raff");
    assertThat(request.getBody()).isNull();
    assertThat(request.getBodyParameters()).isEmpty();
  }

  @Test public void getWithPathAndQueryParamAsync() throws Exception {
    Request request = new RequestBuilderHelper() //
        .setMethod("GET") //
        .setUrl("http://example.com") //
        .setPath("/foo/bar/{ping}/") //
        .addNamedParam("ping", "pong") //
        .addNamedParam("kit", "kat") //
        .setAsynchronous() //
        .build();
    assertThat(request.getMethod()).isEqualTo("GET");
    assertThat(request.getHeaders()).isEmpty();
    assertThat(request.getUrl()).isEqualTo("http://example.com/foo/bar/pong/?kit=kat");
    assertThat(request.getBody()).isNull();
    assertThat(request.getBodyParameters()).isEmpty();
  }

  @Test public void normalPost() throws Exception {
    Request request = new RequestBuilderHelper() //
        .setMethod("POST") //
        .setHasBody() //
        .setUrl("http://example.com") //
        .setPath("/foo/bar/") //
        .build();
    assertThat(request.getMethod()).isEqualTo("POST");
    assertThat(request.getHeaders()).isEmpty();
    assertThat(request.getUrl()).isEqualTo("http://example.com/foo/bar/");
    assertThat(request.getBody()).isNull();
    assertThat(request.getBodyParameters()).isEmpty();
  }

  @Test public void normalPostWithPathParam() throws Exception {
    Request request = new RequestBuilderHelper() //
        .setMethod("POST") //
        .setHasBody() //
        .setUrl("http://example.com") //
        .setPath("/foo/bar/{ping}/") //
        .addNamedParam("ping", "pong") //
        .build();
    assertThat(request.getMethod()).isEqualTo("POST");
    assertThat(request.getHeaders()).isEmpty();
    assertThat(request.getUrl()).isEqualTo("http://example.com/foo/bar/pong/");
    assertThat(request.getBody()).isNull();
    assertThat(request.getBodyParameters()).isEmpty();
  }

  @Test public void singleEntity() throws Exception {
    Request request = new RequestBuilderHelper() //
        .setMethod("POST") //
        .setHasBody() //
        .setUrl("http://example.com") //
        .setPath("/foo/bar/") //
        .addSingleEntityParam(Arrays.asList("quick", "brown", "fox")) //
        .build();
    assertThat(request.getMethod()).isEqualTo("POST");
    assertThat(request.getHeaders()).isEmpty();
    assertThat(request.getUrl()).isEqualTo("http://example.com/foo/bar/");
    assertTypedBytes(request.getBody(), "[\"quick\",\"brown\",\"fox\"]");
    assertThat(request.getBodyParameters()).isEmpty();
  }

  @Test public void singleEntityWithPathParams() throws Exception {
    Request request = new RequestBuilderHelper() //
        .setMethod("POST") //
        .setHasBody() //
        .setUrl("http://example.com") //
        .setPath("/foo/bar/{ping}/{kit}/") //
        .addNamedParam("ping", "pong") //
        .addSingleEntityParam(Arrays.asList("quick", "brown", "fox")) //
        .addNamedParam("kit", "kat") //
        .build();
    assertThat(request.getMethod()).isEqualTo("POST");
    assertThat(request.getHeaders()).isEmpty();
    assertThat(request.getUrl()).isEqualTo("http://example.com/foo/bar/pong/kat/");
    assertTypedBytes(request.getBody(), "[\"quick\",\"brown\",\"fox\"]");
    assertThat(request.getBodyParameters()).isEmpty();
  }

  @Test public void singleEntityWithPathParamsAsync() throws Exception {
    Request request = new RequestBuilderHelper() //
        .setMethod("POST") //
        .setHasBody() //
        .setUrl("http://example.com") //
        .setPath("/foo/bar/{ping}/{kit}/") //
        .addNamedParam("ping", "pong") //
        .addSingleEntityParam(Arrays.asList("quick", "brown", "fox")) //
        .addNamedParam("kit", "kat") //
        .setAsynchronous() //
        .build();
    assertThat(request.getMethod()).isEqualTo("POST");
    assertThat(request.getHeaders()).isEmpty();
    assertThat(request.getUrl()).isEqualTo("http://example.com/foo/bar/pong/kat/");
    assertTypedBytes(request.getBody(), "[\"quick\",\"brown\",\"fox\"]");
    assertThat(request.getBodyParameters()).isEmpty();
  }

  @Test public void simpleMultipart() throws Exception {
    Request request = new RequestBuilderHelper() //
        .setMethod("POST") //
        .setHasBody() //
        .setUrl("http://example.com") //
        .setPath("/foo/bar/") //
        .addNamedParam("ping", "pong") //
        .addNamedParam("kit", new StringTypedBytes("kat")) //
        .setMultipart() //
        .build();
    assertThat(request.getMethod()).isEqualTo("POST");
    assertThat(request.getHeaders()).isEmpty();
    assertThat(request.getUrl()).isEqualTo("http://example.com/foo/bar/");
    assertThat(request.getBody()).isNull();
    assertThat(request.getBodyParameters()).hasSize(2);
    assertTypedBytes(request.getBodyParameters().get("ping"), "pong");
    assertTypedBytes(request.getBodyParameters().get("kit"), "kat");
  }

  @Test public void simpleHeaders() throws Exception {
    Request request = new RequestBuilderHelper() //
        .setMethod("GET") //
        .setUrl("http://example.com") //
        .setPath("/foo/bar/") //
        .addHeader("ping", "pong") //
        .addHeader("kit", "kat") //
        .build();
    assertThat(request.getMethod()).isEqualTo("GET");
    assertThat(request.getHeaders()) //
        .containsExactly(new Header("ping", "pong"), new Header("kit", "kat"));
    assertThat(request.getUrl()).isEqualTo("http://example.com/foo/bar/");
    assertThat(request.getBody()).isNull();
    assertThat(request.getBodyParameters()).isEmpty();
  }

  @Test public void noDuplicateSlashes() throws Exception {
    Request request = new RequestBuilderHelper() //
        .setMethod("GET") //
        .setUrl("http://example.com/") //
        .setPath("/foo/bar/") //
        .build();
    assertThat(request.getUrl()).isEqualTo("http://example.com/foo/bar/");
  }

  private static void assertTypedBytes(TypedBytes bytes, String expected) throws IOException {
    assertThat(bytes).isNotNull();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    bytes.writeTo(baos);
    assertThat(new String(baos.toByteArray(), "UTF-8")).isEqualTo(expected);
  }
}
