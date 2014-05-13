package pl.poznan.put.protein;

import java.util.HashMap;
import java.util.Map;

import pl.poznan.put.atoms.AtomName;
import pl.poznan.put.common.ChiTorsionAngleChooser;
import pl.poznan.put.common.ResidueType;
import pl.poznan.put.common.TorsionAngle;
import pl.poznan.put.common.TorsionAngleImpl;
import pl.poznan.put.helper.Constants;

public class ProteinChiChooser implements ChiTorsionAngleChooser {
    private static final Map<ResidueType, TorsionAngle[]> MAP = new HashMap<ResidueType, TorsionAngle[]>();

    // @formatter:off
    static {
        TorsionAngle chi1, chi2, chi3, chi4, chi5;

        chi1 = new TorsionAngleImpl(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG, 0, 0, 0, 0, "chi1", Constants.UNICODE_CHI1);
        chi2 = new TorsionAngleImpl(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD, 0, 0, 0, 0, "chi2", Constants.UNICODE_CHI2);
        chi3 = new TorsionAngleImpl(AtomName.CB, AtomName.CG, AtomName.CD, AtomName.NE, 0, 0, 0, 0, "chi3", Constants.UNICODE_CHI3);
        chi4 = new TorsionAngleImpl(AtomName.CG, AtomName.CD, AtomName.NE, AtomName.CZ, 0, 0, 0, 0, "chi4", Constants.UNICODE_CHI4);
        chi5 = new TorsionAngleImpl(AtomName.CD, AtomName.NE, AtomName.CZ, AtomName.NH1, 0, 0, 0, 0, "chi5", Constants.UNICODE_CHI5);
        ProteinChiChooser.MAP.put(ResidueType.ARGININE, new TorsionAngle[] { chi1, chi2, chi3, chi4, chi5 });

        chi1 = new TorsionAngleImpl(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG, 0, 0, 0, 0, "chi1", Constants.UNICODE_CHI1);
        chi2 = new TorsionAngleImpl(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.OD1, 0, 0, 0, 0, "chi2", Constants.UNICODE_CHI2);
        ProteinChiChooser.MAP.put(ResidueType.ASPARAGINE, new TorsionAngle[] { chi1, chi2 });

        chi1 = new TorsionAngleImpl(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG, 0, 0, 0, 0, "chi1", Constants.UNICODE_CHI1);
        chi2 = new TorsionAngleImpl(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.OD1, 0, 0, 0, 0, "chi2", Constants.UNICODE_CHI2);
        ProteinChiChooser.MAP.put(ResidueType.ASPARTIC_ACID, new TorsionAngle[] { chi1, chi2 });

        chi1 = new TorsionAngleImpl(AtomName.N, AtomName.CA, AtomName.CB, AtomName.SG, 0, 0, 0, 0, "chi1", Constants.UNICODE_CHI1);
        ProteinChiChooser.MAP.put(ResidueType.CYSTEINE, new TorsionAngle[] { chi1 });

        chi1 = new TorsionAngleImpl(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG, 0, 0, 0, 0, "chi1", Constants.UNICODE_CHI1);
        chi2 = new TorsionAngleImpl(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD, 0, 0, 0, 0, "chi2", Constants.UNICODE_CHI2);
        chi3 = new TorsionAngleImpl(AtomName.CB, AtomName.CG, AtomName.CD, AtomName.OE1, 0, 0, 0, 0, "chi3", Constants.UNICODE_CHI3);
        ProteinChiChooser.MAP.put(ResidueType.GLUTAMIC_ACID, new TorsionAngle[] { chi1, chi2, chi3 });

        chi1 = new TorsionAngleImpl(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG, 0, 0, 0, 0, "chi1", Constants.UNICODE_CHI1);
        chi2 = new TorsionAngleImpl(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD, 0, 0, 0, 0, "chi2", Constants.UNICODE_CHI2);
        chi3 = new TorsionAngleImpl(AtomName.CB, AtomName.CG, AtomName.CD, AtomName.OE1, 0, 0, 0, 0, "chi3", Constants.UNICODE_CHI3);
        ProteinChiChooser.MAP.put(ResidueType.GLUTAMINE, new TorsionAngle[] { chi1, chi2, chi3 });

        chi1 = new TorsionAngleImpl(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG, 0, 0, 0, 0, "chi1", Constants.UNICODE_CHI1);
        chi2 = new TorsionAngleImpl(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.ND1, 0, 0, 0, 0, "chi2", Constants.UNICODE_CHI2);
        ProteinChiChooser.MAP.put(ResidueType.HISTIDINE, new TorsionAngle[] { chi1, chi2 });

        chi1 = new TorsionAngleImpl(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG1, 0, 0, 0, 0, "chi1", Constants.UNICODE_CHI1);
        chi2 = new TorsionAngleImpl(AtomName.CA, AtomName.CB, AtomName.CG1, AtomName.CD1, 0, 0, 0, 0, "chi2", Constants.UNICODE_CHI2);
        ProteinChiChooser.MAP.put(ResidueType.ISOLEUCINE, new TorsionAngle[] { chi1, chi2 });

        chi1 = new TorsionAngleImpl(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG, 0, 0, 0, 0, "chi1", Constants.UNICODE_CHI1);
        chi2 = new TorsionAngleImpl(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD1, 0, 0, 0, 0, "chi2", Constants.UNICODE_CHI2);
        ProteinChiChooser.MAP.put(ResidueType.LEUCINE, new TorsionAngle[] { chi1, chi2 });

        chi1 = new TorsionAngleImpl(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG, 0, 0, 0, 0, "chi1", Constants.UNICODE_CHI1);
        chi2 = new TorsionAngleImpl(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD, 0, 0, 0, 0, "chi2", Constants.UNICODE_CHI2);
        chi3 = new TorsionAngleImpl(AtomName.CB, AtomName.CG, AtomName.CD, AtomName.CE, 0, 0, 0, 0, "chi3", Constants.UNICODE_CHI3);
        chi4 = new TorsionAngleImpl(AtomName.CG, AtomName.CD, AtomName.CE, AtomName.NZ, 0, 0, 0, 0, "chi4", Constants.UNICODE_CHI4);
        ProteinChiChooser.MAP.put(ResidueType.LYSINE, new TorsionAngle[] { chi1, chi2, chi3, chi4 });

        chi1 = new TorsionAngleImpl(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG, 0, 0, 0, 0, "chi1", Constants.UNICODE_CHI1);
        chi2 = new TorsionAngleImpl(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.SD, 0, 0, 0, 0, "chi2", Constants.UNICODE_CHI2);
        chi3 = new TorsionAngleImpl(AtomName.CB, AtomName.CG, AtomName.SD, AtomName.CE, 0, 0, 0, 0, "chi3", Constants.UNICODE_CHI3);
        ProteinChiChooser.MAP.put(ResidueType.METHIONINE, new TorsionAngle[] { chi1, chi2, chi3 });

        chi1 = new TorsionAngleImpl(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG, 0, 0, 0, 0, "chi1", Constants.UNICODE_CHI1);
        chi2 = new TorsionAngleImpl(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD1, 0, 0, 0, 0, "chi2", Constants.UNICODE_CHI2);
        ProteinChiChooser.MAP.put(ResidueType.PHENYLALANINE, new TorsionAngle[] { chi1, chi2 });

        chi1 = new TorsionAngleImpl(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG, 0, 0, 0, 0, "chi1", Constants.UNICODE_CHI1);
        chi2 = new TorsionAngleImpl(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD, 0, 0, 0, 0, "chi2", Constants.UNICODE_CHI2);
        ProteinChiChooser.MAP.put(ResidueType.PROLINE, new TorsionAngle[] { chi1, chi2 });

        chi1 = new TorsionAngleImpl(AtomName.N, AtomName.CA, AtomName.CB, AtomName.OG, 0, 0, 0, 0, "chi1", Constants.UNICODE_CHI1);
        ProteinChiChooser.MAP.put(ResidueType.SERINE, new TorsionAngle[] { chi1 });

        chi1 = new TorsionAngleImpl(AtomName.N, AtomName.CA, AtomName.CB, AtomName.OG1, 0, 0, 0, 0, "chi1", Constants.UNICODE_CHI1);
        ProteinChiChooser.MAP.put(ResidueType.THREONINE, new TorsionAngle[] { chi1 });

        chi1 = new TorsionAngleImpl(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG, 0, 0, 0, 0, "chi1", Constants.UNICODE_CHI1);
        chi2 = new TorsionAngleImpl(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD, 0, 0, 0, 0, "chi2", Constants.UNICODE_CHI2);
        ProteinChiChooser.MAP.put(ResidueType.PROLINE, new TorsionAngle[] { chi1, chi2 });

        chi1 = new TorsionAngleImpl(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG, 0, 0, 0, 0, "chi1", Constants.UNICODE_CHI1);
        chi2 = new TorsionAngleImpl(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD1, 0, 0, 0, 0, "chi2", Constants.UNICODE_CHI2);
        ProteinChiChooser.MAP.put(ResidueType.TRYPTOPHAN, new TorsionAngle[] { chi1, chi2 });

        chi1 = new TorsionAngleImpl(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG1, 0, 0, 0, 0, "chi1", Constants.UNICODE_CHI1);
        ProteinChiChooser.MAP.put(ResidueType.VALINE, new TorsionAngle[] { chi1 });
    }
    // @formatter:on

    @Override
    public TorsionAngle[] getChiAngles(ResidueType residueType) {
        if (ProteinChiChooser.MAP.containsKey(residueType)) {
            return ProteinChiChooser.MAP.get(residueType);
        }

        return null;
    }
}
