package pl.poznan.put.sequence.alignment;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.poznan.put.interfaces.Exportable;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Date;

@Value.Immutable
public abstract class SequenceAlignment implements Exportable {
  private static final Logger LOGGER = LoggerFactory.getLogger(SequenceAlignment.class);

  @Value.Parameter(order = 1)
  public abstract String alignment();

  @Value.Parameter(order = 2)
  public abstract boolean isGlobal();

  @Override
  public final String toString() {
    return alignment();
  }

  @Override
  public final void export(final OutputStream stream) throws IOException {
    try (final Writer writer = new OutputStreamWriter(stream, Charset.defaultCharset())) {
      writer.write(isGlobal() ? "Global" : "Local");
      writer.write(" sequence alignment: ");
      writer.write("\n\n");
      writer.write(alignment());
    } catch (final UnsupportedEncodingException e) {
      SequenceAlignment.LOGGER.error("Failed to export sequence alignment", e);
      throw new IOException(e);
    }
  }

  @Override
  public final File suggestName() {
    final String filename =
        String.format(
            "%s-%s.txt",
            DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(new Date()),
            isGlobal() ? "-global-alignment" : "-local-alignment");
    return new File(filename);
  }
}
