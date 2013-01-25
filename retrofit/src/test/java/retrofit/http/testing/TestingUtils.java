// Copyright 2013 Square, Inc.
package retrofit.http.testing;

import java.lang.reflect.Method;

public class TestingUtils {
  public static Method getMethod(Class c, String name) {
    Method[] methods = c.getDeclaredMethods();
    for (Method method : methods) {
      if (method.getName().equals(name)) {
        return method;
      }
    }
    throw new IllegalArgumentException("Unknown method '" + name + "' on " + c);
  }
}
