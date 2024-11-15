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
@JsonSerialize(as = ImmutablePhenylalanine.class)
@JsonDeserialize(as = ImmutablePhenylalanine.class)
abstract class Phenylalanine implements Sidechain {
  @Override
  public final List<ResidueComponent> moleculeComponents() {
    return Stream.of(ImmutableBackbone.of(), ImmutablePhenylalanine.of())
        .collect(Collectors.toList());
  }

  @Override
  public final char oneLetterName() {
    return 'F';
  }

  @Override
  public final List<String> aliases() {
    return Collections.singletonList("PHE");
  }

  @Override
  public final List<TorsionAngleType> torsionAngleTypes() {
    return Stream.of(
            AminoAcidTorsionAngle.PHI.angleTypes().get(0),
            AminoAcidTorsionAngle.PSI.angleTypes().get(0),
            AminoAcidTorsionAngle.OMEGA.angleTypes().get(0),
            AminoAcidTorsionAngle.CALPHA.angleTypes().get(0),
            Chi1.getInstance(Chi1.PHENYLALANINE_ATOMS),
            Chi2.getInstance(Chi2.PHENYLALANINE_ATOMS))
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
            AtomName.HZ,
            AtomName.CD2,
            AtomName.HD2,
            AtomName.CE2,
            AtomName.HE2)
        .collect(Collectors.toSet());
  }
}
