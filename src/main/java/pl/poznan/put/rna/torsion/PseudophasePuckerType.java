package pl.poznan.put.rna.torsion;

import pl.poznan.put.circular.Angle;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.torsion.MasterTorsionAngleType;
import pl.poznan.put.torsion.TorsionAngleType;
import pl.poznan.put.torsion.TorsionAngleValue;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class PseudophasePuckerType extends TorsionAngleType
        implements MasterTorsionAngleType {
    private static final PseudophasePuckerType INSTANCE =
            new PseudophasePuckerType();

    private PseudophasePuckerType() {
        super(MoleculeType.RNA);
    }

    public static PseudophasePuckerType getInstance() {
        return PseudophasePuckerType.INSTANCE;
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

    @Override
    public TorsionAngleValue calculate(
            final List<PdbResidue> residues, final int currentIndex) {
        TorsionAngleValue nu0 =
                Nu0.getInstance().calculate(residues, currentIndex);
        TorsionAngleValue nu1 =
                Nu1.getInstance().calculate(residues, currentIndex);
        TorsionAngleValue nu2 =
                Nu2.getInstance().calculate(residues, currentIndex);
        TorsionAngleValue nu3 =
                Nu3.getInstance().calculate(residues, currentIndex);
        TorsionAngleValue nu4 =
                Nu4.getInstance().calculate(residues, currentIndex);

        if (!nu0.isValid() || !nu1.isValid() || !nu2.isValid() || !nu3.isValid()
            || !nu4.isValid()) {
            return TorsionAngleValue.invalidInstance(this);
        }

        double scale = 2 * (StrictMath.sin(Math.toRadians(36.0)) + StrictMath
                .sin(Math.toRadians(72.0)));
        double y = (nu1.getValue().getRadians() + nu4.getValue().getRadians())
                   - nu0.getValue().getRadians() - nu3.getValue().getRadians();
        double x = nu2.getValue().getRadians() * scale;
        return new TorsionAngleValue(this, new Angle(StrictMath.atan2(y, x)));
    }

    @Override
    public Collection<? extends TorsionAngleType> getAngleTypes() {
        return Collections.singleton(this);
    }
}
