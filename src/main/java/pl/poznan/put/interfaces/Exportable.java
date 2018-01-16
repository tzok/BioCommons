package pl.poznan.put.interfaces;

import pl.poznan.put.types.ExportFormat;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public interface Exportable {
  void export(OutputStream stream) throws IOException;

  ExportFormat getExportFormat();

  File suggestName();
}
