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
abstract class Glycine implements Sidechain {
  @Override
  public final List<ResidueComponent> moleculeComponents() {
    return Stream.of(ImmutableBackbone.of(), ImmutableGlycine.of())
        .collect(Collectors.toList());
  }

  @Override
  public final char oneLetterName() {
    return 'G';
  }

  @Override
  public final List<String> aliases() {
    return Collections.singletonList("GLY");
  }

  @Override
  public final List<TorsionAngleType> torsionAngleTypes() {
    return Stream.of(
            AminoAcidTorsionAngle.PHI.angleTypes().get(0),
            AminoAcidTorsionAngle.PSI.angleTypes().get(0),
            AminoAcidTorsionAngle.OMEGA.angleTypes().get(0),
            AminoAcidTorsionAngle.CALPHA.angleTypes().get(0))
        .collect(Collectors.toList());
  }

  @Override
  public final Set<AtomName> requiredAtoms() {
    return Stream.of(AtomName.HA1, AtomName.HA2).collect(Collectors.toSet());
  }
}
