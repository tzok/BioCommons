package pl.poznan.put.torsion;

import java.util.Collection;

import pl.poznan.put.interfaces.DisplayableExportable;

/**
 * This is to gather under one interface every "master" torsion angle type. A
 * CHI is a master torsion angle type, and Chi.getInstance(BaseType.PURINE) is a
 * non-master instance.
 * 
 * @author tzok
 */
public interface MasterTorsionAngleType extends DisplayableExportable {
    Collection<? extends TorsionAngleType> getAngleTypes();
}
