package pl.poznan.put.rna.torsion;

import org.apache.commons.math3.util.FastMath;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.enums.ValueType;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.rna.torsion.range.Pseudorotation;
import pl.poznan.put.torsion.TorsionAngleType;
import pl.poznan.put.torsion.TorsionAngleValue;

import java.util.List;

public final class PseudophasePuckerType extends TorsionAngleType {
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
    public TorsionAngleValue calculate(final List<PdbResidue> residues,
                                       final int currentIndex) {
        final TorsionAngleValue nu0 =
                Nu0.getInstance().calculate(residues, currentIndex);
        final TorsionAngleValue nu1 =
                Nu1.getInstance().calculate(residues, currentIndex);
        final TorsionAngleValue nu2 =
                Nu2.getInstance().calculate(residues, currentIndex);
        final TorsionAngleValue nu3 =
                Nu3.getInstance().calculate(residues, currentIndex);
        final TorsionAngleValue nu4 =
                Nu4.getInstance().calculate(residues, currentIndex);

        if (!nu0.getValue().isValid() || !nu1.getValue().isValid() ||
            !nu2.getValue().isValid() || !nu3.getValue().isValid() ||
            !nu4.getValue().isValid()) {
            return TorsionAngleValue.invalidInstance(this);
        }

        final double scale = 2 * (FastMath.sin(Math.toRadians(36.0)) +
                                  FastMath.sin(Math.toRadians(72.0)));
        final double y =
                (nu1.getValue().getRadians() + nu4.getValue().getRadians()) -
                nu0.getValue().getRadians() - nu3.getValue().getRadians();
        final double x = nu2.getValue().getRadians() * scale;
        return new TorsionAngleValue(this, new Angle(FastMath.atan2(y, x),
                                                     ValueType.RADIANS));
    }
}
