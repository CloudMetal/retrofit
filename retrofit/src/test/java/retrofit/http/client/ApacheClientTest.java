// Copyright 2013 Square, Inc.
package retrofit.http.client;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.mime.MultipartEntity;
import org.junit.Test;
import retrofit.http.Header;
import retrofit.http.StringTypedBytes;
import retrofit.io.TypedBytes;

import static org.fest.assertions.api.Assertions.assertThat;

public class ApacheClientTest {
  @Test public void get() {
    Request request = new Request("GET", "http://example.com/foo/bar/?kit=kat", null, null, null);
    HttpUriRequest apacheRequest = ApacheClient.createRequest(request);

    assertThat(apacheRequest.getMethod()).isEqualTo("GET");
    assertThat(apacheRequest.getURI().toString()).isEqualTo("http://example.com/foo/bar/?kit=kat");
    assertThat(apacheRequest.getAllHeaders()).isEmpty();

    if (apacheRequest instanceof HttpEntityEnclosingRequest) {
      HttpEntityEnclosingRequest entityRequest = (HttpEntityEnclosingRequest) apacheRequest;
      assertThat(entityRequest.getEntity()).isNull();
    }
  }

  @Test public void post() throws Exception {
    TypedBytes body = new StringTypedBytes("hi");
    Request request = new Request("POST", "http://example.com/foo/bar/", null, body, null);
    HttpUriRequest apacheRequest = ApacheClient.createRequest(request);

    assertThat(apacheRequest.getMethod()).isEqualTo("POST");
    assertThat(apacheRequest.getURI().toString()).isEqualTo("http://example.com/foo/bar/");
    assertThat(apacheRequest.getAllHeaders()).hasSize(0);

    assertThat(apacheRequest).isInstanceOf(HttpEntityEnclosingRequest.class);
    HttpEntityEnclosingRequest entityRequest = (HttpEntityEnclosingRequest) apacheRequest;
    HttpEntity entity = entityRequest.getEntity();
    assertThat(entity).isNotNull();
    assertBytes(Streams.readFully(entity.getContent()), "hi");
    assertThat(entity.getContentType().getValue()).isEqualTo("text/plain");
  }

  @Test public void multipart() {
    Map<String, TypedBytes> bodyParams = new LinkedHashMap<String, TypedBytes>();
    bodyParams.put("foo", new StringTypedBytes("bar"));
    bodyParams.put("ping", new StringTypedBytes("pong"));
    Request request = new Request("POST", "http://example.com/that/", null, null, bodyParams);
    HttpUriRequest apacheRequest = ApacheClient.createRequest(request);

    assertThat(apacheRequest.getMethod()).isEqualTo("POST");
    assertThat(apacheRequest.getURI().toString()).isEqualTo("http://example.com/that/");
    assertThat(apacheRequest.getAllHeaders()).hasSize(0);

    assertThat(apacheRequest).isInstanceOf(HttpEntityEnclosingRequest.class);
    HttpEntityEnclosingRequest entityRequest = (HttpEntityEnclosingRequest) apacheRequest;
    HttpEntity entity = entityRequest.getEntity();
    assertThat(entity).isInstanceOf(MultipartEntity.class);
    // TODO test more?
  }

  @Test public void headers() {
    List<Header> headers = new ArrayList<Header>();
    headers.add(new Header("kit", "kat"));
    headers.add(new Header("foo", "bar"));
    Request request = new Request("GET", "http://example.com/this/", headers, null, null);
    HttpUriRequest apacheRequest = ApacheClient.createRequest(request);

    assertThat(apacheRequest.getAllHeaders()).hasSize(2);
    org.apache.http.Header kit = apacheRequest.getFirstHeader("kit");
    assertThat(kit).isNotNull();
    assertThat(kit.getValue()).isEqualTo("kat");
    org.apache.http.Header foo = apacheRequest.getFirstHeader("foo");
    assertThat(foo).isNotNull();
    assertThat(foo.getValue()).isEqualTo("bar");
  }

  private static void assertBytes(byte[] bytes, String expected) throws Exception {
    assertThat(new String(bytes, "UTF-8")).isEqualTo(expected);
  }
}
