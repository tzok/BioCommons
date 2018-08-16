package pl.poznan.put.interfaces;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public interface Exportable {
  void export(OutputStream stream) throws IOException;

  File suggestName();
}
