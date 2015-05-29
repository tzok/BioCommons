package pl.poznan.put;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.biojava.bio.structure.Structure;
import org.biojava.bio.structure.io.PDBFileParser;
import org.junit.Before;
import org.junit.Test;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbParsingException;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.pdb.analysis.PdbChain;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.pdb.analysis.PdbParser;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.structure.secondary.formats.BpSeq;
import pl.poznan.put.structure.secondary.formats.InvalidSecondaryStructureException;

public class TestPdbModel {
    private String pdb1EHZ;
    private String pdb2Z74;
    private String pdb4A04;
    private String pdbPKB300;
    private String pdbAmber;
    private String pdb2MIY;

    private String bpseq1EHZ;
    private String bpseq2Z74;
    private String bpseq2MIY;

    @Before
    public void loadPdbFile() throws URISyntaxException, IOException {
        URI uri = getClass().getClassLoader().getResource(".").toURI();
        File dir = new File(uri);
        pdb1EHZ = FileUtils.readFileToString(new File(dir, "../../src/test/resources/1EHZ.pdb"), "utf-8");
        pdb2Z74 = FileUtils.readFileToString(new File(dir, "../../src/test/resources/2Z74.pdb"), "utf-8");
        pdb4A04 = FileUtils.readFileToString(new File(dir, "../../src/test/resources/4A04.pdb"), "utf-8");
        pdbPKB300 = FileUtils.readFileToString(new File(dir, "../../src/test/resources/PKB300.pdb"), "utf-8");
        pdbAmber = FileUtils.readFileToString(new File(dir, "../../src/test/resources/amber.pdb"), "utf-8");
        pdb2MIY = FileUtils.readFileToString(new File(dir, "../../src/test/resources/2MIY.pdb"), "utf-8");

        bpseq1EHZ = FileUtils.readFileToString(new File(dir, "../../src/test/resources/1EHZ-2D-bpseq.txt"), "utf-8");
        bpseq2Z74 = FileUtils.readFileToString(new File(dir, "../../src/test/resources/2Z74-2D-bpseq.txt"), "utf-8");
        bpseq2MIY = FileUtils.readFileToString(new File(dir, "../../src/test/resources/2MIY-2D-bpseq.txt"), "utf-8");
    }

    @Test
    public void testParsing() throws PdbParsingException {
        PdbParser parser = new PdbParser();
        List<PdbModel> models = parser.parse(pdb1EHZ);
        assertEquals(1, models.size());
    }

    @Test
    public void testResidueAnalysis() throws PdbParsingException {
        PdbParser parser = new PdbParser();
        List<PdbModel> models = parser.parse(pdb1EHZ);
        PdbModel model = models.get(0);
        List<PdbResidue> residues = model.getResidues();
        assertEquals(76, residues.size());
    }

    @Test
    public void testChainAnalysis() throws PdbParsingException {
        PdbParser parser = new PdbParser();
        List<PdbModel> models = parser.parse(pdb1EHZ);
        PdbModel model = models.get(0);
        List<PdbChain> chains = model.getChains();
        assertEquals(1, chains.size());
    }

    @Test
    public void testModifiedResidue() throws PdbParsingException {
        PdbParser parser = new PdbParser();
        List<PdbModel> models = parser.parse(pdb1EHZ);
        PdbModel model = models.get(0);
        PdbResidue residue = model.findResidue(new PdbResidueIdentifier('A', 10, ' '));
        assertEquals("2MG", residue.getOriginalResidueName());
        assertEquals("G", residue.getModifiedResidueName());
        assertEquals("G", residue.getDetectedResidueName());
    }

    @Test
    public void testUnmodifiedResidue() throws PdbParsingException {
        PdbParser parser = new PdbParser();
        List<PdbModel> models = parser.parse(pdb1EHZ);
        PdbModel model = models.get(0);
        PdbResidue residue = model.findResidue('A', 74, ' ');
        assertEquals("C", residue.getOriginalResidueName());
        assertEquals("C", residue.getModifiedResidueName());
        assertEquals("C", residue.getDetectedResidueName());
    }

    @Test
    public void testO3PModificationDetection() throws PdbParsingException {
        PdbParser parser = new PdbParser();
        List<PdbModel> models = parser.parse(pdb1EHZ);
        PdbModel model = models.get(0);
        List<PdbResidue> residues = model.getResidues();

        // for first residue expect a mismatch in atom count because it has an
        // unusual O3P terminal atom
        PdbResidue residue = residues.get(0);
        assertEquals(true, residue.isModified());
        assertEquals(false, residue.hasAllAtoms());
    }

    @Test
    public void testUridineModificationDetection() throws PdbParsingException {
        PdbParser parser = new PdbParser();
        List<PdbModel> models = parser.parse(pdb1EHZ);
        PdbModel model = models.get(0);

        // the H2U (dihydrouridine) is modified by two additional hydrogens
        // which is undetectable in a non-hydrogen PDB file
        PdbResidue residue = model.findResidue('A', 16, ' ');
        assertEquals(true, residue.isModified());
        assertEquals(true, residue.hasAllAtoms());

        // the PSU (pseudouridine) contains the same atoms, but it is an isomer
        // and therefore a modified residue
        residue = model.findResidue('A', 39, ' ');
        assertEquals(true, residue.isModified());
        assertEquals(true, residue.hasAllAtoms());

        // the 5MU (methyluridine) is the RNA counterpart of thymine
        residue = model.findResidue('A', 54, ' ');
        assertEquals("5MU", residue.getOriginalResidueName());
        assertEquals("U", residue.getModifiedResidueName());
        assertEquals("U", residue.getDetectedResidueName());
        assertEquals(true, residue.isModified());
        assertEquals(false, residue.hasAllAtoms());
    }

    @Test
    public void testPSUModificationDetection() throws PdbParsingException {
        PdbParser parser = new PdbParser();
        List<PdbModel> models = parser.parse(pdb1EHZ);
        PdbModel model = models.get(0);

        // the PSU (dihydrouridine) is modified by two additional hydrogens
        // which is undetectable in a non-hydrogen PDB file; there can be a
        // mismatch between MODRES entry and hasAllAtoms() call
        PdbResidue residue = model.findResidue('A', 39, ' ');
        assertEquals(residue.isModified(), residue.hasAllAtoms());
    }

    @Test
    public void testModificationDetection() throws PdbParsingException {
        PdbParser parser = new PdbParser();
        List<PdbModel> models = parser.parse(pdb1EHZ);
        PdbModel model = models.get(0);

        for (PdbResidue residue : model.getResidues()) {
            if (!residue.wasSuccessfullyDetected()) {
                continue;
            }

            if (residue.hasAtom(AtomName.O3P)) {
                assertEquals(true, residue.isModified());
                assertEquals(false, residue.hasAllAtoms());
            } else if (residue.getOriginalResidueName().equals("H2U") || residue.getOriginalResidueName().equals("PSU")) {
                assertEquals(true, residue.isModified());
                assertEquals(true, residue.hasAllAtoms());
            } else if (residue.getOriginalResidueName().equals("5MU")) {
                assertEquals("5MU", residue.getOriginalResidueName());
                assertEquals("U", residue.getModifiedResidueName());
                assertEquals("U", residue.getDetectedResidueName());
                assertEquals(true, residue.isModified());
                assertEquals(false, residue.hasAllAtoms());
            } else {
                assertEquals("Detected " + residue.getDetectedResidueName() + " for " + residue.getOriginalResidueName() + "/" + residue.getModifiedResidueName(), residue.getModifiedResidueName(), residue.getDetectedResidueName());
                assertEquals(residue.isModified(), !residue.hasAllAtoms());
            }
        }
    }

    @Test
    public void testResidueInTerminatedChain() throws PdbParsingException {
        PdbParser parser = new PdbParser();
        List<PdbModel> models = parser.parse(pdb2Z74);
        PdbModel model = models.get(0);
        List<PdbChain> chains = model.getChains();
        assertEquals("Found chains (expected [A, B]): " + Arrays.toString(chains.toArray(new PdbChain[chains.size()])), 2, chains.size());
    }

    @Test
    public void testMissingResidues() throws PdbParsingException {
        PdbParser parser = new PdbParser();
        List<PdbModel> models = parser.parse(pdb2Z74);
        PdbModel model = models.get(0);
        PdbResidue residue = model.findResidue('A', 21, ' ');
        assertEquals(true, residue.isMissing());
        residue = model.findResidue('B', 21, ' ');
        assertEquals(true, residue.isMissing());
    }

    @Test
    public void testOutputParsable() throws PdbParsingException {
        PdbParser parser = new PdbParser();
        List<PdbModel> models = parser.parse(pdb1EHZ);
        PdbModel model = models.get(0);
        List<PdbModel> parsed = parser.parse(model.toPdbString());

        assertEquals(1, models.size());
        assertEquals(1, parsed.size());

        assertEquals(1, models.get(0).getChains().size());
        assertEquals(1, parsed.get(0).getChains().size());

        assertEquals(76, models.get(0).getChains().get(0).getResidues().size());
        assertEquals(76, parsed.get(0).getChains().get(0).getResidues().size());
    }

    @Test
    public void testOutputParsableByBioJava() throws IOException, PdbParsingException {
        PdbParser parser = new PdbParser();
        List<PdbModel> models = parser.parse(pdb1EHZ);
        PdbModel model = models.get(0);

        PDBFileParser fileParser = new PDBFileParser();
        Structure structure = fileParser.parsePDBFile(IOUtils.toInputStream(model.toPdbString()));

        assertEquals(1, structure.getChains().size());
        assertEquals(1, model.getChains().size());

        assertEquals(76, structure.getChain(0).getAtomGroups().size());
        assertEquals(76, model.getChains().get(0).getResidues().size());
    }

    @Test
    public void testSequence() throws PdbParsingException {
        PdbParser parser = new PdbParser();
        List<PdbModel> models = parser.parse(pdb1EHZ);
        PdbModel model = models.get(0);
        String sequence = model.getSequence();
        assertEquals("GCGGAUUUAGCUCAGUUGGGAGAGCGCCAGACUGAAGAUCUGGAGGUCCUGUGUUCGAUCCACAGAAUUCGCACCA", sequence.toUpperCase());

        models = parser.parse(pdb2Z74);
        model = models.get(0);
        List<PdbChain> chains = model.getChains();
        sequence = chains.get(0).getSequence();
        assertEquals("AGCGCCUGGACUUAAAGCCAUUGCACU", sequence.toUpperCase());
        sequence = chains.get(1).getSequence();
        assertEquals("CCGGCUUUAAGUUGACGAGGGCAGGGUUUAUCGAGACAUCGGCGGGUGCCCUGCGGUCUUCCUGCGACCGUUAGAGGACUGGUAAAACCACAGGCGACUGUGGCAUAGAGCAGUCCGGGCAGGAA", sequence.toUpperCase());
        sequence = model.getSequence();
        assertEquals("AGCGCCUGGACUUAAAGCCAUUGCACUCCGGCUUUAAGUUGACGAGGGCAGGGUUUAUCGAGACAUCGGCGGGUGCCCUGCGGUCUUCCUGCGACCGUUAGAGGACUGGUAAAACCACAGGCGACUGUGGCAUAGAGCAGUCCGGGCAGGAA", sequence.toUpperCase());

        models = parser.parse(pdb4A04);
        model = models.get(0);
        chains = model.getChains();
        sequence = chains.get(0).getSequence();
        assertEquals("MHHHHHHENLYFQGGVSVQLEMKALWDEFNQLGTEMIVTKAGRRMFPTFQVKLFGMDPMADYMLLMDFVPVDDKRYRYAFHSSSWLVAGKADPATPGRVHYHPDSPAKGAQWMKQIVSFDKLKLTNNLLDDNGHIILNSMHRYQPRFHVVYVDPRKDSEKYAEENFKTFVFEETRFTAVTAYQNHRITQLKIASNPFAKGFRD", sequence.toUpperCase());
        sequence = chains.get(1).getSequence();
        assertEquals("MHHHHHHENLYFQGGVSVQLEMKALWDEFNQLGTEMIVTKAGRRMFPTFQVKLFGMDPMADYMLLMDFVPVDDKRYRYAFHSSSWLVAGKADPATPGRVHYHPDSPAKGAQWMKQIVSFDKLKLTNNLLDDNGHIILNSMHRYQPRFHVVYVDPRKDSEKYAEENFKTFVFEETRFTAVTAYQNHRITQLKIASNPFAKGFRD", sequence.toUpperCase());
        sequence = chains.get(2).getSequence();
        assertEquals("AATTTCACACCTAGGTGTGAAATT", sequence.toUpperCase());
        sequence = chains.get(2).getSequence();
        assertEquals("AATTTCACACCTAGGTGTGAAATT", sequence.toUpperCase());
    }

    @Test
    public void test79CharLongAtomLines() throws PdbParsingException {
        PdbParser parser = new PdbParser(false);
        List<PdbModel> models = parser.parse(pdbPKB300);
        assertEquals(1, models.size());
        PdbModel model = models.get(0);
        List<PdbChain> chains = model.getChains();
        List<PdbResidue> residues = model.getResidues();
        List<PdbAtomLine> atoms = model.getAtoms();

        assertEquals(1, chains.size());
        assertEquals(37, residues.size());
        assertEquals(1186, atoms.size());
    }

    @Test
    public void testAmberModel() throws PdbParsingException {
        PdbParser parser = new PdbParser(false);
        List<PdbModel> models = parser.parse(pdbAmber);
        assertEquals(1, models.size());
        PdbModel model = models.get(0);
        List<PdbChain> chains = model.getChains();
        List<PdbResidue> residues = model.getResidues();
        List<PdbAtomLine> atoms = model.getAtoms();

        assertEquals(3, chains.size());
        assertEquals(49, residues.size());
        assertEquals(1557, atoms.size());
    }

    @Test
    public void testNMRModelsWithHydrogenAtoms() throws PdbParsingException {
        PdbParser parser = new PdbParser(false);
        List<PdbModel> models = parser.parse(pdb2MIY);
        assertEquals(18, models.size());

        PdbModel model = models.get(0);
        List<PdbChain> chains = model.getChains();
        List<PdbResidue> residues = model.getResidues();
        List<PdbAtomLine> atoms = model.getAtoms();
        assertEquals(1, chains.size());
        assertEquals(59, residues.size());
        assertEquals(1909, atoms.size());

        PdbChain chain = chains.get(0);
        String sequence = chain.getSequence();
        assertEquals(true, Character.isLowerCase(sequence.charAt(0)));
        assertEquals(true, StringUtils.isAllUpperCase(sequence.substring(1)));
    }

    @Test
    public void testCanonicalSecondaryStructure() throws PdbParsingException, InvalidSecondaryStructureException {
        assertBpSeqEquals(pdb1EHZ, bpseq1EHZ);
        assertBpSeqEquals(pdb2Z74, bpseq2Z74);
        assertBpSeqEquals(pdb2MIY, bpseq2MIY);
    }

    private static void assertBpSeqEquals(String pdbString, String bpSeqString) throws PdbParsingException, InvalidSecondaryStructureException {
        PdbParser parser = new PdbParser(false);
        List<PdbModel> models = parser.parse(pdbString);
        PdbModel model = models.get(0);

        BpSeq bpSeqFromModel = model.getCanonicalSecondaryStructure();
        assertEquals(bpSeqString, bpSeqFromModel.toString());

        BpSeq bpSeqFromString = BpSeq.fromString(bpSeqString);
        assertEquals(bpSeqFromString, bpSeqFromModel);
    }
}
