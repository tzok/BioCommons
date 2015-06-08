package pl.poznan.put.rna.base;

import java.util.List;

import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.ResidueComponent;
import pl.poznan.put.pdb.analysis.ResidueInformationProvider;
import pl.poznan.put.rna.Base;
import pl.poznan.put.torsion.TorsionAngleType;

public enum NucleobaseType implements ResidueInformationProvider {
    ADENINE(Adenine.getInstance()),
    CYTOSINE(Cytosine.getInstance()),
    GUANINE(Guanine.getInstance()),
    URACIL(Uracil.getInstance()),
    THYMINE(Thymine.getInstance()),
    UNKNOWN(Base.invalidInstance());

    private final Base base;

    private NucleobaseType(Base base) {
        this.base = base;
    }

    public Base getBaseInstance() {
        return base;
    }

    public static NucleobaseType fromOneLetterName(char oneLetterName) {
        for (NucleobaseType candidate : NucleobaseType.values()) {
            if (Character.toLowerCase(oneLetterName) == Character.toLowerCase(candidate.base.getOneLetterName())) {
                return candidate;
            }
        }
        return NucleobaseType.UNKNOWN;
    }

    @Override
    public MoleculeType getMoleculeType() {
        return base.getMoleculeType();
    }

    @Override
    public List<ResidueComponent> getAllMoleculeComponents() {
        return base.getAllMoleculeComponents();
    }

    @Override
    public String getDescription() {
        return base.getDescription();
    }

    @Override
    public char getOneLetterName() {
        return base.getOneLetterName();
    }

    @Override
    public String getDefaultPdbName() {
        return base.getDefaultPdbName();
    }

    @Override
    public List<String> getPdbNames() {
        return base.getPdbNames();
    }

    @Override
    public List<TorsionAngleType> getTorsionAngleTypes() {
        return base.getTorsionAngleTypes();
    }
}
