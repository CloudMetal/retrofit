// Copyright 2012 Square, Inc.
package retrofit.http.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility methods for working with streams.
 * <p/>
 * Adapted from Google Guava's stream helpers.
 */
class Streams {
  private static final int BUFFER_SIZE = 0x1000;

  /**
   * Creates a {@code byte[]} from reading the entirety of an {@link InputStream}. May return an
   * empty array but never {@code null}.
   */
  static byte[] readFully(InputStream stream) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    if (stream != null) {
      byte[] buf = new byte[BUFFER_SIZE];
      int r;
      while ((r = stream.read(buf)) != -1) {
        baos.write(buf, 0, r);
      }
    }
    return baos.toByteArray();
  }
}
