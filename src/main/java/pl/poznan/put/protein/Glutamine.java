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
@JsonSerialize(as = ImmutableGlutamine.class)
@JsonDeserialize(as = ImmutableGlutamine.class)
abstract class Glutamine implements Sidechain {
  @Override
  public final List<ResidueComponent> moleculeComponents() {
    return Stream.of(ImmutableBackbone.of(), ImmutableGlutamine.of()).collect(Collectors.toList());
  }

  @Override
  public final char oneLetterName() {
    return 'Q';
  }

  @Override
  public final List<String> aliases() {
    return Collections.singletonList("GLN");
  }

  @Override
  public final List<TorsionAngleType> torsionAngleTypes() {
    return Stream.of(
            AminoAcidTorsionAngle.PHI.angleTypes().get(0),
            AminoAcidTorsionAngle.PSI.angleTypes().get(0),
            AminoAcidTorsionAngle.OMEGA.angleTypes().get(0),
            AminoAcidTorsionAngle.CALPHA.angleTypes().get(0),
            Chi1.getInstance(Chi1.GLUTAMINE_ATOMS),
            Chi2.getInstance(Chi2.GLUTAMINE_ATOMS),
            Chi3.getInstance(Chi3.GLUTAMINE_ATOMS))
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
            AtomName.NE2,
            AtomName.HE21,
            AtomName.HE22)
        .collect(Collectors.toSet());
  }
}
