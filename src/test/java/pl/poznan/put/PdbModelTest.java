package pl.poznan.put;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.io.PDBFileParser;
import org.junit.Test;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.pdb.analysis.CifParser;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbChain;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.pdb.analysis.PdbParser;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.pdb.analysis.StructureParser;
import pl.poznan.put.structure.secondary.CanonicalStructureExtractor;
import pl.poznan.put.structure.secondary.formats.BpSeq;
import pl.poznan.put.utility.ResourcesHelper;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class PdbModelTest {
  private static void assertBpSeqEquals(final String pdbString, final String bpSeqString) {
    final PdbParser parser = new PdbParser(false);
    final List<PdbModel> models = parser.parse(pdbString);
    final PdbModel model = models.get(0);

    final BpSeq bpSeqFromModel = CanonicalStructureExtractor.bpSeq(model);
    assertThat(bpSeqFromModel.toString(), is(bpSeqString));

    final BpSeq bpSeqFromString = BpSeq.fromString(bpSeqString);
    assertThat(bpSeqFromModel, is(bpSeqFromString));
  }

  @Test
  public final void testParsing() throws Exception {
    final String pdb1EHZ = ResourcesHelper.loadResource("1EHZ.pdb");
    final PdbParser parser = new PdbParser();
    final List<PdbModel> models = parser.parse(pdb1EHZ);
    assertEquals(1, models.size());
  }

  @Test
  public final void testResidueAnalysis() throws Exception {
    final String pdb1EHZ = ResourcesHelper.loadResource("1EHZ.pdb");
    final PdbParser parser = new PdbParser();
    final List<PdbModel> models = parser.parse(pdb1EHZ);
    final PdbModel model = models.get(0);
    final List<PdbResidue> residues = model.getResidues();
    assertEquals(76, residues.size());
  }

  @Test
  public final void testChainAnalysis() throws Exception {
    final String pdb1EHZ = ResourcesHelper.loadResource("1EHZ.pdb");
    final PdbParser parser = new PdbParser();
    final List<PdbModel> models = parser.parse(pdb1EHZ);
    final PdbModel model = models.get(0);
    final List<PdbChain> chains = model.getChains();
    assertEquals(1, chains.size());
  }

  @Test
  public final void testModifiedResidue() throws Exception {
    final String pdb1EHZ = ResourcesHelper.loadResource("1EHZ.pdb");
    final PdbParser parser = new PdbParser();
    final List<PdbModel> models = parser.parse(pdb1EHZ);
    final PdbModel model = models.get(0);
    final PdbResidue residue = model.findResidue(new PdbResidueIdentifier("A", 10, " "));
    assertThat(residue.getOriginalResidueName(), is("2MG"));
    assertThat(residue.getModifiedResidueName(), is("G"));
    assertThat(residue.getDetectedResidueName(), is("G"));
  }

  @Test
  public final void testUnmodifiedResidue() throws Exception {
    final String pdb1EHZ = ResourcesHelper.loadResource("1EHZ.pdb");
    final PdbParser parser = new PdbParser();
    final List<PdbModel> models = parser.parse(pdb1EHZ);
    final PdbModel model = models.get(0);
    final PdbResidue residue = model.findResidue("A", 74, " ");
    assertThat(residue.getOriginalResidueName(), is("C"));
    assertThat(residue.getModifiedResidueName(), is("C"));
    assertThat(residue.getDetectedResidueName(), is("C"));
  }

  @Test
  public final void testO3PModificationDetection() throws Exception {
    final String pdb1EHZ = ResourcesHelper.loadResource("1EHZ.pdb");
    final PdbParser parser = new PdbParser();
    final List<PdbModel> models = parser.parse(pdb1EHZ);
    final PdbModel model = models.get(0);
    final List<PdbResidue> residues = model.getResidues();

    // for first residue expect a mismatch in atom count because it has an
    // unusual O3P terminal atom
    final PdbResidue residue = residues.get(0);
    assertThat(residue.isModified(), is(true));
    assertThat(residue.hasAllAtoms(), is(false));
  }

  @Test
  public final void testUridineModificationDetection() throws Exception {
    final String pdb1EHZ = ResourcesHelper.loadResource("1EHZ.pdb");
    final PdbParser parser = new PdbParser();
    final List<PdbModel> models = parser.parse(pdb1EHZ);
    final PdbModel model = models.get(0);

    // the H2U (dihydrouridine) is modified by two additional hydrogens
    // which is undetectable in a non-hydrogen PDB file
    PdbResidue residue = model.findResidue("A", 16, " ");
    assertThat(residue.isModified(), is(true));
    assertThat(residue.hasAllAtoms(), is(true));

    // the PSU (pseudouridine) contains the same atoms, but it is an isomer
    // and therefore a modified residue
    residue = model.findResidue("A", 39, " ");
    assertThat(residue.isModified(), is(true));
    assertThat(residue.hasAllAtoms(), is(true));

    // the 5MU (methyluridine) is the RNA counterpart of thymine
    residue = model.findResidue("A", 54, " ");
    assertThat(residue.getOriginalResidueName(), is("5MU"));
    assertThat(residue.getModifiedResidueName(), is("U"));
    assertThat(residue.getDetectedResidueName(), is("U"));
    assertThat(residue.isModified(), is(true));
    assertThat(residue.hasAllAtoms(), is(false));
  }

  @Test
  public final void testPSUModificationDetection() throws Exception {
    final String pdb1EHZ = ResourcesHelper.loadResource("1EHZ.pdb");
    final PdbParser parser = new PdbParser();
    final List<PdbModel> models = parser.parse(pdb1EHZ);
    final PdbModel model = models.get(0);

    // the PSU (dihydrouridine) is modified by two additional hydrogens
    // which is undetectable in a non-hydrogen PDB file; there can be a
    // mismatch between MODRES entry and hasAllAtoms() call
    final PdbResidue residue = model.findResidue("A", 39, " ");
    assertThat(residue.hasAllAtoms(), is(residue.isModified()));
  }

  @Test
  public final void testModificationDetection() throws Exception {
    final String pdb1EHZ = ResourcesHelper.loadResource("1EHZ.pdb");
    final PdbParser parser = new PdbParser();
    final List<PdbModel> models = parser.parse(pdb1EHZ);
    final PdbModel model = models.get(0);

    for (final PdbResidue residue : model.getResidues()) {
      if (!residue.wasSuccessfullyDetected()) {
        continue;
      }

      if (residue.hasAtom(AtomName.O3P)) {
        assertThat(residue.isModified(), is(true));
        assertThat(residue.hasAllAtoms(), is(false));
      } else if ("H2U".equals(residue.getOriginalResidueName())
          || "PSU".equals(residue.getOriginalResidueName())) {
        assertThat(residue.isModified(), is(true));
        assertThat(residue.hasAllAtoms(), is(true));
      } else if ("5MU".equals(residue.getOriginalResidueName())) {
        assertThat(residue.getOriginalResidueName(), is("5MU"));
        assertThat(residue.getModifiedResidueName(), is("U"));
        assertThat(residue.getDetectedResidueName(), is("U"));
        assertThat(residue.isModified(), is(true));
        assertThat(residue.hasAllAtoms(), is(false));
      } else {
        assertThat(
            String.format(
                "Detected %s for %s/%s",
                residue.getDetectedResidueName(),
                residue.getOriginalResidueName(),
                residue.getModifiedResidueName()),
            residue.getDetectedResidueName(),
            is(residue.getModifiedResidueName()));
        assertThat(!residue.hasAllAtoms(), is(residue.isModified()));
      }
    }
  }

  @Test
  public final void testResidueInTerminatedChain() throws Exception {
    final String pdb2Z74 = ResourcesHelper.loadResource("2Z74.pdb");
    final PdbParser parser = new PdbParser();
    final List<PdbModel> models = parser.parse(pdb2Z74);
    final PdbModel model = models.get(0);
    final List<PdbChain> chains = model.getChains();
    assertEquals(
        String.format(
            "Found chains (expected [A, B]): %s", Arrays.toString(chains.toArray(new PdbChain[0]))),
        2,
        chains.size());
  }

  @Test
  public final void testMissingResidues() throws Exception {
    final String pdb2Z74 = ResourcesHelper.loadResource("2Z74.pdb");
    final PdbParser parser = new PdbParser();
    final List<PdbModel> models = parser.parse(pdb2Z74);
    final PdbModel model = models.get(0);
    PdbResidue residue = model.findResidue("A", 21, " ");
    assertThat(residue.isMissing(), is(true));
    residue = model.findResidue("B", 21, " ");
    assertThat(residue.isMissing(), is(true));
  }

  @Test
  public final void testOutputParsable() throws Exception {
    final String pdb1EHZ = ResourcesHelper.loadResource("1EHZ.pdb");
    final PdbParser parser = new PdbParser();
    final List<PdbModel> models = parser.parse(pdb1EHZ);
    final PdbModel model = models.get(0);
    final List<PdbModel> parsed = parser.parse(model.toPdbString());

    assertEquals(1, models.size());
    assertEquals(1, parsed.size());

    assertEquals(1, models.get(0).getChains().size());
    assertEquals(1, parsed.get(0).getChains().size());

    assertEquals(76, models.get(0).getChains().get(0).getResidues().size());
    assertEquals(76, parsed.get(0).getChains().get(0).getResidues().size());
  }

  @Test
  public final void testOutputParsableByBioJava() throws Exception {
    final String pdb1EHZ = ResourcesHelper.loadResource("1EHZ.pdb");
    final PdbParser parser = new PdbParser();
    final List<PdbModel> models = parser.parse(pdb1EHZ);
    final PdbModel model = models.get(0);

    final PDBFileParser fileParser = new PDBFileParser();
    final Structure structure =
        fileParser.parsePDBFile(
            IOUtils.toInputStream(model.toPdbString(), Charset.defaultCharset()));

    assertEquals(1, structure.getChains().size());
    assertEquals(1, model.getChains().size());

    assertEquals(76, structure.getChain("A").getAtomGroups().size());
    assertEquals(76, model.getChains().get(0).getResidues().size());
  }

  @Test
  public final void testSequence() throws Exception {
    final String pdb1EHZ = ResourcesHelper.loadResource("1EHZ.pdb");
    final PdbParser parser = new PdbParser();
    List<PdbModel> models = parser.parse(pdb1EHZ);
    PdbModel model = models.get(0);
    String sequence = model.getSequence();
    assertThat(
        sequence.toUpperCase(Locale.ENGLISH),
        is("GCGGAUUUAGCUCAGUUGGGAGAGCGCCAGACUGAAGAUCUGGAGGUCCUGUGUUCGAUCCACAGAAUUCGCACCA"));

    final String pdb2Z74 = ResourcesHelper.loadResource("2Z74.pdb");
    models = parser.parse(pdb2Z74);
    model = models.get(0);
    List<PdbChain> chains = model.getChains();
    sequence = chains.get(0).getSequence();
    assertThat(sequence.toUpperCase(Locale.ENGLISH), is("AGCGCCUGGACUUAAAGCCAUUGCACU"));
    sequence = chains.get(1).getSequence();
    assertThat(
        sequence.toUpperCase(Locale.ENGLISH),
        is(
            "CCGGCUUUAAGUUGACGAGGGCAGGGUUUAUCGAGACAUCGGCGGGUGCCCUGCGGUCUUCCUGCGACCGUUAGAGGACUGGUAAAACCACAGGCGACUGUGGCAUAGAGCAGUCCGGGCAGGAA"));
    sequence = model.getSequence();
    assertThat(
        sequence.toUpperCase(Locale.ENGLISH),
        is(
            "AGCGCCUGGACUUAAAGCCAUUGCACUCCGGCUUUAAGUUGACGAGGGCAGGGUUUAUCGAGACAUCGGCGGGUGCCCUGCGGUCUUCCUGCGACCGUUAGAGGACUGGUAAAACCACAGGCGACUGUGGCAUAGAGCAGUCCGGGCAGGAA"));

    final String pdb4A04 = ResourcesHelper.loadResource("4A04.pdb");
    models = parser.parse(pdb4A04);
    model = models.get(0);
    chains = model.getChains();
    sequence = chains.get(0).getSequence();
    assertThat(
        sequence.toUpperCase(Locale.ENGLISH),
        is(
            "MHHHHHHENLYFQGGVSVQLEMKALWDEFNQLGTEMIVTKAGRRMFPTFQVKLFGMDPMADYMLLMDFVPVDDKRYRYAFHSSSWLVAGKADPATPGRVHYHPDSPAKGAQWMKQIVSFDKLKLTNNLLDDNGHIILNSMHRYQPRFHVVYVDPRKDSEKYAEENFKTFVFEETRFTAVTAYQNHRITQLKIASNPFAKGFRD"));
    sequence = chains.get(1).getSequence();
    assertThat(
        sequence.toUpperCase(Locale.ENGLISH),
        is(
            "MHHHHHHENLYFQGGVSVQLEMKALWDEFNQLGTEMIVTKAGRRMFPTFQVKLFGMDPMADYMLLMDFVPVDDKRYRYAFHSSSWLVAGKADPATPGRVHYHPDSPAKGAQWMKQIVSFDKLKLTNNLLDDNGHIILNSMHRYQPRFHVVYVDPRKDSEKYAEENFKTFVFEETRFTAVTAYQNHRITQLKIASNPFAKGFRD"));
    sequence = chains.get(2).getSequence();
    assertThat(sequence.toUpperCase(Locale.ENGLISH), is("AATTTCACACCTAGGTGTGAAATT"));
    sequence = chains.get(2).getSequence();
    assertThat(sequence.toUpperCase(Locale.ENGLISH), is("AATTTCACACCTAGGTGTGAAATT"));
  }

  @Test
  public final void test79CharLongAtomLines() throws Exception {
    final String pdbPKB300 = ResourcesHelper.loadResource("PKB300.pdb");
    final PdbParser parser = new PdbParser(false);
    final List<PdbModel> models = parser.parse(pdbPKB300);
    assertEquals(1, models.size());
    final PdbModel model = models.get(0);
    final List<PdbChain> chains = model.getChains();
    final List<PdbResidue> residues = model.getResidues();
    final List<PdbAtomLine> atoms = model.getAtoms();

    assertEquals(1, chains.size());
    assertEquals(37, residues.size());
    assertEquals(1186, atoms.size());
  }

  @Test
  public final void testAmberModel() throws Exception {
    final String pdbAmber = ResourcesHelper.loadResource("amber.pdb");
    final PdbParser parser = new PdbParser(false);
    final List<PdbModel> models = parser.parse(pdbAmber);
    assertEquals(1, models.size());
    final PdbModel model = models.get(0);
    final List<PdbChain> chains = model.getChains();
    final List<PdbResidue> residues = model.getResidues();
    final List<PdbAtomLine> atoms = model.getAtoms();

    assertEquals(3, chains.size());
    assertEquals(49, residues.size());
    assertEquals(1557, atoms.size());
  }

  @Test
  public final void testNMRModelsWithHydrogenAtoms() throws Exception {
    final String pdb2MIY = ResourcesHelper.loadResource("2MIY.pdb");
    final PdbParser parser = new PdbParser(false);
    final List<PdbModel> models = parser.parse(pdb2MIY);
    assertEquals(18, models.size());

    final PdbModel model = models.get(0);
    final List<PdbChain> chains = model.getChains();
    final List<PdbResidue> residues = model.getResidues();
    final List<PdbAtomLine> atoms = model.getAtoms();
    assertEquals(1, chains.size());
    assertEquals(59, residues.size());
    assertEquals(1909, atoms.size());

    final PdbChain chain = chains.get(0);
    final String sequence = chain.getSequence();
    assertThat(Character.isLowerCase(sequence.charAt(0)), is(true));
    assertThat(StringUtils.isAllUpperCase(sequence.substring(1)), is(true));
  }

  @Test
  public final void testCanonicalSecondaryStructure() throws Exception {
    final String pdb1EHZ = ResourcesHelper.loadResource("1EHZ.pdb");
    final String pdb2Z74 = ResourcesHelper.loadResource("2Z74.pdb");
    final String pdb2MIY = ResourcesHelper.loadResource("2MIY.pdb");
    final String bpseq1EHZ = ResourcesHelper.loadResource("1EHZ-2D-bpseq.txt");
    final String bpseq2Z74 = ResourcesHelper.loadResource("2Z74-2D-bpseq.txt");
    final String bpseq2MIY = ResourcesHelper.loadResource("2MIY-2D-bpseq.txt");
    PdbModelTest.assertBpSeqEquals(pdb1EHZ, bpseq1EHZ);
    PdbModelTest.assertBpSeqEquals(pdb2Z74, bpseq2Z74);
    PdbModelTest.assertBpSeqEquals(pdb2MIY, bpseq2MIY);
  }

  @Test
  public final void testFrabaseExport() throws Exception {
    final String pdbFrabaseExport = ResourcesHelper.loadResource("FrabaseExport.pdb");
    final PdbParser parser = new PdbParser(false);
    final List<PdbModel> models = parser.parse(pdbFrabaseExport);
    assertEquals(2, models.size());

    PdbModel model = models.get(0);
    List<PdbChain> chains = model.getChains();
    List<PdbResidue> residues = model.getResidues();
    List<PdbAtomLine> atoms = model.getAtoms();
    assertEquals(1, chains.size());
    assertEquals(7, residues.size());
    assertEquals(150, atoms.size());

    model = models.get(1);
    chains = model.getChains();
    residues = model.getResidues();
    atoms = model.getAtoms();
    assertEquals(1, chains.size());
    assertEquals(7, residues.size());
    assertEquals(150, atoms.size());
  }

  @Test
  public final void testModelWithResiduesUNK() throws Exception {
    final String pdb3KFU = ResourcesHelper.loadResource("3KFU.pdb");
    final PdbParser parser = new PdbParser(false);
    final List<PdbModel> models = parser.parse(pdb3KFU);
    assertEquals(1, models.size());

    final PdbModel model = models.get(0);
    assertEquals(14, model.getChains().size());

    final PdbModel rna = model.filteredNewInstance(MoleculeType.RNA);
    assertEquals(4, rna.getChains().size());
  }

  @Test
  public final void test148L() throws Exception {
    final String pdb148L = ResourcesHelper.loadResource("148L.pdb");
    final PdbParser parser = new PdbParser();
    final List<PdbModel> models = parser.parse(pdb148L);
    assertEquals(1, models.size());
    final PdbModel model = models.get(0);

    PdbResidue residue = model.findResidue("E", 164, " ");
    assertThat(residue.isMissing(), is(true));

    residue = model.findResidue("S", 169, " ");
    assertThat(residue.getOriginalResidueName(), is("API"));
    assertThat(residue.getModifiedResidueName(), is("LYS"));
  }

  @Test
  public final void testCif() throws Exception {
    final String pdb1EHZ = ResourcesHelper.loadResource("1EHZ.pdb");
    final PdbParser pdbParser = new PdbParser();
    final List<PdbModel> pdbModels = pdbParser.parse(pdb1EHZ);
    assertEquals(1, pdbModels.size());
    final PdbModel pdbModel = pdbModels.get(0);

    final String cif1EHZ = pdbModel.toCifString();

    final StructureParser cifParser = new CifParser();
    final List<PdbModel> cifModels = cifParser.parse(cif1EHZ);
    assertEquals(1, cifModels.size());
    final PdbModel cifModel = cifModels.get(0);

    final List<PdbResidue> pdbResidues = pdbModel.getResidues();
    final List<PdbResidue> cifResidues = cifModel.getResidues();
    assertEquals(cifResidues.size(), pdbResidues.size());

    for (int i = 0; i < pdbResidues.size(); i++) {
      final PdbResidue pdbResidue = pdbResidues.get(i);
      final PdbResidue cifResidue = cifResidues.get(i);
      if (!pdbResidue.isModified()) {
        assertThat(cifResidue, is(pdbResidue));
      }
    }
  }
}
