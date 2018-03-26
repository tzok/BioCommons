package pl.poznan.put.utility;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.Charset;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Test;

public class TabularExporterTest {
  @Test
  public void exportEmpty() throws IOException {
    final TableModel tableModel = new DefaultTableModel(0, 0);
    final ByteArrayOutputStream stream = new ByteArrayOutputStream();
    TabularExporter.export(tableModel, stream);

    assertEquals("\n", stream.toString(Charset.defaultCharset()));
  }

  @Test
  public void exportJustColumns() throws IOException {
    final Object[][] values = {};
    final Object[] columns = {"A", "B", "C"};
    final TableModel tableModel = new DefaultTableModel(values, columns);
    final ByteArrayOutputStream stream = new ByteArrayOutputStream();
    TabularExporter.export(tableModel, stream);

    assertEquals("A,B,C\n", stream.toString(Charset.defaultCharset()));
  }

  @Test
  public void exportData() throws IOException {
    final Object[][] values = {{1, 2, 3}, {"x", "y", "z"}};
    final Object[] columns = {"A", "B", "C"};
    final TableModel tableModel = new DefaultTableModel(values, columns);
    final ByteArrayOutputStream stream = new ByteArrayOutputStream();
    TabularExporter.export(tableModel, stream);

    assertEquals("A,B,C\n1,2,3\nx,y,z\n", stream.toString(Charset.defaultCharset()));
  }

  @Test
  public void exportWithNull() throws IOException {
    final Object[][] values = {{1, null, 3}};
    final Object[] columns = {"A", "B", "C"};
    final TableModel tableModel = new DefaultTableModel(values, columns);
    final ByteArrayOutputStream stream = new ByteArrayOutputStream();
    TabularExporter.export(tableModel, stream);

    assertEquals("A,B,C\n1,,3\n", stream.toString(Charset.defaultCharset()));
  }
}
