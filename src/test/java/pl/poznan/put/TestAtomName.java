package pl.poznan.put;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbParsingException;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.pdb.analysis.PdbParser;

public class TestAtomName {
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
    public void testAtomName() throws PdbParsingException {
        PdbParser parser = new PdbParser();
        for (String pdbContent : new String[] { pdb1EHZ, pdb2Z74 }) {
            for (PdbModel model : parser.parse(pdbContent)) {
                for (PdbAtomLine atom : model.getAtoms()) {
                    AtomName atomName = atom.detectAtomName();
                    assertNotEquals("Unknown atom: " + atom.getAtomName(), AtomName.UNKNOWN, atomName);
                }
            }
        }
    }
}
