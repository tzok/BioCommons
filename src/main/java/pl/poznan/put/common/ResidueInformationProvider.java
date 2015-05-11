package pl.poznan.put.common;

import java.util.List;

import pl.poznan.put.torsion.type.TorsionAngleType;

public interface ResidueInformationProvider extends AtomContainer {
    MoleculeType getMoleculeType();

    List<ResidueComponent> getAllMoleculeComponents();

    String getDescription();

    char getOneLetterName();

    String getDefaultPdbName();

    List<String> getPdbNames();

    List<TorsionAngleType> getTorsionAngleTypes();
}
