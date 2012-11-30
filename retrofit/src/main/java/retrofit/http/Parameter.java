// Copyright 2012 Square, Inc.
package retrofit.http;

import java.lang.reflect.Type;

/**
 * Represents a named parameter and its value.
 * <p/>
 * This is used in one of three places in a request:
 * <ul>
 * <li>Named replacement in the relative URL path.</li>
 * <li>As a URL query parameter.</li>
 * <li>As a POST/PUT body parameter.</li>
 * </ul>
 */
public final class Parameter {
  private final String name;
  private final Type type;
  private final Object value;

  public Parameter(String name, Type type, Object value) {
    this.name = name;
    this.type = type;
    this.value = value;
  }

  /** Parameter name. */
  public String getName() {
    return name;
  }

  /** The instance type of {@link #getValue()}. */
  public Type getType() {
    return type;
  }

  /** Parameter value. */
  public Object getValue() {
    return value;
  }

  @Override public String toString() {
    return name + "=" + value;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Parameter parameter = (Parameter) o;

    if (!name.equals(parameter.name)) return false;
    if (!type.equals(parameter.type)) return false;
    if (value != null ? !value.equals(parameter.value) : parameter.value != null) return false;

    return true;
  }

  @Override public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + type.hashCode();
    result = 31 * result + (value != null ? value.hashCode() : 0);
    return result;
  }
}
