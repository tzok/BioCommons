package pl.poznan.put.interfaces;

import javax.swing.table.TableModel;

public interface Tabular {
  TableModel asExportableTableModel();

  TableModel asDisplayableTableModel();
}
