package pl.poznan.put.pdb.analysis;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.immutables.value.Value;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.ImmutableAngle;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.torsion.MasterTorsionAngleType;
import pl.poznan.put.torsion.TorsionAngleType;
import pl.poznan.put.torsion.TorsionAngleValue;

/** A collection of torsion angles values for a single residue. */
@Value.Immutable
@JsonSerialize(as = ImmutableResidueTorsionAngles.class)
@JsonDeserialize(as = ImmutableResidueTorsionAngles.class)
public abstract class ResidueTorsionAngles {
  /**
   * Calculates all torsion angles' values in the given residue.
   *
   * @param residues The list of all residues.
   * @param index The index of residue in the list.
   * @return An instance of this class with all torsion angles' values calculated.
   */
  public static ResidueTorsionAngles calculate(final List<PdbResidue> residues, final int index) {
    final PdbResidue residue = residues.get(index);
    return ImmutableResidueTorsionAngles.of(
        residue.identifier(),
        residue.residueInformationProvider().torsionAngleTypes().stream()
            .map(type -> type.calculate(residues, index))
            .collect(Collectors.toList()));
  }

  /**
   * @return The residue identifier.
   */
  @Value.Parameter(order = 1)
  public abstract PdbResidueIdentifier identifier();

  /**
   * @return The list of torsion angles' values.
   */
  @Value.Parameter(order = 2)
  protected abstract List<TorsionAngleValue> values();

  /**
   * Finds the value of specific torsion angle type in this collection.
   *
   * @param type The type of torsion angle to look for.
   * @return The value of torsion angle in this residue or NaN if none found.
   */
  public final Angle value(final TorsionAngleType type) {
    return values().stream()
        .filter(angle -> Objects.equals(angle.angleType(), type))
        .map(TorsionAngleValue::value)
        .findFirst()
        .orElse(ImmutableAngle.of(Double.NaN));
  }

  /**
   * Finds the value of a master torsion angle type in this collection.
   *
   * @param masterType The master torsion angle tyoe to look for.
   * @return The value of torsion angle in this residue or NaN if none found.
   */
  public final Angle value(final MasterTorsionAngleType masterType) {
    return values().stream()
        .flatMap(angleValue -> masterType.angleTypes().stream().map(this::value))
        .filter(Angle::isValid)
        .findFirst()
        .orElse(ImmutableAngle.of(Double.NaN));
  }
}
