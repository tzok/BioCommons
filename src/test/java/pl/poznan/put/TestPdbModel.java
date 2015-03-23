package pl.poznan.put;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.analysis.PdbChain;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.pdb.analysis.PdbParser;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.pdb.analysis.PdbResidueIdentifier;

public class TestPdbModel {
    private String pdb1EHZ;
    private String pdb2Z74;

    @Before
    public void loadPdbFile() throws URISyntaxException, IOException {
        URI uri = getClass().getClassLoader().getResource(".").toURI();
        File dir = new File(uri);
        pdb1EHZ = FileUtils.readFileToString(new File(dir, "../../src/test/resources/1EHZ.pdb"), "utf-8");
        pdb2Z74 = FileUtils.readFileToString(new File(dir, "../../src/test/resources/2Z74.pdb"), "utf-8");
    }

    @Test
    public void testParsing() {
        PdbParser parser = new PdbParser();
        List<PdbModel> models = parser.parse(pdb1EHZ);
        assertEquals(1, models.size());
    }

    @Test
    public void testResidueAnalysis() {
        PdbParser parser = new PdbParser();
        List<PdbModel> models = parser.parse(pdb1EHZ);
        PdbModel model = models.get(0);
        List<PdbResidue> residues = model.getResidues();
        assertEquals(76, residues.size());
    }

    @Test
    public void testChainAnalysis() {
        PdbParser parser = new PdbParser();
        List<PdbModel> models = parser.parse(pdb1EHZ);
        PdbModel model = models.get(0);
        List<PdbChain> chains = model.getChains();
        assertEquals(1, chains.size());
    }

    @Test
    public void testModifiedResidue() {
        PdbParser parser = new PdbParser();
        List<PdbModel> models = parser.parse(pdb1EHZ);
        PdbModel model = models.get(0);
        PdbResidue residue = model.findResidue(new PdbResidueIdentifier('A', 10, ' '));
        assertEquals("2MG", residue.getOriginalResidueName());
        assertEquals("G", residue.getModifiedResidueName());
        assertEquals("G", residue.getDetectedResidueName());
    }

    @Test
    public void testUnmodifiedResidue() {
        PdbParser parser = new PdbParser();
        List<PdbModel> models = parser.parse(pdb1EHZ);
        PdbModel model = models.get(0);
        PdbResidue residue = model.findResidue('A', 74, ' ');
        assertEquals("C", residue.getOriginalResidueName());
        assertEquals("C", residue.getModifiedResidueName());
        assertEquals("C", residue.getDetectedResidueName());
    }

    @Test
    public void testO3PModificationDetection() {
        PdbParser parser = new PdbParser();
        List<PdbModel> models = parser.parse(pdb1EHZ);
        PdbModel model = models.get(0);
        List<PdbResidue> residues = model.getResidues();

        // for first residue expect a mismatch in atom count because it has an
        // unusual O3P terminal atom
        PdbResidue residue = residues.get(0);
        assertEquals(false, residue.isModified());
        assertEquals(false, residue.hasAllAtoms());
    }

    @Test
    public void testUridineModificationDetection() {
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
        assertEquals("T", residue.getDetectedResidueName());
        assertEquals(true, residue.isModified());
        assertEquals(true, residue.hasAllAtoms());
    }

    @Test
    public void testPSUModificationDetection() {
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
    public void testModificationDetection() {
        PdbParser parser = new PdbParser();
        List<PdbModel> models = parser.parse(pdb1EHZ);
        PdbModel model = models.get(0);

        for (PdbResidue residue : model.getResidues()) {
            if (!residue.wasSuccessfullyDetected()) {
                continue;
            }

            if (residue.hasAtom(AtomName.O3P)) {
                assertEquals(false, residue.isModified());
                assertEquals(false, residue.hasAllAtoms());
            } else if (residue.getOriginalResidueName().equals("H2U") || residue.getOriginalResidueName().equals("PSU")) {
                assertEquals(true, residue.isModified());
                assertEquals(true, residue.hasAllAtoms());
            } else if (residue.getOriginalResidueName().equals("5MU")) {
                assertEquals("5MU", residue.getOriginalResidueName());
                assertEquals("U", residue.getModifiedResidueName());
                assertEquals("T", residue.getDetectedResidueName());
                assertEquals(true, residue.isModified());
                assertEquals(true, residue.hasAllAtoms());
            } else {
                assertEquals("Detected " + residue.getDetectedResidueName() + " for " + residue.getOriginalResidueName() + "/" + residue.getModifiedResidueName(), residue.getModifiedResidueName(), residue.getDetectedResidueName());
                assertEquals(residue.isModified(), !residue.hasAllAtoms());
            }
        }
    }

    @Test
    public void testResidueInTerminatedChain() {
        PdbParser parser = new PdbParser();
        List<PdbModel> models = parser.parse(pdb2Z74);
        PdbModel model = models.get(0);
        List<PdbChain> chains = model.getChains();
        assertEquals("Found chains (expected [A, B, a]): " + Arrays.toString(chains.toArray(new PdbChain[chains.size()])), 3, chains.size());
    }

    @Test
    public void testMissingResidues() {
        PdbParser parser = new PdbParser();
        List<PdbModel> models = parser.parse(pdb2Z74);
        PdbModel model = models.get(0);
        PdbResidue residue = model.findResidue('A', 21, ' ');
        assertEquals(true, residue.isMissing());
        residue = model.findResidue('B', 21, ' ');
        assertEquals(true, residue.isMissing());
    }
}
