package pl.poznan.put.pdb.analysis;

import java.util.List;

import pl.poznan.put.torsion.TorsionAngleType;

public interface ResidueInformationProvider {
    MoleculeType getMoleculeType();

    List<ResidueComponent> getAllMoleculeComponents();

    String getDescription();

    char getOneLetterName();

    String getDefaultPdbName();

    List<String> getPdbNames();

    List<TorsionAngleType> getTorsionAngleTypes();
}
