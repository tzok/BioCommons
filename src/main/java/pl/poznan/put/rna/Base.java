package pl.poznan.put.rna;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.common.ResidueComponent;
import pl.poznan.put.common.ResidueInformationProvider;
import pl.poznan.put.types.Quadruplet;

public abstract class Base extends NucleicAcidResidueComponent implements ResidueInformationProvider {
    private static final Base INVALID = new Base(Collections.<AtomName> emptyList(), "UNK", 'X', "UNK") {
        private final Quadruplet<AtomName> chiAtoms = new Quadruplet<AtomName>(AtomName.UNKNOWN, AtomName.UNKNOWN, AtomName.UNKNOWN, AtomName.UNKNOWN);

        @Override
        public Quadruplet<AtomName> getChiAtoms() {
            return chiAtoms;
        }

        @Override
        public Sugar getDefaultSugarInstance() {
            return Sugar.invalidInstance();
        }
    };

    public static Base invalidInstance() {
        return Base.INVALID;
    }

    private final String longName;
    private final char oneLetterName;
    private final List<String> pdbNames;

    protected Base(List<AtomName> atoms, String longName, char oneLetterName,
            String... pdbNames) {
        super(RNAResidueComponentType.BASE, atoms);
        this.longName = longName;
        this.oneLetterName = oneLetterName;
        this.pdbNames = Arrays.asList(pdbNames);
    }

    @Override
    public List<ResidueComponent> getAllMoleculeComponents() {
        return Arrays.asList(new ResidueComponent[] { Phosphate.getInstance(), getDefaultSugarInstance(), this });
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

    public abstract Sugar getDefaultSugarInstance();
}
