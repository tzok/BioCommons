package pl.poznan.put.pdb.analysis;

import pl.poznan.put.torsion.TorsionAngleType;

import java.util.Collections;
import java.util.List;

public class InvalidResidueInformationProvider
        implements ResidueInformationProvider {
    private final MoleculeType moleculeType;
    private final String pdbName;

    public InvalidResidueInformationProvider(String pdbName) {
        super();
        this.moleculeType = MoleculeType.UNKNOWN;
        this.pdbName = pdbName;
    }

    @Override
    public MoleculeType getMoleculeType() {
        return moleculeType;
    }

    @Override
    public List<ResidueComponent> getAllMoleculeComponents() {
        return Collections.emptyList();
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public char getOneLetterName() {
        assert pdbName != null && pdbName.length() > 0;
        return pdbName.charAt(pdbName.length() - 1);
    }

    @Override
    public String getDefaultPdbName() {
        return pdbName;
    }

    @Override
    public List<String> getPdbNames() {
        return Collections.singletonList(pdbName);
    }

    @Override
    public List<TorsionAngleType> getTorsionAngleTypes() {
        return Collections.emptyList();
    }
}
