package pl.poznan.put.interfaces;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/** A set of methods that any kind of result must implement to be exported to output file. */
public interface Exportable {
  /**
   * Exports the result to a provided output stream.
   *
   * @param stream Where to export the data to.
   * @throws IOException When it was impossible to export the data.
   */
  void export(OutputStream stream) throws IOException;

  /**
   * Generates a useful file name for this kind of exported data.
   *
   * @return An instance of {@link File} depicting a file name.
   */
  File suggestName();
}
