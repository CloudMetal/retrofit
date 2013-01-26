// Copyright 2013 Square, Inc.
package retrofit.http;

class Response {
  final String text;

  public Response(String text) {
    this.text = text;
  }

  @Override public int hashCode() {
    return 7;
  }

  @Override public boolean equals(Object obj) {
    return obj instanceof Response && text.equals(((Response) obj).text);
  }
}
