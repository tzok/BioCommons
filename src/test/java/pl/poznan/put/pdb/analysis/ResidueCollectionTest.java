package pl.poznan.put.pdb.analysis;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
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
    cifBuilder.add(stem, "D1", "A.1 A.7 gCGGAUU ((((((( YYRRRYY A.66 A.72 AAUUCGC ))))))) RRYYYRY");
    cifBuilder.add(single, "S1", "A.7 A.11 UUAgC (...( YYRYY");

    final var strings = cifBuilder.build().lines().map(String::trim).collect(Collectors.toList());
    assertTrue(
        strings.contains("D1 'A.1 A.7 gCGGAUU ((((((( YYRRRYY A.66 A.72 AAUUCGC ))))))) RRYYYRY'"));
    assertTrue(strings.contains("S1 'A.7 A.11 UUAgC (...( YYRYY'"));
  }

  @Test
  public void testPdbBuilder() {
    final var pdbBuilder = new ResidueCollection.PdbBuilder();
    pdbBuilder.add(stem, "D1");
    pdbBuilder.add(single, "S1");
    final var pdbString = pdbBuilder.build();
    final var strings =
        pdbString.lines().filter(s -> s.equals("D1") || s.equals("S1")).collect(Collectors.toSet());
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
    assertTrue(
        StringUtils.contains(
            new ResidueCollection.CifBuilder().build(), "10.1093/bioinformatics/btab069"));
  }

  @Test
  public void testPdbBuilderTwice() {
    final var builder = new ResidueCollection.PdbBuilder();
    builder.add(stem, "D1");
    var first = builder.build();
    var second = builder.build();
    assertEquals(first, second);

    builder.add(single, "S1");
    first = builder.build();
    second = builder.build();
    assertEquals(first, second);
  }

  @Test
  public void testCifBuilderTwice() throws IOException {
    final var builder = new ResidueCollection.CifBuilder();
    builder.add(stem, "D1");
    var first = builder.build();
    var second = builder.build();
    assertEquals(first, second);

    builder.add(single, "S1");
    first = builder.build();
    second = builder.build();
    assertEquals(first, second);
  }
}
