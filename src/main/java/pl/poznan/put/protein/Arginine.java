package pl.poznan.put.protein;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.analysis.ResidueComponent;
import pl.poznan.put.torsion.TorsionAngleType;

@Value.Immutable(singleton = true)
@JsonSerialize(as = ImmutableArginine.class)
@JsonDeserialize(as = ImmutableArginine.class)
abstract class Arginine implements Sidechain {
  @Override
  public final List<ResidueComponent> moleculeComponents() {
    return Stream.of(ImmutableBackbone.of(), ImmutableArginine.of()).collect(Collectors.toList());
  }

  @Override
  public final char oneLetterName() {
    return 'R';
  }

  @Override
  public final List<String> aliases() {
    return Collections.singletonList("ARG");
  }

  @Override
  public final List<TorsionAngleType> torsionAngleTypes() {
    return Stream.of(
            AminoAcidTorsionAngle.PHI.angleTypes().get(0),
            AminoAcidTorsionAngle.PSI.angleTypes().get(0),
            AminoAcidTorsionAngle.OMEGA.angleTypes().get(0),
            AminoAcidTorsionAngle.CALPHA.angleTypes().get(0),
            Chi1.getInstance(Chi1.ARGININE_ATOMS),
            Chi2.getInstance(Chi2.ARGININE_ATOMS),
            Chi3.getInstance(Chi3.ARGININE_ATOMS),
            Chi4.getInstance(Chi4.ARGININE_ATOMS),
            Chi5.getInstance(Chi5.ARGININE_ATOMS))
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
            AtomName.HD1,
            AtomName.HD2,
            AtomName.NE,
            AtomName.HE,
            AtomName.CZ,
            AtomName.NH1,
            AtomName.HH11,
            AtomName.HH12,
            AtomName.NH2,
            AtomName.HH21,
            AtomName.HH22)
        .collect(Collectors.toSet());
  }
}
