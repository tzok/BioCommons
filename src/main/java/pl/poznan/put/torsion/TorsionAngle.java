package pl.poznan.put.torsion;

import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.interfaces.DisplayableExportable;

public interface TorsionAngle extends DisplayableExportable {
    MoleculeType getMoleculeType();
}