package pl.poznan.put.structure.formats;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.pdb.analysis.PdbParser;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.utility.ResourcesHelper;

public class BpSeqTest {
  // @formatter:off
  private static final String INPUT_GOOD_1 = "1 A 0\n" + "2 C 3\n" + "3 G 2\n" + "4 U 0";
  private static final String INPUT_GOOD_2 =
      "# Comment line\n" + "1 A 0\n" + "2 C 3\n" + "3 G 2\n" + "4 U 0 # Comment inline";
  private static final String INPUT_TOO_FEW = "1 A 0\n" + "2 C \n" + "3 G 2\n" + "4 U 0";
  private static final String INPUT_TOO_MANY = "1 A 0\n" + "2 C 3 1\n" + "3 G 2\n" + "4 U 0";
  private static final String INPUT_TOO_LONG_SEQ = "1 ADE 0\n" + "2 C 3\n" + "3 G 2\n" + "4 U 0";
  private static final String INPUT_INDEX_1 = "-1 A 0\n" + "2 C 3\n" + "3 G 2\n" + "4 U 0";
  private static final String INPUT_INDEX_2 = "xyz A 0\n" + "2 C 3\n" + "3 G 2\n" + "4 U 0";
  private static final String INPUT_PAIR_1 = "1 A -1\n" + "2 C 3\n" + "3 G 2\n" + "4 U 0";
  private static final String INPUT_PAIR_2 = "1 A xyz\n" + "2 C 3\n" + "3 G 2\n" + "4 U 0";
  private static final String INPUT_NUMBERING = "1 A 0\n" + "3 C 4\n" + "4 G 3\n" + "5 U 0";
  private static final String INPUT_SELF_PAIRED = "1 A 0\n" + "2 C 2\n" + "3 G 0\n" + "4 U 0";
  private static final String INPUT_MAPPING_1 = "1 A 0\n" + "2 C 5\n" + "3 G 2\n" + "4 U 0";
  private static final String INPUT_MAPPING_2 = "1 A 0\n" + "2 C 3\n" + "3 G 4\n" + "4 U 3";
  // @formatter:on

  private String bpseq1DDYall;
  private String bpseq1DDYnonisolated;
  private String bpseq1XPO;
  private String pdb1XPO;

  @Before
  public final void loadPdbFile() throws IOException {
    bpseq1DDYall = ResourcesHelper.loadResource("1DDY-A-all.bpseq");
    bpseq1DDYnonisolated = ResourcesHelper.loadResource("1DDY-A-nonisolated.bpseq");
    bpseq1XPO = ResourcesHelper.loadResource("1XPO.bpseq");
    pdb1XPO = ResourcesHelper.loadResource("1XPO.pdb");
  }

  @Test
  public final void testGood() {
    BpSeq.fromString(BpSeqTest.INPUT_GOOD_1);
    BpSeq.fromString(BpSeqTest.INPUT_GOOD_2);
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testFew() {
    BpSeq.fromString(BpSeqTest.INPUT_TOO_FEW);
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testMany() {
    BpSeq.fromString(BpSeqTest.INPUT_TOO_MANY);
  }

  public final void testLongSeq() {
    final BpSeq bpSeq = BpSeq.fromString(BpSeqTest.INPUT_TOO_LONG_SEQ);
    assertThat(bpSeq.sequence(), is("ACGU"));
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testIndex1() {
    BpSeq.fromString(BpSeqTest.INPUT_INDEX_1);
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testIndex2() {
    BpSeq.fromString(BpSeqTest.INPUT_INDEX_2);
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testPair1() {
    BpSeq.fromString(BpSeqTest.INPUT_PAIR_1);
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testPair2() {
    BpSeq.fromString(BpSeqTest.INPUT_PAIR_2);
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testNumbering() {
    BpSeq.fromString(BpSeqTest.INPUT_NUMBERING);
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testSelfPaired() {
    BpSeq.fromString(BpSeqTest.INPUT_SELF_PAIRED);
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testMapping1() {
    BpSeq.fromString(BpSeqTest.INPUT_MAPPING_1);
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testMapping2() {
    BpSeq.fromString(BpSeqTest.INPUT_MAPPING_2);
  }

  @Test
  public final void fromDotBracket() {
    final DefaultDotBracket db = DefaultDotBracket.fromString(DefaultDotBracketTest.FROM_2Z74);
    BpSeq.fromDotBracket(db);
  }

  @Test
  public final void testManyChainsWithMissingResidues() {
    final PdbParser parser = new PdbParser();
    final List<PdbModel> models = parser.parse(pdb1XPO);
    assertThat(models.size(), is(1));
    final PdbModel model = models.get(0);

    assertThat(
        model.residues().stream().filter(PdbResidue::isMissing).count(),
        is((long) model.missingResidues().size()));

    final PdbModel rna = model.filteredNewInstance(MoleculeType.RNA);
    assertThat(model.missingResidues().size(), is(103));
    assertThat(rna.missingResidues().size(), is(36));

    final BpSeq bpSeq = BpSeq.fromString(bpseq1XPO);
    Ct.fromBpSeqAndPdbModel(bpSeq, rna);
  }

  @Test
  public final void testRemovalOfIsolatedBasePairs() {
    final BpSeq all = BpSeq.fromString(bpseq1DDYall);
    final BpSeq nonIsolated = BpSeq.fromString(bpseq1DDYnonisolated);
    assertThat(all.withoutIsolatedPairs(), is(nonIsolated));
  }

  @Test
  public final void testUnsucessfulRemovalOfIsolatedBasePairs() {
    final BpSeq nonIsolated = BpSeq.fromString(bpseq1DDYnonisolated);
    assertThat(nonIsolated.withoutIsolatedPairs(), is(nonIsolated));
  }
}
