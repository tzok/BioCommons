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
@JsonSerialize(as = ImmutableTryptophan.class)
@JsonDeserialize(as = ImmutableTryptophan.class)
abstract class Tryptophan implements Sidechain {
  @Override
  public final List<ResidueComponent> moleculeComponents() {
    return Stream.of(ImmutableBackbone.of(), ImmutableTryptophan.of()).collect(Collectors.toList());
  }

  @Override
  public final char oneLetterName() {
    return 'W';
  }

  @Override
  public final List<String> aliases() {
    return Collections.singletonList("TRP");
  }

  @Override
  public final List<TorsionAngleType> torsionAngleTypes() {
    return Stream.of(
            AminoAcidTorsionAngle.PHI.angleTypes().get(0),
            AminoAcidTorsionAngle.PSI.angleTypes().get(0),
            AminoAcidTorsionAngle.OMEGA.angleTypes().get(0),
            AminoAcidTorsionAngle.CALPHA.angleTypes().get(0),
            Chi1.getInstance(Chi1.TRYPTOPHAN_ATOMS),
            Chi2.getInstance(Chi2.TRYPTOPHAN_ATOMS))
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
            AtomName.NE1,
            AtomName.HE1,
            AtomName.CE2,
            AtomName.CD2,
            AtomName.CE3,
            AtomName.HE3,
            AtomName.CZ3,
            AtomName.HZ3,
            AtomName.CZ2,
            AtomName.HZ2,
            AtomName.CH2,
            AtomName.HH2)
        .collect(Collectors.toSet());
  }
}
