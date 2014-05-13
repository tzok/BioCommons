package pl.poznan.put.nucleic;

import pl.poznan.put.atoms.AtomName;
import pl.poznan.put.common.ChiTorsionAngleChooser;
import pl.poznan.put.common.ResidueType;
import pl.poznan.put.common.TorsionAngle;
import pl.poznan.put.common.TorsionAngleImpl;
import pl.poznan.put.helper.Constants;

public class RNAChiChooser implements ChiTorsionAngleChooser {
    private static final TorsionAngle[] CHI_PURINE = new TorsionAngle[] { new TorsionAngleImpl(
            AtomName.O4p, AtomName.C1p, AtomName.N9, AtomName.C4, 0, 0, 0, 0,
            "chi", Constants.UNICODE_CHI) };
    private static final TorsionAngle[] CHI_PYRIMIDINE = new TorsionAngle[] { new TorsionAngleImpl(
            AtomName.O4p, AtomName.C1p, AtomName.N1, AtomName.C2, 0, 0, 0, 0,
            "chi", Constants.UNICODE_CHI) };

    @Override
    public TorsionAngle[] getChiAngles(ResidueType residueType) {
        if (residueType == ResidueType.CYTOSINE
                || residueType == ResidueType.URACIL
                || residueType == ResidueType.THYMINE) {
            return RNAChiChooser.CHI_PYRIMIDINE;
        } else if (residueType == ResidueType.GUANINE
                || residueType == ResidueType.ADENINE) {
            return RNAChiChooser.CHI_PURINE;
        }

        return null;
    }
}
