package pl.poznan.put.rna;

import junit.framework.TestCase;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import pl.poznan.put.pdb.ImmutablePdbResidueIdentifier;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.pdb.analysis.PdbParser;
import pl.poznan.put.utility.ResourcesHelper;

import java.io.IOException;

public class BasePairParametersTest extends TestCase {
  public void test1EHZ_A10_A45() throws IOException {
    final PdbModel model = new PdbParser().parse(ResourcesHelper.loadResource("1EHZ.pdb")).get(0);
    final StandardReferenceFrame frame1 =
        StandardReferenceFrame.ofResidue(
            model.findResidue(ImmutablePdbResidueIdentifier.of("A", 10, " ")));
    final StandardReferenceFrame frame2 =
        StandardReferenceFrame.ofResidue(
            model.findResidue(ImmutablePdbResidueIdentifier.of("A", 45, " ")));
    final BasePairParameters parameters = BasePairParameters.of(frame1, frame2);

    Assert.assertTrue(FastMath.abs(parameters.shear() - (-0.3)) < 0.1);
    Assert.assertTrue(FastMath.abs(parameters.stretch() - 7.3) < 0.1);
    Assert.assertTrue(FastMath.abs(parameters.stagger() - (-1.0)) < 0.1);
    Assert.assertTrue(FastMath.abs(parameters.buckle().degrees() - (-13.4)) < 0.1);
    Assert.assertTrue(FastMath.abs(parameters.propeller().degrees() - (-24.1)) < 0.1);
    Assert.assertTrue(FastMath.abs(parameters.opening().degrees() - 13.1) < 0.1);
    Assert.assertTrue(FastMath.abs(parameters.interBaseAngle().degrees() - 27.6) < 0.1);
  }

  public void test1EHZ_A6_A67() throws IOException {
    final PdbModel model = new PdbParser().parse(ResourcesHelper.loadResource("1EHZ.pdb")).get(0);
    final StandardReferenceFrame frame1 =
        StandardReferenceFrame.ofResidue(
            model.findResidue(ImmutablePdbResidueIdentifier.of("A", 6, " ")));
    final StandardReferenceFrame frame2 =
        StandardReferenceFrame.ofResidue(
            model.findResidue(ImmutablePdbResidueIdentifier.of("A", 67, " ")));
    final BasePairParameters parameters = BasePairParameters.of(frame1, frame2);

    Assert.assertTrue(FastMath.abs(parameters.shear() - 0.1) < 0.1);
    Assert.assertTrue(FastMath.abs(parameters.stretch() - (-0.1)) < 0.1);
    Assert.assertTrue(FastMath.abs(parameters.stagger() - (-0.1)) < 0.1);
    Assert.assertTrue(FastMath.abs(parameters.buckle().degrees() - (-0.7)) < 0.1);
    Assert.assertTrue(FastMath.abs(parameters.propeller().degrees() - (-23.4)) < 0.1);
    Assert.assertTrue(FastMath.abs(parameters.opening().degrees() - 5.9) < 0.1);
    Assert.assertTrue(FastMath.abs(parameters.interBaseAngle().degrees() - 23.4) < 0.1);
  }
}
