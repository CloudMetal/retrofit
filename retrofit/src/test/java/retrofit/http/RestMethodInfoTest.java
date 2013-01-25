// Copyright 2013 Square, Inc.
package retrofit.http;

import com.google.gson.reflect.TypeToken;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import javax.inject.Named;
import org.junit.Ignore;
import org.junit.Test;
import retrofit.http.testing.ExtendingCallback;
import retrofit.http.testing.Response;
import retrofit.http.testing.ResponseCallback;
import retrofit.http.testing.TestingUtils;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.fest.assertions.api.Assertions.assertThat;
import static retrofit.http.RestMethodInfo.NO_SINGLE_ENTITY;

public class RestMethodInfoTest {
  @Test public void pathParameterParsing() throws Exception {
    expectParams("");
    expectParams("foo");
    expectParams("foo/bar");
    expectParams("foo/bar/{}");
    expectParams("foo/bar/{taco}", "taco");
    expectParams("foo/bar/{t}", "t");
    expectParams("foo/bar/{!!!}/"); // Invalid parameter.
    expectParams("foo/bar/{}/{taco}", "taco");
    expectParams("foo/bar/{taco}/or/{burrito}", "taco", "burrito");
    expectParams("foo/bar/{taco}/or/{taco}", "taco");
    expectParams("foo/bar/{taco-shell}", "taco-shell");
    expectParams("foo/bar/{taco_shell}", "taco_shell");
  }

  @Test public void concreteCallbackTypes() {
    Type expected = Response.class;
    Method method = TestingUtils.getMethod(TypeExamples.class, "a");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
    assertThat(methodInfo.isSynchronous).isFalse();
    assertThat(methodInfo.type).isEqualTo(expected);
  }

  @Test public void concreteCallbackTypesWithParams() {
    Type expected = Response.class;
    Method method = TestingUtils.getMethod(TypeExamples.class, "b");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
    assertThat(methodInfo.isSynchronous).isFalse();
    assertThat(methodInfo.type).isEqualTo(expected);
  }

  @Test public void genericCallbackTypes() {
    Type expected = Response.class;
    Method method = TestingUtils.getMethod(TypeExamples.class, "c");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
    assertThat(methodInfo.isSynchronous).isFalse();
    assertThat(methodInfo.type).isEqualTo(expected);
  }

  @Test public void genericCallbackTypesWithParams() {
    Type expected = Response.class;
    Method method = TestingUtils.getMethod(TypeExamples.class, "d");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
    assertThat(methodInfo.isSynchronous).isFalse();
    assertThat(methodInfo.type).isEqualTo(expected);
  }

  @Test public void wildcardGenericCallbackTypes() {
    Type expected = Response.class;
    Method method = TestingUtils.getMethod(TypeExamples.class, "e");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
    assertThat(methodInfo.isSynchronous).isFalse();
    assertThat(methodInfo.type).isEqualTo(expected);
  }

  @Test public void genericCallbackWithGenericType() {
    Type expected = new TypeToken<List<String>>() {}.getType();
    Method method = TestingUtils.getMethod(TypeExamples.class, "f");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
    assertThat(methodInfo.isSynchronous).isFalse();
    assertThat(methodInfo.type).isEqualTo(expected);
  }

  @Ignore // TODO support this case!
  @Test public void extendingGenericCallback() {
    Type expected = Response.class;
    Method method = TestingUtils.getMethod(TypeExamples.class, "g");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
    assertThat(methodInfo.isSynchronous).isFalse();
    assertThat(methodInfo.type).isEqualTo(expected);
  }

  @Test public void synchronousResponse() {
    Type expected = Response.class;
    Method method = TestingUtils.getMethod(TypeExamples.class, "h");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
    assertThat(methodInfo.isSynchronous).isTrue();
    assertThat(methodInfo.type).isEqualTo(expected);
  }

  @Test public void synchronousGenericResponse() {
    Type expected = new TypeToken<List<String>>() {}.getType();
    Method method = TestingUtils.getMethod(TypeExamples.class, "i");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
    assertThat(methodInfo.isSynchronous).isTrue();
    assertThat(methodInfo.type).isEqualTo(expected);
  }

  @Test(expected = IllegalArgumentException.class)
  public void missingCallbackTypes() {
    Method method = TestingUtils.getMethod(TypeExamples.class, "j");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    assertThat(methodInfo.isSynchronous).isFalse();
    methodInfo.init();
  }

  @Test(expected = IllegalArgumentException.class)
  public void synchronousWithAsyncCallback() {
    Method method = TestingUtils.getMethod(TypeExamples.class, "k");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
  }

  @Test(expected = IllegalStateException.class)
  public void lackingMethod() {
    Method method = TestingUtils.getMethod(AnnotationExamples.class, "a");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
  }

  @Test public void deleteMethod() {
    Method method = TestingUtils.getMethod(AnnotationExamples.class, "b");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
    assertThat(methodInfo.restMethod.value()).isEqualTo("DELETE");
    assertThat(methodInfo.restMethod.hasBody()).isFalse();
    assertThat(methodInfo.path).isEqualTo("foo");
  }

  @Test public void getMethod() {
    Method method = TestingUtils.getMethod(AnnotationExamples.class, "c");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
    assertThat(methodInfo.restMethod.value()).isEqualTo("GET");
    assertThat(methodInfo.restMethod.hasBody()).isFalse();
    assertThat(methodInfo.path).isEqualTo("foo");
  }

  @Test public void headMethod() {
    Method method = TestingUtils.getMethod(AnnotationExamples.class, "d");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
    assertThat(methodInfo.restMethod.value()).isEqualTo("HEAD");
    assertThat(methodInfo.restMethod.hasBody()).isFalse();
    assertThat(methodInfo.path).isEqualTo("foo");
  }

  @Test public void postMethod() {
    Method method = TestingUtils.getMethod(AnnotationExamples.class, "e");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
    assertThat(methodInfo.restMethod.value()).isEqualTo("POST");
    assertThat(methodInfo.restMethod.hasBody()).isTrue();
    assertThat(methodInfo.path).isEqualTo("foo");
  }

  @Test public void putMethod() {
    Method method = TestingUtils.getMethod(AnnotationExamples.class, "f");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
    assertThat(methodInfo.restMethod.value()).isEqualTo("PUT");
    assertThat(methodInfo.restMethod.hasBody()).isTrue();
    assertThat(methodInfo.path).isEqualTo("foo");
  }

  @Test public void custom1Method() {
    Method method = TestingUtils.getMethod(AnnotationExamples.class, "g");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
    assertThat(methodInfo.restMethod.value()).isEqualTo("CUSTOM1");
    assertThat(methodInfo.restMethod.hasBody()).isFalse();
    assertThat(methodInfo.path).isEqualTo("foo");
  }

  @Test public void custom2Method() {
    Method method = TestingUtils.getMethod(AnnotationExamples.class, "h");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
    assertThat(methodInfo.restMethod.value()).isEqualTo("CUSTOM2");
    assertThat(methodInfo.restMethod.hasBody()).isTrue();
    assertThat(methodInfo.path).isEqualTo("foo");
  }

  @Test public void singleQueryParam() {
    Method method = TestingUtils.getMethod(AnnotationExamples.class, "i");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
    assertThat(methodInfo.pathQueryParams).hasSize(1);
    QueryParam param = methodInfo.pathQueryParams[0];
    assertThat(param.name()).isEqualTo("a");
    assertThat(param.value()).isEqualTo("b");
  }

  @Test public void multipleQueryParam() {
    Method method = TestingUtils.getMethod(AnnotationExamples.class, "j");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
    assertThat(methodInfo.pathQueryParams).hasSize(2);
    QueryParam param1 = methodInfo.pathQueryParams[0];
    assertThat(param1.name()).isEqualTo("a");
    assertThat(param1.value()).isEqualTo("b");
    QueryParam param2 = methodInfo.pathQueryParams[1];
    assertThat(param2.name()).isEqualTo("c");
    assertThat(param2.value()).isEqualTo("d");
  }

  @Test(expected = IllegalStateException.class)
  public void bothQueryParamAnnotations() {
    Method method = TestingUtils.getMethod(AnnotationExamples.class, "k");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
  }

  @Test(expected = IllegalStateException.class)
  public void missingMethodWithQueryParam() {
    Method method = TestingUtils.getMethod(AnnotationExamples.class, "l");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
  }

  @Test(expected = IllegalStateException.class)
  public void missingMethodWithQueryParams() {
    Method method = TestingUtils.getMethod(AnnotationExamples.class, "m");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
  }

  @Test(expected = IllegalStateException.class)
  public void emptyQueryParams() {
    Method method = TestingUtils.getMethod(AnnotationExamples.class, "n");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
  }

  @Test public void noQueryParamsNonNull() {
    Method method = TestingUtils.getMethod(AnnotationExamples.class, "b");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
    assertThat(methodInfo.pathQueryParams).isEmpty();
  }

  @Test public void emptyParams() {
    Method method = TestingUtils.getMethod(ParameterExamples.class, "a");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
    assertThat(methodInfo.pathNamedParams).isEmpty();
    assertThat(methodInfo.singleEntityArgumentIndex).isEqualTo(NO_SINGLE_ENTITY);
  }

  @Test public void singleParam() {
    Method method = TestingUtils.getMethod(ParameterExamples.class, "b");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
    assertThat(methodInfo.pathNamedParams).hasSize(1).containsSequence("a");
    assertThat(methodInfo.singleEntityArgumentIndex).isEqualTo(NO_SINGLE_ENTITY);
  }

  @Test public void multipleParams() {
    Method method = TestingUtils.getMethod(ParameterExamples.class, "c");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
    assertThat(methodInfo.pathNamedParams).hasSize(3).containsSequence("a", "b", "c");
    assertThat(methodInfo.singleEntityArgumentIndex).isEqualTo(NO_SINGLE_ENTITY);
  }

  @Test public void emptyParamsWithCallback() {
    Method method = TestingUtils.getMethod(ParameterExamples.class, "d");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
    assertThat(methodInfo.pathNamedParams).isEmpty();
    assertThat(methodInfo.singleEntityArgumentIndex).isEqualTo(NO_SINGLE_ENTITY);
  }

  @Test public void singleParamWithCallback() {
    Method method = TestingUtils.getMethod(ParameterExamples.class, "e");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
    assertThat(methodInfo.pathNamedParams).hasSize(1).containsSequence("a");
    assertThat(methodInfo.singleEntityArgumentIndex).isEqualTo(NO_SINGLE_ENTITY);
  }

  @Test public void multipleParamsWithCallback() {
    Method method = TestingUtils.getMethod(ParameterExamples.class, "f");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
    assertThat(methodInfo.pathNamedParams).hasSize(2).containsSequence("a", "b");
    assertThat(methodInfo.singleEntityArgumentIndex).isEqualTo(NO_SINGLE_ENTITY);
  }

  @Test public void singleEntity() {
    Method method = TestingUtils.getMethod(ParameterExamples.class, "g");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
    assertThat(methodInfo.pathNamedParams).hasSize(1);
    assertThat(methodInfo.pathNamedParams[0]).isNull();
    assertThat(methodInfo.singleEntityArgumentIndex).isEqualTo(0);
  }

  @Test public void singleEntityWithCallback() {
    Method method = TestingUtils.getMethod(ParameterExamples.class, "h");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
    assertThat(methodInfo.pathNamedParams).hasSize(1);
    assertThat(methodInfo.pathNamedParams[0]).isNull();
    assertThat(methodInfo.singleEntityArgumentIndex).isEqualTo(0);
  }

  @Test(expected = IllegalStateException.class)
  public void twoSingleEntities() {
    Method method = TestingUtils.getMethod(ParameterExamples.class, "i");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
  }

  @Test public void singleEntityWithNamed() {
    Method method = TestingUtils.getMethod(ParameterExamples.class, "j");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
    assertThat(methodInfo.pathNamedParams).hasSize(3).containsSequence("a", null, "c");
    assertThat(methodInfo.singleEntityArgumentIndex).isEqualTo(1);
  }

  @Test public void singleEntityWithNamedAndCallback() {
    Method method = TestingUtils.getMethod(ParameterExamples.class, "k");
    RestMethodInfo methodInfo = new RestMethodInfo(method);
    methodInfo.init();
    assertThat(methodInfo.pathNamedParams).hasSize(3).containsSequence("a", null, "c");
    assertThat(methodInfo.singleEntityArgumentIndex).isEqualTo(1);
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////

  private static void expectParams(String path, String... expected) {
    Set<String> calculated = RestMethodInfo.parsePathParameters(path);
    assertThat(calculated).hasSize(expected.length);
    if (expected.length > 0) {
        assertThat(calculated).containsExactly(expected);
    }
  }

  @SuppressWarnings("UnusedDeclaration") // Accessed reflectively.
  interface TypeExamples {
    // Asynchronous response delivery.
    @GET("foo") void a(ResponseCallback c);
    @GET("foo") void b(@Named("id") String id, ResponseCallback c);
    @GET("foo") void c(Callback<Response> c);
    @GET("foo") void d(@Named("id") String id, Callback<Response> c);
    @GET("foo") void e(Callback<? extends Response> c);
    @GET("foo") void f(Callback<List<String>> c);
    @GET("foo") void g(ExtendingCallback<Response> callback);

    // Synchronous response delivery.
    @GET("foo") Response h();
    @GET("foo") List<String> i();

    // Invalid response delivery.
    @GET("foo") void j(@Named("id") String id);
    @GET("foo") Response k(Callback<Response> callback);
  }

  @RestMethod("CUSTOM1")
  @Target(METHOD) @Retention(RUNTIME)
  @interface CUSTOM1 {
    String value();
  }
  @RestMethod(value = "CUSTOM2", hasBody = true)
  @Target(METHOD) @Retention(RUNTIME)
  @interface CUSTOM2 {
    String value();
  }

  @SuppressWarnings("UnusedDeclaration") // Accessed reflectively.
  interface AnnotationExamples {
    Response a(); // Invalid, no method annotation.
    @DELETE("foo") Response b();
    @GET("foo") Response c();
    @HEAD("foo") Response d();
    @POST("foo") Response e();
    @PUT("foo") Response f();
    @CUSTOM1("foo") Response g();
    @CUSTOM2("foo") Response h();

    @GET("foo")
    @QueryParam(name = "a", value = "b")
    Response i();

    @GET("foo")
    @QueryParams({
        @QueryParam(name = "a", value = "b"),
        @QueryParam(name = "c", value = "d")
    })
    Response j();

    @GET("foo")
    @QueryParam(name = "a", value = "b")
    @QueryParams({
        @QueryParam(name = "a", value = "b"),
        @QueryParam(name = "c", value = "d")
    })
    Response k(); // Invalid, both query param annotations.

    @QueryParam(name = "a", value = "b")
    Response l(); // Invalid, no method annotation.

    @QueryParams({
        @QueryParam(name = "a", value = "b"),
        @QueryParam(name = "c", value = "d")
    })
    Response m(); // Invalid, no method annotation.

    @GET("foo")
    @QueryParams({})
    Response n(); // Invalid, empty query params list.
  }

  @SuppressWarnings("UnusedDeclaration") // Accessed reflectively.
  interface ParameterExamples {
    @GET("") Response a();
    @GET("") Response b(@Named("a") String a);
    @GET("") Response c(@Named("a") String a, @Named("b") String b, @Named("c") String c);
    @GET("") void d(ResponseCallback cb);
    @GET("") void e(@Named("a") String a, ResponseCallback cb);
    @GET("") void f(@Named("a") String a, @Named("b") String b, ResponseCallback cb);
    @GET("") Response g(@SingleEntity Object o);
    @GET("") void h(@SingleEntity Object o, ResponseCallback cb);
    @GET("") Response i(@SingleEntity int o1, @SingleEntity int o2); // Invalid, two entities.
    @GET("") Response j(@Named("a") int a, @SingleEntity int b, @Named("c") int c);
    @GET("") void k(@Named("a") int a, @SingleEntity int b, @Named("c") int c, ResponseCallback cb);
  }
}
