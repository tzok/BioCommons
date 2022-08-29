package pl.poznan.put.torsion;

import java.util.List;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.interfaces.DisplayableExportable;
import pl.poznan.put.rna.NucleotideTorsionAngle;
import pl.poznan.put.torsion.range.Range;

/**
 * The main torsion angle type, which may consist of one or more basic angle types. For example,
 * {@link NucleotideTorsionAngle#CHI} is a master torsion angle type consisting of {@link
 * pl.poznan.put.rna.Chi#PURINE} amd {@link pl.poznan.put.rna.Chi#PYRIMIDINE}.
 */
public interface MasterTorsionAngleType extends DisplayableExportable {
  /**
   * @return The list of basic angle types this type consists of.
   */
  List<TorsionAngleType> angleTypes();

  /**
   * Finds a matching range for the given angle value.
   *
   * @param angle The angle value to find the range for.
   * @return An instance of {@link Range} which incorporates this angle.
   */
  Range range(Angle angle);
}
