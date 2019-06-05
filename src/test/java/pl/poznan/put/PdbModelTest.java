package pl.poznan.put;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.io.PDBFileParser;
import org.junit.Test;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbParsingException;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.pdb.analysis.*;
import pl.poznan.put.structure.secondary.CanonicalStructureExtractor;
import pl.poznan.put.structure.secondary.formats.BpSeq;
import pl.poznan.put.structure.secondary.formats.InvalidStructureException;
import pl.poznan.put.utility.ResourcesHelper;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class PdbModelTest {
  private static void assertBpSeqEquals(final String pdbString, final String bpSeqString)
      throws PdbParsingException, InvalidStructureException {
    final PdbParser parser = new PdbParser(false);
    final List<PdbModel> models = parser.parse(pdbString);
    final PdbModel model = models.get(0);

    final BpSeq bpSeqFromModel = CanonicalStructureExtractor.bpSeq(model);
    assertEquals(bpSeqString, bpSeqFromModel.toString());

    final BpSeq bpSeqFromString = BpSeq.fromString(bpSeqString);
    assertEquals(bpSeqFromString, bpSeqFromModel);
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
    assertEquals("2MG", residue.getOriginalResidueName());
    assertEquals("G", residue.getModifiedResidueName());
    assertEquals("G", residue.getDetectedResidueName());
  }

  @Test
  public final void testUnmodifiedResidue() throws Exception {
    final String pdb1EHZ = ResourcesHelper.loadResource("1EHZ.pdb");
    final PdbParser parser = new PdbParser();
    final List<PdbModel> models = parser.parse(pdb1EHZ);
    final PdbModel model = models.get(0);
    final PdbResidue residue = model.findResidue("A", 74, " ");
    assertEquals("C", residue.getOriginalResidueName());
    assertEquals("C", residue.getModifiedResidueName());
    assertEquals("C", residue.getDetectedResidueName());
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
    assertTrue(residue.isModified());
    assertFalse(residue.hasAllAtoms());
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
    assertTrue(residue.isModified());
    assertTrue(residue.hasAllAtoms());

    // the PSU (pseudouridine) contains the same atoms, but it is an isomer
    // and therefore a modified residue
    residue = model.findResidue("A", 39, " ");
    assertTrue(residue.isModified());
    assertTrue(residue.hasAllAtoms());

    // the 5MU (methyluridine) is the RNA counterpart of thymine
    residue = model.findResidue("A", 54, " ");
    assertEquals("5MU", residue.getOriginalResidueName());
    assertEquals("U", residue.getModifiedResidueName());
    assertEquals("U", residue.getDetectedResidueName());
    assertTrue(residue.isModified());
    assertFalse(residue.hasAllAtoms());
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
    assertEquals(residue.isModified(), residue.hasAllAtoms());
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
        assertTrue(residue.isModified());
        assertFalse(residue.hasAllAtoms());
      } else if (residue.getOriginalResidueName().equals("H2U")
          || residue.getOriginalResidueName().equals("PSU")) {
        assertTrue(residue.isModified());
        assertTrue(residue.hasAllAtoms());
      } else if (residue.getOriginalResidueName().equals("5MU")) {
        assertEquals("5MU", residue.getOriginalResidueName());
        assertEquals("U", residue.getModifiedResidueName());
        assertEquals("U", residue.getDetectedResidueName());
        assertTrue(residue.isModified());
        assertFalse(residue.hasAllAtoms());
      } else {
        assertEquals(
            String.format(
                "Detected %s for %s/%s",
                residue.getDetectedResidueName(),
                residue.getOriginalResidueName(),
                residue.getModifiedResidueName()),
            residue.getModifiedResidueName(),
            residue.getDetectedResidueName());
        assertEquals(residue.isModified(), !residue.hasAllAtoms());
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
            "Found chains (expected [A, B]): %s",
            Arrays.toString(chains.toArray(new PdbChain[chains.size()]))),
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
    assertTrue(residue.isMissing());
    residue = model.findResidue("B", 21, " ");
    assertTrue(residue.isMissing());
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
    assertEquals(
        "GCGGAUUUAGCUCAGUUGGGAGAGCGCCAGACUGAAGAUCUGGAGGUCCUGUGUUCGAUCCACAGAAUUCGCACCA",
        sequence.toUpperCase());

    final String pdb2Z74 = ResourcesHelper.loadResource("2Z74.pdb");
    models = parser.parse(pdb2Z74);
    model = models.get(0);
    List<PdbChain> chains = model.getChains();
    sequence = chains.get(0).getSequence();
    assertEquals("AGCGCCUGGACUUAAAGCCAUUGCACU", sequence.toUpperCase());
    sequence = chains.get(1).getSequence();
    assertEquals(
        "CCGGCUUUAAGUUGACGAGGGCAGGGUUUAUCGAGACAUCGGCGGGUGCCCUGCGGUCUUCCUGCGACCGUUAGAGGACUGGUAAAACCACAGGCGACUGUGGCAUAGAGCAGUCCGGGCAGGAA",
        sequence.toUpperCase());
    sequence = model.getSequence();
    assertEquals(
        "AGCGCCUGGACUUAAAGCCAUUGCACUCCGGCUUUAAGUUGACGAGGGCAGGGUUUAUCGAGACAUCGGCGGGUGCCCUGCGGUCUUCCUGCGACCGUUAGAGGACUGGUAAAACCACAGGCGACUGUGGCAUAGAGCAGUCCGGGCAGGAA",
        sequence.toUpperCase());

    final String pdb4A04 = ResourcesHelper.loadResource("4A04.pdb");
    models = parser.parse(pdb4A04);
    model = models.get(0);
    chains = model.getChains();
    sequence = chains.get(0).getSequence();
    assertEquals(
        "MHHHHHHENLYFQGGVSVQLEMKALWDEFNQLGTEMIVTKAGRRMFPTFQVKLFGMDPMADYMLLMDFVPVDDKRYRYAFHSSSWLVAGKADPATPGRVHYHPDSPAKGAQWMKQIVSFDKLKLTNNLLDDNGHIILNSMHRYQPRFHVVYVDPRKDSEKYAEENFKTFVFEETRFTAVTAYQNHRITQLKIASNPFAKGFRD",
        sequence.toUpperCase());
    sequence = chains.get(1).getSequence();
    assertEquals(
        "MHHHHHHENLYFQGGVSVQLEMKALWDEFNQLGTEMIVTKAGRRMFPTFQVKLFGMDPMADYMLLMDFVPVDDKRYRYAFHSSSWLVAGKADPATPGRVHYHPDSPAKGAQWMKQIVSFDKLKLTNNLLDDNGHIILNSMHRYQPRFHVVYVDPRKDSEKYAEENFKTFVFEETRFTAVTAYQNHRITQLKIASNPFAKGFRD",
        sequence.toUpperCase());
    sequence = chains.get(2).getSequence();
    assertEquals("AATTTCACACCTAGGTGTGAAATT", sequence.toUpperCase());
    sequence = chains.get(2).getSequence();
    assertEquals("AATTTCACACCTAGGTGTGAAATT", sequence.toUpperCase());
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
    assertTrue(Character.isLowerCase(sequence.charAt(0)));
    assertTrue(StringUtils.isAllUpperCase(sequence.substring(1)));
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
    assertTrue(residue.isMissing());

    residue = model.findResidue("S", 169, " ");
    assertEquals("API", residue.getOriginalResidueName());
    assertEquals("LYS", residue.getModifiedResidueName());
  }

  @Test
  public final void testCif() throws Exception {
    final String pdb1EHZ = ResourcesHelper.loadResource("1EHZ.pdb");
    final PdbParser pdbParser = new PdbParser();
    final List<PdbModel> pdbModels = pdbParser.parse(pdb1EHZ);
    assertEquals(1, pdbModels.size());
    final PdbModel pdbModel = pdbModels.get(0);

    final String cif1EHZ = pdbModel.toCifString();

    final CifParser cifParser = new CifParser();
    final List<PdbModel> cifModels = cifParser.parse(cif1EHZ);
    assertEquals(1, cifModels.size());
    final PdbModel cifModel = cifModels.get(0);

    final List<PdbResidue> pdbResidues = pdbModel.getResidues();
    final List<PdbResidue> cifResidues = cifModel.getResidues();
    assertEquals(pdbResidues.size(), cifResidues.size());

    for (int i = 0; i < pdbResidues.size(); i++) {
      final PdbResidue pdbResidue = pdbResidues.get(i);
      final PdbResidue cifResidue = cifResidues.get(i);
      if (!pdbResidue.isModified()) {
        assertEquals(pdbResidue, cifResidue);
      }
    }
  }
}
