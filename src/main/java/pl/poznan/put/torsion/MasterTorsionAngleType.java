package pl.poznan.put.torsion;

import pl.poznan.put.circular.Angle;
import pl.poznan.put.interfaces.DisplayableExportable;
import pl.poznan.put.rna.NucleotideTorsionAngle;
import pl.poznan.put.torsion.range.Range;

import java.util.List;

/**
 * This is to gather under one interface every "master" torsion angle type. A {@link
 * NucleotideTorsionAngle#CHI} is a master torsion angle type.
 *
 * @author tzok
 */
public interface MasterTorsionAngleType extends DisplayableExportable {
  List<TorsionAngleType> angleTypes();

  Range range(Angle angle);
}
