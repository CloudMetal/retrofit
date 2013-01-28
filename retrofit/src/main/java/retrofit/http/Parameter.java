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
 * <li>As a POST/PUT body.</li>
 * </ul>
 */
public final class Parameter {
  private final String name;
  private final Type type;
  private final Object value;

  public Parameter(String name, Object value, Type valueType) {
    if (name == null) {
      throw new NullPointerException("Parameter name cannot be null.");
    }

    this.name = name;
    this.type = valueType;
    this.value = value;
  }

  /** Parameter name. */
  public String getName() {
    return name;
  }

  /** Parameter value. */
  public Object getValue() {
    return value;
  }

  /** The instance type of {@link #getValue()}. */
  public Type getValueType() {
    return type;
  }

  @Override public String toString() {
    return name + "=" + value;
  }

  /**
   * NOTE: This method only uses {@link #getName()}} and {@link #getValue()}} for equality testing.
   * <p/>
   * {@inheritDoc}
   */
  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Parameter parameter = (Parameter) o;

    if (!name.equals(parameter.name)) return false;
    if (value != null ? !value.equals(parameter.value) : parameter.value != null) return false;

    return true;
  }

  /**
   * NOTE: This method only uses {@link #getName()} and {@link #getValue()}} for computing hash
   * code.
   * <p/>
   * {@inheritDoc}
   */
  @Override public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + (value != null ? value.hashCode() : 0);
    return result;
  }
}
