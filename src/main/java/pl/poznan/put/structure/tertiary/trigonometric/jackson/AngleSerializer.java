package pl.poznan.put.structure.tertiary.trigonometric.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import pl.poznan.put.pdb.PdbResidueIdentifier;

public class PdbResidueIdentifierSerializer extends StdSerializer<PdbResidueIdentifier> {
  private static final long serialVersionUID = 7713891781801136584L;

  public PdbResidueIdentifierSerializer(final Class<PdbResidueIdentifier> t) {
    super(t);
  }

  @Override
  public final void serialize(
      final PdbResidueIdentifier t,
      final JsonGenerator jsonGenerator,
      final SerializerProvider serializerProvider)
      throws IOException {
    jsonGenerator.writeString(t.toString());
  }
}
