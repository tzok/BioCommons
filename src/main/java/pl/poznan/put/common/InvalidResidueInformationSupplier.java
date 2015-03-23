package pl.poznan.put.common;

import java.util.Collections;
import java.util.List;

import pl.poznan.put.atom.AtomName;

public class InvalidResidueInformationSupplier implements ResidueInformationProvider {
    private final String pdbName;

    public InvalidResidueInformationSupplier(String pdbName) {
        super();
        this.pdbName = pdbName;
    }

    @Override
    public List<AtomName> getAtoms() {
        return Collections.emptyList();
    }

    @Override
    public MoleculeType getMoleculeType() {
        return MoleculeType.UNKNOWN;
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
}
