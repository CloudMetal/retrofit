// Copyright 2012 Square, Inc.
package retrofit.http;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.Executor;
import javax.inject.Named;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicStatusLine;
import org.easymock.Capture;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import retrofit.http.client.ApacheClient;
import retrofit.http.testing.Response;
import retrofit.http.testing.ResponseCallback;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;

@Ignore
public class OldRestAdapterTest {
  private static final String ID = "123";
  private static final String ENTITY = "entity";
  private static final String ENTITY_PATH_PARAM = "entity/{id}";
  private static final String BASE_URL = "http://host/api/entity";
  private static final String PATH_URL_PREFIX = BASE_URL + "/";
  private static final String GET_DELETE_SIMPLE_URL = BASE_URL;
  private static final String GET_DELETE_SIMPLE_URL_WITH_PARAMS = GET_DELETE_SIMPLE_URL + "?";
  private static final Gson GSON = new Gson();
  private static final Response RESPONSE = new Response("some text");

  private RestAdapter restAdapter;
  private HttpClient mockHttpClient;
  private Executor mockHttpExecutor;
  private Executor mockCallbackExecutor;
  private ResponseCallback mockCallback;
  private HttpResponse mockResponse;

  @SuppressWarnings("unchecked") // Mock List to List<Header>
  @Before public void setUp() throws Exception {
    mockHttpClient = createMock(HttpClient.class);
    mockHttpExecutor = createMock(Executor.class);
    mockCallbackExecutor = createMock(Executor.class);
    mockCallback = createMock(ResponseCallback.class);
    mockResponse = createMock(HttpResponse.class);

    restAdapter = new RestAdapter.Builder() //
        .setServer("http://host/api/")
        .setClient(new ApacheClient(mockHttpClient))
        .setExecutors(mockHttpExecutor, mockCallbackExecutor)
        .setConverter(new GsonConverter(GSON))
        .build();
  }

  @Test public void testServiceDeleteSimpleAsync() throws IOException {
    expectAsyncLifecycle(HttpDelete.class, GET_DELETE_SIMPLE_URL);
    replayAll();

    DeleteService service = restAdapter.create(DeleteService.class);
    service.delete(mockCallback);
    verifyAll();
  }

  @Test public void testServiceDeleteSimpleSync() throws IOException {
    expectSyncLifecycle(HttpDelete.class, GET_DELETE_SIMPLE_URL);
    replayAll();

    DeleteService service = restAdapter.create(DeleteService.class);
    Response response = service.delete();
    assertThat(response).isEqualTo(RESPONSE);
    verifyAll();
  }

  @Test public void testServiceDeleteParamAsync() throws IOException {
    expectAsyncLifecycle(HttpDelete.class, GET_DELETE_SIMPLE_URL_WITH_PARAMS + "id=" + ID);
    replayAll();

    DeleteService service = restAdapter.create(DeleteService.class);
    service.deleteWithParam(ID, mockCallback);
    verifyAll();
  }

  @Test public void testServiceDeleteParamSync() throws IOException {
    expectSyncLifecycle(HttpDelete.class, GET_DELETE_SIMPLE_URL_WITH_PARAMS + "id=" + ID);
    replayAll();

    DeleteService service = restAdapter.create(DeleteService.class);
    Response response = service.deleteWithParam(ID);
    assertThat(response).isEqualTo(RESPONSE);
    verifyAll();
  }

  @Test public void testServiceDeleteWithFixedParamAsync() throws IOException {
    expectAsyncLifecycle(HttpDelete.class,
        GET_DELETE_SIMPLE_URL_WITH_PARAMS + "filter=merchant&id=" + ID);
    replayAll();

    DeleteService service = restAdapter.create(DeleteService.class);
    service.deleteWithFixedParam(ID, mockCallback);
    verifyAll();
  }

  @Test public void testServiceDeleteWithFixedParamSync() throws IOException {
    expectSyncLifecycle(HttpDelete.class,
        GET_DELETE_SIMPLE_URL_WITH_PARAMS + "filter=merchant&id=" + ID);
    replayAll();

    DeleteService service = restAdapter.create(DeleteService.class);
    Response response = service.deleteWithFixedParam(ID);
    assertThat(response).isEqualTo(RESPONSE);
    verifyAll();
  }

  @Test public void testServiceDeleteWithMultipleFixedParamAsync() throws IOException {
    expectAsyncLifecycle(HttpDelete.class,
        GET_DELETE_SIMPLE_URL_WITH_PARAMS + "filter=merchant&name2=value2&" + "id=" + ID);
    replayAll();

    DeleteService service = restAdapter.create(DeleteService.class);
    service.deleteWithMultipleFixedParams(ID, mockCallback);
    verifyAll();
  }

  @Test public void testServiceDeleteWithMultipleFixedParamSync() throws IOException {
    expectSyncLifecycle(HttpDelete.class,
        GET_DELETE_SIMPLE_URL_WITH_PARAMS + "filter=merchant&name2=value2&" + "id=" + ID);
    replayAll();

    DeleteService service = restAdapter.create(DeleteService.class);
    Response response = service.deleteWithMultipleFixedParams(ID);
    assertThat(response).isEqualTo(RESPONSE);
    verifyAll();
  }

  @Test public void testServiceDeletePathParamAsync() throws IOException {
    expectAsyncLifecycle(HttpDelete.class, PATH_URL_PREFIX + ID);
    replayAll();

    DeleteService service = restAdapter.create(DeleteService.class);
    service.deleteWithPathParam(ID, mockCallback);
    verifyAll();
  }

  @Test public void testServiceDeletePathParamSync() throws IOException {
    expectSyncLifecycle(HttpDelete.class, PATH_URL_PREFIX + ID);
    replayAll();

    DeleteService service = restAdapter.create(DeleteService.class);
    Response response = service.deleteWithPathParam(ID);
    assertThat(response).isEqualTo(RESPONSE);
    verifyAll();
  }

  @Test public void testServiceGetSimpleAsync() throws IOException {
    expectAsyncLifecycle(HttpGet.class, GET_DELETE_SIMPLE_URL);
    replayAll();

    GetService service = restAdapter.create(GetService.class);
    service.get(mockCallback);
    verifyAll();
  }

  @Test public void testServiceGetSimpleSync() throws IOException {
    expectSyncLifecycle(HttpGet.class, GET_DELETE_SIMPLE_URL);
    replayAll();

    GetService service = restAdapter.create(GetService.class);
    Response response = service.get();
    assertThat(response).isEqualTo(RESPONSE);
    verifyAll();
  }

  @Test public void testServiceGetParamAsync() throws IOException {
    expectAsyncLifecycle(HttpGet.class, GET_DELETE_SIMPLE_URL_WITH_PARAMS + "id=" + ID);
    replayAll();

    GetService service = restAdapter.create(GetService.class);
    service.getWithParam(ID, mockCallback);
    verifyAll();
  }

  @Test public void testServiceGetParamSync() throws IOException {
    expectSyncLifecycle(HttpGet.class, GET_DELETE_SIMPLE_URL_WITH_PARAMS + "id=" + ID);
    replayAll();

    GetService service = restAdapter.create(GetService.class);
    Response response = service.getWithParam(ID);
    assertThat(response).isEqualTo(RESPONSE);
    verifyAll();
  }

  @Test public void testServiceGetWithFixedParamAsync() throws IOException {
    expectAsyncLifecycle(HttpGet.class,
        GET_DELETE_SIMPLE_URL_WITH_PARAMS + "filter=merchant&id=" + ID);
    replayAll();

    GetService service = restAdapter.create(GetService.class);
    service.getWithFixedParam(ID, mockCallback);
    verifyAll();
  }

  @Test public void testServiceGetWithFixedParamSync() throws IOException {
    expectSyncLifecycle(HttpGet.class,
        GET_DELETE_SIMPLE_URL_WITH_PARAMS + "filter=merchant&id=" + ID);
    replayAll();

    GetService service = restAdapter.create(GetService.class);
    Response response = service.getWithFixedParam(ID);
    assertThat(response).isEqualTo(RESPONSE);
    verifyAll();
  }

  @Test public void testServiceGetWithMultipleFixedParamsAsync() throws IOException {
    expectAsyncLifecycle(HttpGet.class,
        GET_DELETE_SIMPLE_URL_WITH_PARAMS + "filter=merchant&name2=value2&id=" + ID);
    replayAll();

    GetService service = restAdapter.create(GetService.class);
    service.getWithMultipleFixedParams(ID, mockCallback);
    verifyAll();
  }

  @Test public void testServiceGetWithMultipleFixedParamsSync() throws IOException {
    expectSyncLifecycle(HttpGet.class,
        GET_DELETE_SIMPLE_URL_WITH_PARAMS + "filter=merchant&name2=value2&id=" + ID);
    replayAll();

    GetService service = restAdapter.create(GetService.class);
    Response response = service.getWithMultipleFixedParams(ID);
    assertThat(response).isEqualTo(RESPONSE);
    verifyAll();
  }

  @Test public void testServiceGetPathParamAsync() throws IOException {
    expectAsyncLifecycle(HttpGet.class, PATH_URL_PREFIX + ID);
    replayAll();

    GetService service = restAdapter.create(GetService.class);
    service.getWithPathParam(ID, mockCallback);
    verifyAll();
  }

  @Test public void testServiceGetPathParamSync() throws IOException {
    expectSyncLifecycle(HttpGet.class, PATH_URL_PREFIX + ID);
    replayAll();

    GetService service = restAdapter.create(GetService.class);
    Response response = service.getWithPathParam(ID);
    assertThat(response).isEqualTo(RESPONSE);
    verifyAll();
  }

  @Test public void testServicePostSimpleAsync() throws IOException {
    expectAsyncLifecycle(HttpPost.class, BASE_URL);
    replayAll();

    PostService service = restAdapter.create(PostService.class);
    service.post(mockCallback);
    verifyAll();
  }

  @Test public void testServicePostSimpleSync() throws IOException {
    expectSyncLifecycle(HttpPost.class, BASE_URL);
    replayAll();

    PostService service = restAdapter.create(PostService.class);
    Response response = service.post();
    assertThat(response).isEqualTo(RESPONSE);
    verifyAll();
  }

  @Test public void testServicePostSimpleClientErrorAsync() throws IOException {
    expectAsyncLifecycleClientError(HttpPost.class, BASE_URL);
    replayAll();

    PostService service = restAdapter.create(PostService.class);
    service.post(mockCallback);
    verifyAll();
  }

  @Test public void testServicePostSimpleClientErrorSync() throws IOException {
    expectSyncLifecycleClientError(HttpPost.class, BASE_URL);
    replayAll();

    PostService service = restAdapter.create(PostService.class);
    try {
      service.post();
      fail("Expected client exception.");
    } catch (RetrofitError expected) {
    }
    verifyAll();
  }

  @Test public void testServicePostSimpleServerErrorAsync() throws IOException {
    expectAsyncLifecycleServerError(HttpPost.class, BASE_URL);
    replayAll();

    PostService service = restAdapter.create(PostService.class);
    service.post(mockCallback);
    verifyAll();
  }

  @Test public void testServicePostSimpleServerErrorSync() throws IOException {
    expectSyncLifecycleServerError(HttpPost.class, BASE_URL);
    replayAll();

    PostService service = restAdapter.create(PostService.class);
    try {
      service.post();
      fail("Expected server exception");
    } catch (RetrofitError expected) {
    }
    verifyAll();
  }

  @Test public void testServicePostParamAsync() throws IOException {
    expectAsyncLifecycle(HttpPost.class, BASE_URL);
    replayAll();

    PostService service = restAdapter.create(PostService.class);
    service.postWithParam(ID, mockCallback);
    verifyAll();
  }

  @Test public void testServicePostParamSync() throws IOException {
    expectSyncLifecycle(HttpPost.class, BASE_URL);
    replayAll();

    PostService service = restAdapter.create(PostService.class);
    Response response = service.postWithParam(ID);
    assertThat(response).isEqualTo(RESPONSE);
    verifyAll();
  }

  @Test public void testServicePostPathParamAsync() throws IOException {
    expectAsyncLifecycle(HttpPost.class, PATH_URL_PREFIX + ID);
    replayAll();

    PostService service = restAdapter.create(PostService.class);
    service.postWithPathParam(ID, mockCallback);
    verifyAll();
  }

  @Test public void testServicePostPathParamSync() throws IOException {
    expectSyncLifecycle(HttpPost.class, PATH_URL_PREFIX + ID);
    replayAll();

    PostService service = restAdapter.create(PostService.class);
    Response response = service.postWithPathParam(ID);
    assertThat(response).isEqualTo(RESPONSE);
    verifyAll();
  }

  @Test public void testServicePutSimpleAsync() throws IOException {
    expectAsyncLifecycle(HttpPut.class, BASE_URL);
    replayAll();

    PutService service = restAdapter.create(PutService.class);
    service.put(mockCallback);
    verifyAll();
  }

  @Test public void testServicePutSimpleSync() throws IOException {
    expectSyncLifecycle(HttpPut.class, BASE_URL);
    replayAll();

    PutService service = restAdapter.create(PutService.class);
    Response response = service.put();
    assertThat(response).isEqualTo(RESPONSE);
    verifyAll();
  }

  @Test public void testServicePutParamAsync() throws IOException {
    expectAsyncLifecycle(HttpPut.class, BASE_URL);
    replayAll();

    PutService service = restAdapter.create(PutService.class);
    service.putWithParam(ID, mockCallback);
    verifyAll();
  }

  @Test public void testServicePutParamSync() throws IOException {
    expectSyncLifecycle(HttpPut.class, BASE_URL);
    replayAll();

    PutService service = restAdapter.create(PutService.class);
    Response response = service.putWithParam(ID);
    assertThat(response).isEqualTo(RESPONSE);
    verifyAll();
  }

  @Test public void testServicePutPathParamAsync() throws IOException {
    expectAsyncLifecycle(HttpPut.class, PATH_URL_PREFIX + ID);
    replayAll();

    PutService service = restAdapter.create(PutService.class);
    service.putWithPathParam(ID, mockCallback);
    verifyAll();
  }

  @Test public void testServicePutPathParamSync() throws IOException {
    expectSyncLifecycle(HttpPut.class, PATH_URL_PREFIX + ID);
    replayAll();

    PutService service = restAdapter.create(PutService.class);
    Response response = service.putWithPathParam(ID);
    assertThat(response).isEqualTo(RESPONSE);
    verifyAll();
  }

  private void replayAll() {
    replay(mockHttpExecutor, mockHttpClient, mockCallbackExecutor, mockCallback, mockResponse);
  }

  private void verifyAll() {
    verify(mockHttpExecutor, mockHttpClient, mockCallbackExecutor, mockCallback, mockResponse);
  }

  private <T extends HttpUriRequest> void expectAsyncLifecycle(Class<T> requestClass,
      String requestUrl) throws IOException {
    expectAsynchronousInvocation();
    expectHttpExecution(requestClass, requestUrl, RESPONSE, HttpStatus.SC_OK);
    expectCallbacks();
  }

  private <T extends HttpUriRequest> void expectSyncLifecycle(Class<T> requestClass,
      String requestUrl) throws IOException {
    expectHttpExecution(requestClass, requestUrl, RESPONSE, HttpStatus.SC_OK);
  }

  private <T extends HttpUriRequest> void expectAsyncLifecycleClientError(Class<T> requestClass,
      String requestUrl) throws IOException {
    expectAsynchronousInvocation();
    expectHttpExecution(requestClass, requestUrl, RESPONSE, HttpStatus.SC_CONFLICT);
    expectFailure();
  }

  private <T extends HttpUriRequest> void expectSyncLifecycleClientError(Class<T> requestClass,
      String requestUrl) throws IOException {
    expectHttpExecution(requestClass, requestUrl, RESPONSE, HttpStatus.SC_CONFLICT);
  }

  private <T extends HttpUriRequest> void expectAsyncLifecycleServerError(Class<T> requestClass,
      String requestUrl) throws IOException {
    expectAsynchronousInvocation();
    expectHttpExecution(requestClass, requestUrl, new Object(), HttpStatus.SC_NOT_IMPLEMENTED);
    expectFailure();
  }

  private <T extends HttpUriRequest> void expectSyncLifecycleServerError(Class<T> requestClass,
      String requestUrl) throws IOException {
    expectHttpExecution(requestClass, requestUrl, new Object(), HttpStatus.SC_NOT_IMPLEMENTED);
  }

  private void expectAsynchronousInvocation() {
    expectExecution(mockHttpExecutor);
    expectExecution(mockCallbackExecutor);
  }

  private <T extends HttpUriRequest> void expectHttpExecution(Class<T> requestClass,
      String requestUrl, Object response, int status) throws IOException {
    //expectSetOnWithRequest(requestClass, requestUrl);
    expectResponseCalls(GSON.toJson(response), status);
    expectHttpClientExecute();
  }

  private void expectCallbacks() {
    mockCallback.success(RESPONSE);
    expectLastCall().once();
  }

  private void expectFailure() {
    mockCallback.failure(isA(RetrofitError.class));
    expectLastCall().once();
  }

  private void expectHttpClientExecute() throws IOException {
    expect(mockHttpClient.execute(isA(HttpUriRequest.class))).andReturn(mockResponse);
  }

  private void expectResponseCalls(String jsonToReturn, int statusCode)
      throws UnsupportedEncodingException {
    expect(mockResponse.getEntity()).andReturn(new StringEntity(jsonToReturn));
    expect(mockResponse.getStatusLine()).andReturn(
        new BasicStatusLine(HttpVersion.HTTP_1_1, statusCode, ""));
    expect(mockResponse.getAllHeaders()).andReturn(null);
  }

  //private <T extends HttpUriRequest> void expectSetOnWithRequest(
  //    final Class<T> expectedRequestClass, final String expectedUri) {
  //  final Capture<HttpMessage> capture = new Capture<HttpMessage>();
  //  mockHeaders.setOn(capture(capture));
  //  expectLastCall().andAnswer(new IAnswer<Object>() {
  //    @Override public Object answer() throws Throwable {
  //      T request = expectedRequestClass.cast(capture.getValue());
  //      assertThat(request.getURI().toString()).isEqualTo(expectedUri);
  //      return null;
  //    }
  //  });
  //}

  private void expectExecution(Executor executor) {
    final Capture<Runnable> capture = new Capture<Runnable>();
    executor.execute(capture(capture));
    expectLastCall().andAnswer(new IAnswer<Object>() {
      @Override public Object answer() throws Throwable {
        capture.getValue().run();
        return null;
      }
    });
  }

  private interface DeleteService {

    @DELETE(ENTITY) void delete(Callback<Response> callback);
    @DELETE(ENTITY) Response delete();

    @DELETE(ENTITY) void deleteWithParam(@Named("id") String id, Callback<Response> callback);
    @DELETE(ENTITY) Response deleteWithParam(@Named("id") String id);

    @DELETE(ENTITY) @QueryParam(name = "filter", value = "merchant")
    void deleteWithFixedParam(@Named("id") String id, Callback<Response> callback);

    @DELETE(ENTITY) @QueryParam(name = "filter", value = "merchant")
    Response deleteWithFixedParam(@Named("id") String id);

    @DELETE(ENTITY) //
    @QueryParams({
        @QueryParam(name = "filter", value = "merchant"),
        @QueryParam(name = "name2", value = "value2")
    }) void deleteWithMultipleFixedParams(@Named("id") String id, Callback<Response> callback);

    @DELETE(ENTITY) //
    @QueryParams({
        @QueryParam(name = "filter", value = "merchant"),
        @QueryParam(name = "name2", value = "value2")
    }) Response deleteWithMultipleFixedParams(@Named("id") String id);

    @DELETE(ENTITY_PATH_PARAM)
    void deleteWithPathParam(@Named("id") String id, Callback<Response> callback);

    @DELETE(ENTITY_PATH_PARAM) Response deleteWithPathParam(@Named("id") String id);
  }

  private interface GetService {
    @GET(ENTITY) void get(Callback<Response> callback);
    @GET(ENTITY) Response get();

    @GET(ENTITY) void getWithParam(@Named("id") String id, Callback<Response> callback);
    @GET(ENTITY) Response getWithParam(@Named("id") String id);

    @GET(ENTITY) @QueryParam(name = "filter", value = "merchant")
    void getWithFixedParam(@Named("id") String id, Callback<Response> callback);

    @GET(ENTITY) @QueryParam(name = "filter", value = "merchant")
    Response getWithFixedParam(@Named("id") String id);

    @GET(ENTITY) //
    @QueryParams({
        @QueryParam(name = "filter", value = "merchant"),
        @QueryParam(name = "name2", value = "value2")
    }) void getWithMultipleFixedParams(@Named("id") String id, Callback<Response> callback);

    @GET(ENTITY) //
    @QueryParams({
        @QueryParam(name = "filter", value = "merchant"),
        @QueryParam(name = "name2", value = "value2")
    }) Response getWithMultipleFixedParams(@Named("id") String id);

    @GET(ENTITY_PATH_PARAM)
    void getWithPathParam(@Named("id") String id, Callback<Response> callback);

    @GET(ENTITY_PATH_PARAM) Response getWithPathParam(@Named("id") String id);
  }

  private interface PostService {
    @POST(ENTITY) void post(Callback<Response> callback);
    @POST(ENTITY) Response post();

    @POST(ENTITY) void postWithParam(@Named("id") String id, Callback<Response> callback);
    @POST(ENTITY) Response postWithParam(@Named("id") String id);

    @POST(ENTITY_PATH_PARAM)
    void postWithPathParam(@Named("id") String id, Callback<Response> callback);

    @POST(ENTITY_PATH_PARAM) Response postWithPathParam(@Named("id") String id);
  }

  private interface PutService {
    @PUT(ENTITY) void put(Callback<Response> callback);
    @PUT(ENTITY) Response put();

    @PUT(ENTITY) void putWithParam(@Named("id") String id, Callback<Response> callback);
    @PUT(ENTITY) Response putWithParam(@Named("id") String id);

    @PUT(ENTITY_PATH_PARAM)
    void putWithPathParam(@Named("id") String id, Callback<Response> callback);

    @PUT(ENTITY_PATH_PARAM) Response putWithPathParam(@Named("id") String id);
  }
}
