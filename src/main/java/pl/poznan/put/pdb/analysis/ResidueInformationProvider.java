package pl.poznan.put.pdb.analysis;

import pl.poznan.put.torsion.TorsionAngleType;

import java.io.Serializable;
import java.util.List;

public interface ResidueInformationProvider extends Serializable {
    MoleculeType getMoleculeType();

    List<ResidueComponent> getAllMoleculeComponents();

    String getDescription();

    char getOneLetterName();

    String getDefaultPdbName();

    List<String> getPdbNames();

    List<TorsionAngleType> getTorsionAngleTypes();
}
