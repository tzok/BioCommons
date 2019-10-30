package pl.poznan.put.utility;

import static org.hamcrest.CoreMatchers.*;

import java.io.IOException;
import java.nio.charset.Charset;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.Test;

public class TabularExporterTest {
  @Test
  public final void exportEmpty() throws IOException {
    final TableModel tableModel = new DefaultTableModel(0, 0);
    final ByteArrayOutputStream stream = new ByteArrayOutputStream();
    TabularExporter.export(tableModel, stream);

    Assert.assertThat(stream.toString(Charset.defaultCharset()), is("\n"));
  }

  @Test
  public final void exportJustColumns() throws IOException {
    final Object[][] values = {};
    final Object[] columns = {"A", "B", "C"};
    final TableModel tableModel = new DefaultTableModel(values, columns);
    final ByteArrayOutputStream stream = new ByteArrayOutputStream();
    TabularExporter.export(tableModel, stream);

    Assert.assertThat(stream.toString(Charset.defaultCharset()), is("A,B,C\n"));
  }

  @Test
  public final void exportData() throws IOException {
    final Object[][] values = {{1, 2, 3}, {"x", "y", "z"}};
    final Object[] columns = {"A", "B", "C"};
    final TableModel tableModel = new DefaultTableModel(values, columns);
    final ByteArrayOutputStream stream = new ByteArrayOutputStream();
    TabularExporter.export(tableModel, stream);

    Assert.assertThat(stream.toString(Charset.defaultCharset()), is("A,B,C\n1,2,3\nx,y,z\n"));
  }

  @Test
  public final void exportWithNull() throws IOException {
    final Object[][] values = {{1, null, 3}};
    final Object[] columns = {"A", "B", "C"};
    final TableModel tableModel = new DefaultTableModel(values, columns);
    final ByteArrayOutputStream stream = new ByteArrayOutputStream();
    TabularExporter.export(tableModel, stream);

    Assert.assertThat(stream.toString(Charset.defaultCharset()), is("A,B,C\n1,,3\n"));
  }
}
