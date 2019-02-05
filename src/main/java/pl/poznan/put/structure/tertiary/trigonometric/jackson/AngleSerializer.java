package pl.poznan.put.structure.tertiary.trigonometric.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import pl.poznan.put.circular.Angle;

public class AngleSerializer extends StdSerializer<Angle> {
  public AngleSerializer(final Class<Angle> t) {
    super(t);
  }

  @Override
  public final void serialize(
      final Angle t, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider)
      throws IOException {
    jsonGenerator.writeNumber(t.getDegrees());
  }
}
