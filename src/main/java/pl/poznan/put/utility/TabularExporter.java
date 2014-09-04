package pl.poznan.put.utility;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.table.TableModel;

import org.jumpmind.symmetric.csv.CsvWriter;

import pl.poznan.put.interfaces.Tabular;

public class TabularExporter {
    public static void export(Tabular tabular, File file) throws IOException {
        TabularExporter.export(tabular.asExportableTableModel(), file);
    }

    public static void export(TableModel tableModel, File file)
            throws IOException {
        try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
            CsvWriter csvWriter = new CsvWriter(writer, ';');

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
        }
    }
}
