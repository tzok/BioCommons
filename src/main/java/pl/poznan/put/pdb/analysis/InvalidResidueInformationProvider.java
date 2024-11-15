package pl.poznan.put.pdb.analysis;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Collections;
import java.util.List;
import org.immutables.value.Value;
import pl.poznan.put.torsion.TorsionAngleType;

@Value.Immutable
@JsonSerialize(as = ImmutableInvalidResidueInformationProvider.class)
@JsonDeserialize(as = ImmutableInvalidResidueInformationProvider.class)
abstract class InvalidResidueInformationProvider implements ResidueInformationProvider {
  @Value.Parameter(order = 1)
  public abstract String residueName();

  @Override
  public final MoleculeType moleculeType() {
    return MoleculeType.UNKNOWN;
  }

  @Override
  public final List<ResidueComponent> moleculeComponents() {
    return Collections.emptyList();
  }

  @Override
  public final char oneLetterName() {
    return residueName().charAt(residueName().length() - 1);
  }

  @Override
  public final List<String> aliases() {
    return Collections.singletonList(residueName());
  }

  @Override
  public final List<TorsionAngleType> torsionAngleTypes() {
    return Collections.emptyList();
  }
}
