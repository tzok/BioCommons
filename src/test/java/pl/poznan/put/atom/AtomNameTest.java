package pl.poznan.put.atom;

import org.junit.Test;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.analysis.PdbParser;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.utility.ResourcesHelper;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

public class AtomNameTest {
  @Test
  public final void testAtomName() throws Exception {
    final String pdb1EHZ = ResourcesHelper.loadResource("1EHZ.pdb");
    final String pdb2Z74 = ResourcesHelper.loadResource("2Z74.pdb");
    final PdbParser parser = new PdbParser();

    for (final String pdbContent : new String[] {pdb1EHZ, pdb2Z74}) {
      for (final PdbModel model : parser.parse(pdbContent)) {
        for (final PdbAtomLine atom : model.atoms()) {
          final AtomName atomName = atom.detectAtomName();
          assertThat(
              String.format("Unknown atom: %s", atom.atomName()),
              atomName,
              not(is(AtomName.UNKNOWN)));
        }
      }
    }
  }
}
