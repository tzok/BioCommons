package pl.poznan.put.pdb.analysis;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Test;

public class PdbParserTest {

  @Test
  public void testParse1a9nFile() throws IOException {
    // Given
    String pdbContent =
        new String(Files.readAllBytes(Paths.get("src/test/resources/1a9nR_M1.pdb")));
    PdbParser parser = new PdbParser();

    // When
    List<PdbModel> models = parser.parse(pdbContent);

    // Then
    assertNotNull("Parsed models should not be null", models);
    assertFalse("Should parse at least one model", models.isEmpty());
  }
}
