// Copyright 2013 Square, Inc.
package retrofit.http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import retrofit.io.AbstractTypedBytes;
import retrofit.io.MimeType;

public class StringTypedBytes extends AbstractTypedBytes {
  private final byte[] bytes;

  public StringTypedBytes(String string) {
    super(new MimeType("text/plain", "txt"));
    try {
      bytes = string.getBytes("UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override public int length() {
    return bytes.length;
  }

  @Override public void writeTo(OutputStream out) throws IOException {
    out.write(bytes);
  }
}
