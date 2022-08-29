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
abstract class Valine implements Sidechain {
  @Override
  public final List<ResidueComponent> moleculeComponents() {
    return Stream.of(ImmutableBackbone.of(), ImmutableValine.of()).collect(Collectors.toList());
  }

  @Override
  public final char oneLetterName() {
    return 'V';
  }

  @Override
  public final List<String> aliases() {
    return Collections.singletonList("VAL");
  }

  @Override
  public final List<TorsionAngleType> torsionAngleTypes() {
    return Stream.of(
            AminoAcidTorsionAngle.PHI.angleTypes().get(0),
            AminoAcidTorsionAngle.PSI.angleTypes().get(0),
            AminoAcidTorsionAngle.OMEGA.angleTypes().get(0),
            AminoAcidTorsionAngle.CALPHA.angleTypes().get(0),
            Chi1.getInstance(Chi1.VALINE_ATOMS))
        .collect(Collectors.toList());
  }

  @Override
  public final Set<AtomName> requiredAtoms() {
    return Stream.of(
            AtomName.CB,
            AtomName.HB,
            AtomName.CG1,
            AtomName.HG11,
            AtomName.HG12,
            AtomName.HG13,
            AtomName.CG2,
            AtomName.HG21,
            AtomName.HG22,
            AtomName.HG23)
        .collect(Collectors.toSet());
  }
}
