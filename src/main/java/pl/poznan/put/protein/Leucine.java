package pl.poznan.put.protein;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
@JsonSerialize(as = ImmutableLeucine.class)
@JsonDeserialize(as = ImmutableLeucine.class)
abstract class Leucine implements Sidechain {
  @Override
  public final List<ResidueComponent> moleculeComponents() {
    return Stream.of(ImmutableBackbone.of(), ImmutableLeucine.of()).collect(Collectors.toList());
  }

  @Override
  public final char oneLetterName() {
    return 'L';
  }

  @Override
  public final List<String> aliases() {
    return Collections.singletonList("LEU");
  }

  @Override
  public final List<TorsionAngleType> torsionAngleTypes() {
    return Stream.of(
            AminoAcidTorsionAngle.PHI.angleTypes().get(0),
            AminoAcidTorsionAngle.PSI.angleTypes().get(0),
            AminoAcidTorsionAngle.OMEGA.angleTypes().get(0),
            AminoAcidTorsionAngle.CALPHA.angleTypes().get(0),
            Chi1.getInstance(Chi1.LEUCINE_ATOMS),
            Chi2.getInstance(Chi2.LEUCINE_ATOMS))
        .collect(Collectors.toList());
  }

  @Override
  public final Set<AtomName> requiredAtoms() {
    return Stream.of(
            AtomName.CB,
            AtomName.HB1,
            AtomName.HB2,
            AtomName.CG,
            AtomName.HG,
            AtomName.CD1,
            AtomName.HD11,
            AtomName.HD12,
            AtomName.HD13,
            AtomName.CD2,
            AtomName.HD21,
            AtomName.HD22,
            AtomName.HD23)
        .collect(Collectors.toSet());
  }
}
