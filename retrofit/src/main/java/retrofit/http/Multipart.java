// Copyright 2013 Square, Inc.
package retrofit.http;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** Denotes a mutli-part request. */
@Target(METHOD) @Retention(RUNTIME)
public @interface Multipart {
}
