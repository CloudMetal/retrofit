// Copyright 2013 Square, Inc.
package retrofit.http;

import java.util.concurrent.Executor;
import org.junit.Before;
import org.junit.Test;
import retrofit.http.client.ApacheClient;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class RestAdapterTest {
  interface Example {
  }

  private RestAdapter restAdapter;

  @Before public void setUp() {
    Executor executor = mock(Executor.class);
    restAdapter = new RestAdapter.Builder()
        .setExecutors(executor, executor)
        .setClient(new ApacheClient())
        .setServer("http://example.com")
        .build();
  }

  @Test public void objectMethodsStillWork() {
    Example ex = restAdapter.create(Example.class);
    assertThat(ex.hashCode()).isNotZero();
    assertThat(ex.equals(this)).isFalse();
    assertThat(ex.toString()).isNotEmpty();
  }
}
