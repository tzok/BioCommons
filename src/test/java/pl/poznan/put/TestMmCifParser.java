package pl.poznan.put;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.poznan.put.pdb.PdbParsingException;
import pl.poznan.put.pdb.analysis.MmCifParser;
import pl.poznan.put.pdb.analysis.PdbChain;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.pdb.analysis.PdbResidue;

import java.io.IOException;
import java.util.List;

/**
 * Created by tzok on 24.05.16.
 */
public class TestMmCifParser {
    private String cif100D;
    private String cif148L;

    @Before
    public void readTestData() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        cif100D = IOUtils.toString(classLoader.getResourceAsStream("100D.cif"));
        cif148L = IOUtils.toString(classLoader.getResourceAsStream("148L.cif"));
    }

    @Test
    public void test100D() throws IOException, PdbParsingException {
        MmCifParser parser = new MmCifParser();
        List<PdbModel> models = parser.parse(cif100D);
        Assert.assertEquals(1, models.size());
        PdbModel model = models.get(0);
        List<PdbChain> chains = model.getChains();
        Assert.assertEquals(2, chains.size());
    }

    @Test
    public void test148L() throws IOException, PdbParsingException {
        MmCifParser parser = new MmCifParser();
        List<PdbModel> models = parser.parse(cif148L);
        Assert.assertEquals(1, models.size());
        PdbModel model = models.get(0);

        PdbResidue residue = model.findResidue("E", 164, " ");
        Assert.assertTrue(residue.isMissing());

        residue = model.findResidue("S", 169, " ");
        Assert.assertEquals("API", residue.getOriginalResidueName());
        Assert.assertEquals("LYS", residue.getModifiedResidueName());
    }
}
