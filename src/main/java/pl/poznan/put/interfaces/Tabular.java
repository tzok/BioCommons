package pl.poznan.put.interfaces;

import javax.swing.table.TableModel;

/** A set of methods to be implemented by result objects with tabular form of data. */
public interface Tabular {
  /**
   * Generates tabular data which are formatted for UI, so they might be long, complex and with
   * Unicode characters.
   *
   * @return An instance of {@link TableModel} with the data.
   */
  TableModel asExportableTableModel();

  /**
   * Generates tabular data to be exported to output file i.e. raw numbers with maximum precision
   * and ASCII only.
   *
   * @return An instance of {@link TableModel} with the data.
   */
  TableModel asDisplayableTableModel();
}
