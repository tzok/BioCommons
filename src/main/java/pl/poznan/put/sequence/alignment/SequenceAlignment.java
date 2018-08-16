package pl.poznan.put.sequence.alignment;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Date;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import pl.poznan.put.interfaces.ExportFormat;
import pl.poznan.put.interfaces.Exportable;

@Data
@Slf4j
public class SequenceAlignment implements Exportable {
  private final boolean isGlobal;
  private final String alignment;

  @Override
  public final String toString() {
    return alignment;
  }

  @Override
  public final void export(final OutputStream stream) throws IOException {
    try (final Writer writer = new OutputStreamWriter(stream, Charset.defaultCharset())) {
      writer.write(isGlobal ? "Global" : "Local");
      writer.write(" sequence alignment: ");
      writer.write("\n\n");
      writer.write(alignment);
    } catch (final UnsupportedEncodingException e) {
      SequenceAlignment.log.error("Failed to export sequence alignment", e);
      throw new IOException(e);
    }
  }

  @Override
  public final ExportFormat getExportFormat() {
    return ExportFormat.TXT;
  }

  @Override
  public final File suggestName() {
    final String filename =
        String.format(
            "%s-%s.txt",
            DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(new Date()),
            isGlobal ? "-global-alignment" : "-local-alignment");
    return new File(filename);
  }
}
