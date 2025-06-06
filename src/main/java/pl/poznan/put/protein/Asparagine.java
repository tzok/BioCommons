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
@JsonSerialize(as = ImmutableAsparagine.class)
@JsonDeserialize(as = ImmutableAsparagine.class)
abstract class Asparagine implements Sidechain {
  @Override
  public final List<ResidueComponent> moleculeComponents() {
    return Stream.of(ImmutableBackbone.of(), ImmutableAsparagine.of()).collect(Collectors.toList());
  }

  @Override
  public final char oneLetterName() {
    return 'N';
  }

  @Override
  public final List<String> aliases() {
    return Collections.singletonList("ASN");
  }

  @Override
  public final List<TorsionAngleType> torsionAngleTypes() {
    return Stream.of(
            AminoAcidTorsionAngle.PHI.angleTypes().get(0),
            AminoAcidTorsionAngle.PSI.angleTypes().get(0),
            AminoAcidTorsionAngle.OMEGA.angleTypes().get(0),
            AminoAcidTorsionAngle.CALPHA.angleTypes().get(0),
            Chi1.getInstance(Chi1.ASPARAGINE_ATOMS),
            Chi2.getInstance(Chi2.ASPARAGINE_ATOMS))
        .collect(Collectors.toList());
  }

  @Override
  public final Set<AtomName> requiredAtoms() {
    return Stream.of(
            AtomName.CB,
            AtomName.HB1,
            AtomName.HB2,
            AtomName.CG,
            AtomName.OD1,
            AtomName.ND2,
            AtomName.HD21,
            AtomName.HD22)
        .collect(Collectors.toSet());
  }
}
