package pl.poznan.put.utility;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.swing.table.TableModel;

import org.jumpmind.symmetric.csv.CsvWriter;

public class TabularExporter {
    public static void export(TableModel tableModel, OutputStream stream) throws IOException {
        CsvWriter csvWriter = new CsvWriter(stream, ',', Charset.forName("UTF-8"));

        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            csvWriter.write(tableModel.getColumnName(i));
        }
        csvWriter.endRecord();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            for (int j = 0; j < tableModel.getColumnCount(); j++) {
                Object value = tableModel.getValueAt(i, j);
                csvWriter.write(value != null ? value.toString() : null);
            }
            csvWriter.endRecord();
        }

        csvWriter.close();
    }
}
