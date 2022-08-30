package pl.poznan.put.utility;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.nio.charset.Charset;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Test;

public class TabularExporterTest {
  @Test
  public final void exportEmpty() throws IOException {
    final TableModel tableModel = new DefaultTableModel(0, 0);
    final ByteArrayOutputStream stream = new ByteArrayOutputStream();
    TabularExporter.export(tableModel, stream);

    assertThat(stream.toString(Charset.defaultCharset()), is(System.lineSeparator()));
  }

  @Test
  public final void exportJustColumns() throws IOException {
    final Object[][] values = {};
    final Object[] columns = {"A", "B", "C"};
    final TableModel tableModel = new DefaultTableModel(values, columns);
    final ByteArrayOutputStream stream = new ByteArrayOutputStream();
    TabularExporter.export(tableModel, stream);

    assertThat(stream.toString(Charset.defaultCharset()), is("A,B,C" + System.lineSeparator()));
  }

  @Test
  public final void exportData() throws IOException {
    final Object[][] values = {{1, 2, 3}, {"x", "y", "z"}};
    final Object[] columns = {"A", "B", "C"};
    final TableModel tableModel = new DefaultTableModel(values, columns);
    final ByteArrayOutputStream stream = new ByteArrayOutputStream();
    TabularExporter.export(tableModel, stream);

    assertThat(
        stream.toString(Charset.defaultCharset()),
        is(
            "A,B,C"
                + System.lineSeparator()
                + "1,2,3"
                + System.lineSeparator()
                + "x,y,z"
                + System.lineSeparator()));
  }

  @Test
  public final void exportWithNull() throws IOException {
    final Object[][] values = {{1, null, 3}};
    final Object[] columns = {"A", "B", "C"};
    final TableModel tableModel = new DefaultTableModel(values, columns);
    final ByteArrayOutputStream stream = new ByteArrayOutputStream();
    TabularExporter.export(tableModel, stream);

    assertThat(
        stream.toString(Charset.defaultCharset()),
        is("A,B,C" + System.lineSeparator() + "1,,3" + System.lineSeparator()));
  }
}
