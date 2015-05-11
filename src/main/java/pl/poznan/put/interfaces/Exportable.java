package pl.poznan.put.interfaces;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import pl.poznan.put.types.ExportFormat;

public interface Exportable {
    void export(OutputStream stream) throws IOException;

    ExportFormat getExportFormat();

    File suggestName();
}
