// Copyright 2012 Square, Inc.
package retrofit.http;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.List;
import retrofit.io.MimeType;
import retrofit.io.TypedBytes;

import static retrofit.http.RestAdapter.UTF_8;

/**
 * A {@link Converter} which uses GSON for serialization and deserialization of entities.
 *
 * @author Jake Wharton (jw@squareup.com)
 */
public class GsonConverter implements Converter {
  static final MimeType JSON = new MimeType("application/json", "json");

  private final Gson gson;

  public GsonConverter(Gson gson) {
    this.gson = gson;
  }

  @Override public Object to(byte[] body, Type type) throws ConversionException {
    try {
      InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(body), UTF_8);
      return gson.fromJson(isr, type);
    } catch (IOException e) {
      throw new ConversionException(e);
    } catch (JsonParseException e) {
      throw new ConversionException(e);
    }
  }

  @Override public TypedBytes fromObject(Object object) {
    try {
      return new JsonTypedBytes(gson.toJson(object).getBytes(UTF_8));
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException(UTF_8 + " encoding does not exist.", e);
    }
  }

  @Override public TypedBytes fromParams(List<Parameter> parameters) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      JsonWriter jsonWriter = new JsonWriter(new OutputStreamWriter(baos));
      jsonWriter.beginObject();
      for (Parameter parameter : parameters) {
        jsonWriter.name(parameter.getName());
        gson.toJson(parameter.getValue(), parameter.getType(), jsonWriter);
      }
      jsonWriter.endObject();
      jsonWriter.close();
      return new JsonTypedBytes(baos.toByteArray());
    } catch (IOException e) {
      throw new RuntimeException("Unable to convert parameters to JSON.", e);
    }
  }

  static class JsonTypedBytes implements TypedBytes {
    final byte[] jsonBytes;

    JsonTypedBytes(byte[] jsonBytes) {
      this.jsonBytes = jsonBytes;
    }

    @Override public MimeType mimeType() {
      return JSON;
    }

    @Override public int length() {
      return jsonBytes.length;
    }

    @Override public void writeTo(OutputStream out) throws IOException {
      out.write(jsonBytes);
    }
  }
}
