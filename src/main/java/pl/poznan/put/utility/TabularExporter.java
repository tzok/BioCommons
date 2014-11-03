package pl.poznan.put.utility;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.swing.table.TableModel;

import org.apache.commons.io.IOUtils;
import org.jumpmind.symmetric.csv.CsvWriter;

import pl.poznan.put.interfaces.Tabular;

public class TabularExporter {
    public static void export(Tabular tabular, File file) throws IOException {
        TabularExporter.export(tabular.asExportableTableModel(), file);
    }

    public static void export(Tabular tabular, Writer writer) throws IOException {
        TabularExporter.export(tabular.asExportableTableModel(), writer);
    }

    public static String export(Tabular tabular) throws IOException {
        Writer writer = new StringWriter();
        TabularExporter.export(tabular, writer);
        return writer.toString();
    }

    public static void export(TableModel tableModel, File file) throws IOException {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file, "UTF-8");
            TabularExporter.export(tableModel, writer);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    public static void export(TableModel tableModel, Writer writer) throws IOException {
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

    public static String export(TableModel tableModel) throws IOException {
        Writer writer = new StringWriter();
        TabularExporter.export(tableModel, writer);
        return writer.toString();
    }
}
