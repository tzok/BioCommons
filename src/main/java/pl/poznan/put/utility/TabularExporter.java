package pl.poznan.put.utility;

import org.jumpmind.symmetric.csv.CsvWriter;

import javax.swing.table.TableModel;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/** A collections of methods for easy export of tabular data to CSV files. */
public final class TabularExporter {
  private TabularExporter() {
    super();
  }

  /**
   * Exports the tabular data in CSV format and pushed it to an output stream.
   *
   * @param tableModel The data to export.
   * @param stream The stream that will receive CSV data.
   * @throws IOException When writing to the stream fails.
   */
  public static void export(final TableModel tableModel, final OutputStream stream)
      throws IOException {
    final CsvWriter csvWriter = new CsvWriter(stream, ',', StandardCharsets.UTF_8);

    for (int i = 0; i < tableModel.getColumnCount(); i++) {
      csvWriter.write(tableModel.getColumnName(i));
    }
    csvWriter.endRecord();

    for (int i = 0; i < tableModel.getRowCount(); i++) {
      for (int j = 0; j < tableModel.getColumnCount(); j++) {
        final Object value = tableModel.getValueAt(i, j);
        csvWriter.write((value != null) ? value.toString() : null);
      }
      csvWriter.endRecord();
    }

    csvWriter.close();
  }
}
