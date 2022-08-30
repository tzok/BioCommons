package pl.poznan.put.protein;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.immutables.value.Value;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.analysis.ResidueComponent;
import pl.poznan.put.torsion.TorsionAngleType;

@Value.Immutable(singleton = true)
abstract class Tyrosine implements Sidechain {
  @Override
  public final List<ResidueComponent> moleculeComponents() {
    return Stream.of(ImmutableBackbone.of(), ImmutableTyrosine.of()).collect(Collectors.toList());
  }

  @Override
  public final char oneLetterName() {
    return 'Y';
  }

  @Override
  public final List<String> aliases() {
    return Collections.singletonList("TYR");
  }

  @Override
  public final List<TorsionAngleType> torsionAngleTypes() {
    return Stream.of(
            AminoAcidTorsionAngle.PHI.angleTypes().get(0),
            AminoAcidTorsionAngle.PSI.angleTypes().get(0),
            AminoAcidTorsionAngle.OMEGA.angleTypes().get(0),
            AminoAcidTorsionAngle.CALPHA.angleTypes().get(0),
            Chi1.getInstance(Chi1.TYROSINE_ATOMS),
            Chi2.getInstance(Chi2.TYROSINE_ATOMS))
        .collect(Collectors.toList());
  }

  @Override
  public final Set<AtomName> requiredAtoms() {
    return Stream.of(
            AtomName.CB,
            AtomName.HB1,
            AtomName.HB2,
            AtomName.CG,
            AtomName.CD1,
            AtomName.HD1,
            AtomName.CE1,
            AtomName.HE1,
            AtomName.CZ,
            AtomName.OH,
            AtomName.HH,
            AtomName.CD2,
            AtomName.HD2,
            AtomName.CE2,
            AtomName.HE2)
        .collect(Collectors.toSet());
  }
}
