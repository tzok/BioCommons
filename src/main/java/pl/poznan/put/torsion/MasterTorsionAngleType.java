package pl.poznan.put.torsion;

import java.util.Collection;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.interfaces.DisplayableExportable;
import pl.poznan.put.rna.torsion.Chi;
import pl.poznan.put.rna.torsion.RNATorsionAngleType;
import pl.poznan.put.torsion.range.Range;

/**
 * This is to gather under one interface every "master" torsion angle type. A {@link
 * RNATorsionAngleType#CHI} is a master torsion angle type, and {@link Chi#getPurineInstance()} is a
 * non-master instance.
 *
 * @author tzok
 */
public interface MasterTorsionAngleType extends DisplayableExportable {
  Collection<TorsionAngleType> getAngleTypes();

  Range getRange(Angle angle);
}
