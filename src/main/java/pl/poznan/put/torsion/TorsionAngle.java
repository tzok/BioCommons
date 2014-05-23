package pl.poznan.put.torsion;

import pl.poznan.put.common.DisplayableExportable;
import pl.poznan.put.common.MoleculeType;

public interface TorsionAngle extends DisplayableExportable {
    MoleculeType getMoleculeType();
}