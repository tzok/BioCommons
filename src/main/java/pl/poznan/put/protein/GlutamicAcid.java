package pl.poznan.put.protein;

import org.immutables.value.Value;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.analysis.ResidueComponent;
import pl.poznan.put.torsion.TorsionAngleType;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Value.Immutable(singleton = true)
abstract class GlutamicAcid implements Sidechain {
  @Override
  public final List<ResidueComponent> moleculeComponents() {
    return Stream.of(ImmutableBackbone.of(), ImmutableGlutamicAcid.of())
        .collect(Collectors.toList());
  }

  @Override
  public final char oneLetterName() {
    return 'E';
  }

  @Override
  public final List<String> aliases() {
    return Collections.singletonList("GLU");
  }

  @Override
  public final List<TorsionAngleType> torsionAngleTypes() {
    return Stream.of(
            AminoAcidTorsionAngle.PHI.angleTypes().get(0),
            AminoAcidTorsionAngle.PSI.angleTypes().get(0),
            AminoAcidTorsionAngle.OMEGA.angleTypes().get(0),
            AminoAcidTorsionAngle.CALPHA.angleTypes().get(0),
            Chi1.getInstance(Chi1.GLUTAMIC_ACID_ATOMS),
            Chi2.getInstance(Chi2.GLUTAMIC_ACID_ATOMS),
            Chi3.getInstance(Chi3.GLUTAMIC_ACID_ATOMS))
        .collect(Collectors.toList());
  }

  @Override
  public final Set<AtomName> requiredAtoms() {
    return Stream.of(
            AtomName.CB,
            AtomName.HB1,
            AtomName.HB2,
            AtomName.CG,
            AtomName.HG1,
            AtomName.HG2,
            AtomName.CD,
            AtomName.OE1,
            AtomName.OE2)
        .collect(Collectors.toSet());
  }
}
