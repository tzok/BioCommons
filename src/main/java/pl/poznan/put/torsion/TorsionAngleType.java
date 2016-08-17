package pl.poznan.put.torsion;

import pl.poznan.put.interfaces.DisplayableExportable;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbResidue;

import java.util.List;

public abstract class TorsionAngleType implements DisplayableExportable {
    private static final TorsionAngleType INVALID_INSTANCE =
            new TorsionAngleType(MoleculeType.UNKNOWN) {
                @Override
                public String getLongDisplayName() {
                    return "Invalid";
                }

                @Override
                public String getShortDisplayName() {
                    return "Invalid";
                }

                @Override
                public String getExportName() {
                    return "Invalid";
                }

                @Override
                public TorsionAngleValue calculate(List<PdbResidue> residues,
                                                   int currentIndex) {
                    return TorsionAngleValue.invalidInstance(this);
                }
            };
    private final MoleculeType moleculeType;

    protected TorsionAngleType(MoleculeType moleculeType) {
        super();
        this.moleculeType = moleculeType;
    }

    public static TorsionAngleType invalidInstance() {
        return TorsionAngleType.INVALID_INSTANCE;
    }

    public MoleculeType getMoleculeType() {
        return moleculeType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (moleculeType == null ? 0 : moleculeType
                .hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TorsionAngleType other = (TorsionAngleType) obj;
        return moleculeType == other.moleculeType;
    }

    public abstract TorsionAngleValue calculate(List<PdbResidue> residues,
                                                int currentIndex);
}