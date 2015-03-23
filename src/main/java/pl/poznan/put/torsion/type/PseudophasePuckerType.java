package pl.poznan.put.torsion.type;

import pl.poznan.put.common.MoleculeType;

public class PseudophasePuckerType extends TorsionAngleType {
    private static final PseudophasePuckerType INSTANCE = new PseudophasePuckerType();

    public static PseudophasePuckerType getInstance() {
        return PseudophasePuckerType.INSTANCE;
    }

    private PseudophasePuckerType() {
        super(MoleculeType.RNA);
    }

    @Override
    public String getLongDisplayName() {
        return "P";
    }

    @Override
    public String getShortDisplayName() {
        return "P";
    }

    @Override
    public String getExportName() {
        return "P";
    }
}
