package pl.poznan.put.rna;

import java.util.Arrays;
import java.util.List;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.common.ResidueComponent;
import pl.poznan.put.common.ResidueInformationSupplier;
import pl.poznan.put.types.Quadruplet;

public abstract class Base extends RNAResidueComponent implements ResidueInformationSupplier {
    private final String longName;
    private final char oneLetterName;
    private final List<String> pdbNames;

    protected Base(List<AtomName> atoms, String longName, char oneLetterName, String... pdbNames) {
        super(RNAResidueComponentType.BASE, atoms);
        this.longName = longName;
        this.oneLetterName = oneLetterName;
        this.pdbNames = Arrays.asList(pdbNames);
    }

    @Override
    public List<ResidueComponent> getAllMoleculeComponents() {
        return Arrays.asList(new ResidueComponent[] { Phosphate.getInstance(), Sugar.getInstance(), this });
    }

    @Override
    public String getDescription() {
        return longName;
    }

    @Override
    public char getOneLetterName() {
        return oneLetterName;
    }

    @Override
    public String getDefaultPdbName() {
        assert pdbNames.size() > 0;
        return pdbNames.get(0);
    }

    @Override
    public List<String> getPdbNames() {
        return pdbNames;
    }

    public abstract Quadruplet<AtomName> getChiAtoms();
}
