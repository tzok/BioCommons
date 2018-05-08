package pl.poznan.put.structure.tertiary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pl.poznan.put.pdb.PdbParsingException;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.utility.ResourcesHelper;

public class StructureManagerTest {
  private File file1EHZ;
  private File file1EVV;
  private PdbModel model1EHZ;
  private PdbModel model1EVV;

  @Before
  public final void setUp() throws URISyntaxException, IOException, PdbParsingException {
    file1EHZ = ResourcesHelper.loadResourceFile("1EHZ.pdb");
    final List<? extends PdbModel> models1EHZ = StructureManager.loadStructure(file1EHZ);
    assertEquals(1, models1EHZ.size());
    model1EHZ = models1EHZ.get(0);

    final List<PdbModel> models1EVV = StructureManager.loadStructure("1EVV");
    assertEquals(1, models1EVV.size());
    model1EVV = models1EVV.get(0);
    file1EVV = StructureManager.getFile(model1EVV);
  }

  @After
  public final void tearDown() {
    assertEquals(2, StructureManager.getAllStructures().size());
    StructureManager.remove(file1EHZ);
    assertEquals(1, StructureManager.getAllStructures().size());
    StructureManager.remove(file1EVV);
    assertTrue(StructureManager.getAllStructures().isEmpty());

    // this is temporary file downloaded each time in @Before method
    FileUtils.deleteQuietly(file1EVV);
  }

  @Test(expected = IllegalArgumentException.class)
  public final void getFileInvalid() {
    final PdbModel structure = mock(PdbModel.class);
    StructureManager.getFile(structure);
  }

  @Test(expected = IllegalArgumentException.class)
  public final void getStructureInvalid() {
    StructureManager.getStructure("invalid");
  }

  @Test(expected = IllegalArgumentException.class)
  public final void getNameInvalid() {
    final PdbModel structure = mock(PdbModel.class);
    StructureManager.getName(structure);
  }

  @Test(expected = IllegalArgumentException.class)
  public final void loadStructureInvalid() throws Exception {
    StructureManager.loadStructure("invalid");
  }

  @Test
  public final void getFile() {
    final File file = StructureManager.getFile(model1EHZ);
    assertEquals(file1EHZ, file);
  }

  @Test
  public final void getStructure() {
    final PdbModel structure = StructureManager.getStructure("1EHZ");
    assertEquals(model1EHZ, structure);
  }

  @Test
  public final void getName() {
    final String name = StructureManager.getName(model1EHZ);
    assertEquals("1EHZ", name);
  }

  @Test
  public final void getNameMultiModel() throws Exception {
    final File file = ResourcesHelper.loadResourceFile("2MIY.pdb");
    final List<? extends PdbModel> models = StructureManager.loadStructure(file);
    final int size = models.size();
    assertEquals(18, size);

    for (int i = 0; i < size; i++) {
      final PdbModel model = models.get(i);
      final String expected = String.format("2MIY.%02d", i + 1);
      final String actual = StructureManager.getName(model);
      assertEquals(expected, actual);
    }

    StructureManager.remove(file);
  }

  @Test
  public final void getAllStructures() {
    final List<PdbModel> structures = StructureManager.getAllStructures();
    assertTrue(CollectionUtils.isEqualCollection(Arrays.asList(model1EHZ, model1EVV), structures));
  }

  @Test
  public final void getAllNames() {
    final List<String> names = StructureManager.getAllNames();
    assertTrue(CollectionUtils.isEqualCollection(Arrays.asList("1EHZ", "1EVV"), names));
  }

  @Test
  public final void getNames() {
    final List<String> names = StructureManager.getNames(Arrays.asList(model1EHZ, model1EVV));
    assertTrue(CollectionUtils.isEqualCollection(Arrays.asList("1EHZ", "1EVV"), names));
  }

  @Test
  public final void getModels() {
    final List<PdbModel> models = StructureManager.getModels(file1EHZ);
    assertEquals(1, models.size());
    assertEquals(model1EHZ, models.get(0));
  }
}
