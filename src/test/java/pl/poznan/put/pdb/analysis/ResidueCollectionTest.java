package pl.poznan.put.pdb.analysis;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import pl.poznan.put.utility.ResourcesHelper;

public class ResidueCollectionTest {
  private ResidueCollection stem;
  private ResidueCollection single;

  @Before
  public void setUp() throws IOException {
    final var pdb1ehz = ResourcesHelper.loadResource("1EHZ.pdb");
    final var models = new PdbParser().parse(pdb1ehz);
    assertFalse(models.isEmpty());

    final var model = models.get(0);
    assertEquals(76, model.residues().size());

    final var d1 =
        model.residues().stream()
            .filter(
                residue ->
                    (residue.residueNumber() >= 1 && residue.residueNumber() <= 7)
                        || (residue.residueNumber() >= 66 && residue.residueNumber() <= 72))
            .collect(Collectors.toList());
    final var s1 =
        model.residues().stream()
            .filter(residue -> residue.residueNumber() >= 7 && residue.residueNumber() <= 11)
            .collect(Collectors.toList());
    stem = ImmutableDefaultResidueCollection.of(d1);
    single = ImmutableDefaultResidueCollection.of(s1);
  }

  @Test
  public void testCifBuilder() throws IOException {
    final var cifBuilder = new ResidueCollection.CifBuilder();
    cifBuilder.add(stem, "D1");
    cifBuilder.add(single, "S1");

    final var strings =
        cifBuilder.build().lines().filter(s -> s.startsWith("data_")).collect(Collectors.toSet());
    assertEquals(2, strings.size());
    assertTrue(strings.contains("data_D1"));
    assertTrue(strings.contains("data_S1"));
  }

  @Test
  public void testPdbBuilder() throws IOException {
    final var pdbBuilder = new ResidueCollection.PdbBuilder();
    pdbBuilder.add(stem, "D1");
    pdbBuilder.add(single, "S1");

    final var strings =
        pdbBuilder
            .build()
            .lines()
            .filter(s -> s.equals("D1") || s.equals("S1"))
            .collect(Collectors.toSet());
    assertEquals(2, strings.size());
    assertTrue(strings.contains("D1"));
    assertTrue(strings.contains("S1"));
  }

  @Test
  public void testPdbBuilderEmpty() {
    assertEquals("", new ResidueCollection.PdbBuilder().build());
  }

  @Test
  public void testCifBuilderEmpty() throws IOException {
    assertEquals("", new ResidueCollection.CifBuilder().build());
  }
}
