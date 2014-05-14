package pl.poznan.put.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.biojava.bio.structure.Group;

import pl.poznan.put.atoms.AtomName;
import pl.poznan.put.helper.Helper;
import pl.poznan.put.nucleic.RNAChiTorsionAngle;
import pl.poznan.put.nucleic.RNAResidueAtoms;
import pl.poznan.put.protein.ProteinChiTorsionAngle;
import pl.poznan.put.protein.ProteinResidueAtoms;

public enum ResidueType {
    UNKNOWN(MoleculeType.UNKNOWN, null, 'X', "UNK"),
    ADENINE(MoleculeType.RNA, new TorsionAngle[] { RNAChiTorsionAngle.PURINE }, 'A', "ADE", "A"),
    GUANINE(MoleculeType.RNA, new TorsionAngle[] { RNAChiTorsionAngle.PURINE }, 'G', "GUA", "G"),
    CYTOSINE(MoleculeType.RNA, new TorsionAngle[] { RNAChiTorsionAngle.PYRIMIDINE }, 'C', "CYT", "C"),
    URACIL(MoleculeType.RNA, new TorsionAngle[] { RNAChiTorsionAngle.PYRIMIDINE }, 'U', "URA", "URI", "U"),
    THYMINE(MoleculeType.RNA, new TorsionAngle[] { RNAChiTorsionAngle.PYRIMIDINE }, 'T', "THY", "T"),
    ALANINE(MoleculeType.PROTEIN, new TorsionAngle[] {}, 'A', "ALA"),
    ARGININE(MoleculeType.PROTEIN, new TorsionAngle[] { ProteinChiTorsionAngle.ARG_CHI1, ProteinChiTorsionAngle.ARG_CHI2, ProteinChiTorsionAngle.ARG_CHI3, ProteinChiTorsionAngle.ARG_CHI4, ProteinChiTorsionAngle.ARG_CHI5 }, 'R', "ARG"),
    ASPARAGINE(MoleculeType.PROTEIN, new TorsionAngle[] { ProteinChiTorsionAngle.ASN_CHI1, ProteinChiTorsionAngle.ASN_CHI2 }, 'N', "ASN"),
    ASPARTIC_ACID(MoleculeType.PROTEIN, new TorsionAngle[] { ProteinChiTorsionAngle.ASP_CHI1, ProteinChiTorsionAngle.ASP_CHI2 }, 'D', "ASP"),
    CYSTEINE(MoleculeType.PROTEIN, new TorsionAngle[] { ProteinChiTorsionAngle.CYS_CHI1 }, 'C', "CYS"),
    GLUTAMINE(MoleculeType.PROTEIN, new TorsionAngle[] { ProteinChiTorsionAngle.GLN_CHI1, ProteinChiTorsionAngle.GLN_CHI2, ProteinChiTorsionAngle.GLN_CHI3 }, 'Q', "GLN"),
    GLUTAMIC_ACID(MoleculeType.PROTEIN, new TorsionAngle[] { ProteinChiTorsionAngle.GLU_CHI1, ProteinChiTorsionAngle.GLU_CHI2, ProteinChiTorsionAngle.GLU_CHI3 }, 'E', "GLU"),
    GLYCINE(MoleculeType.PROTEIN, new TorsionAngle[] {}, 'G', "GLY"),
    HISTIDINE(MoleculeType.PROTEIN, new TorsionAngle[] { ProteinChiTorsionAngle.HIS_CHI1, ProteinChiTorsionAngle.HIS_CHI2 }, 'H', "HIS", "HSD", "HSE", "HSP"),
    ISOLEUCINE(MoleculeType.PROTEIN, new TorsionAngle[] { ProteinChiTorsionAngle.ILE_CHI1, ProteinChiTorsionAngle.ILE_CHI2 }, 'I', "ILE"),
    LEUCINE(MoleculeType.PROTEIN, new TorsionAngle[] { ProteinChiTorsionAngle.LEU_CHI1, ProteinChiTorsionAngle.LEU_CHI2 }, 'L', "LEU"),
    LYSINE(MoleculeType.PROTEIN, new TorsionAngle[] { ProteinChiTorsionAngle.LYS_CHI1, ProteinChiTorsionAngle.LYS_CHI2, ProteinChiTorsionAngle.LYS_CHI3, ProteinChiTorsionAngle.LYS_CHI4 }, 'K', "LYS"),
    METHIONINE(MoleculeType.PROTEIN, new TorsionAngle[] { ProteinChiTorsionAngle.MET_CHI1, ProteinChiTorsionAngle.MET_CHI2, ProteinChiTorsionAngle.MET_CHI3 }, 'M', "MET"),
    PHENYLALANINE(MoleculeType.PROTEIN, new TorsionAngle[] { ProteinChiTorsionAngle.PHE_CHI1, ProteinChiTorsionAngle.PHE_CHI2 }, 'F', "PHE"),
    PROLINE(MoleculeType.PROTEIN, new TorsionAngle[] { ProteinChiTorsionAngle.PRO_CHI1, ProteinChiTorsionAngle.PRO_CHI2 }, 'P', "PRO"),
    SERINE(MoleculeType.PROTEIN, new TorsionAngle[] { ProteinChiTorsionAngle.SER_CHI1 }, 'S', "SER"),
    THREONINE(MoleculeType.PROTEIN, new TorsionAngle[] { ProteinChiTorsionAngle.THR_CHI1 }, 'T', "THR"),
    TRYPTOPHAN(MoleculeType.PROTEIN, new TorsionAngle[] { ProteinChiTorsionAngle.TRP_CHI1, ProteinChiTorsionAngle.TRP_CHI2 }, 'W', "TRP"),
    TYROSINE(MoleculeType.PROTEIN, new TorsionAngle[] { ProteinChiTorsionAngle.TYR_CHI1, ProteinChiTorsionAngle.TYR_CHI2 }, 'Y', "TYR"),
    VALINE(MoleculeType.PROTEIN, new TorsionAngle[] { ProteinChiTorsionAngle.VAL_CHI1 }, 'V', "VAL");

    private final MoleculeType chainType;
    private final TorsionAngle[] chiAngles;
    private final char oneLetter;
    private final String[] names;

    private ResidueType(MoleculeType chainType, TorsionAngle[] chiAngles,
            char oneLetter, String... names) {
        this.chainType = chainType;
        this.chiAngles = chiAngles;
        this.oneLetter = oneLetter;
        this.names = names;
    }

    public MoleculeType getChainType() {
        return chainType;
    }

    public char getOneLetter() {
        return oneLetter;
    }

    public String[] getNames() {
        return names;
    }

    public List<AtomName> getResidueAtoms() {
        switch (chainType) {
        case PROTEIN:
            return ProteinResidueAtoms.getAtoms(this);
        case RNA:
            return RNAResidueAtoms.getAtoms(this);
        case UNKNOWN:
        default:
            return null;
        }
    }

    public List<TorsionAngle> getTorsionAngles() {
        if (this == UNKNOWN) {
            return null;
        }

        List<TorsionAngle> result = new ArrayList<TorsionAngle>();
        result.addAll(chainType.getBackboneTorsionAngles());
        result.addAll(Arrays.asList(chiAngles));
        return result;
    }

    public static ResidueType fromString(MoleculeType chainType, String pdbName) {
        pdbName = pdbName.trim();

        for (ResidueType type : ResidueType.values()) {
            if (chainType == type.getChainType()) {
                for (String name : type.getNames()) {
                    if (name.compareToIgnoreCase(pdbName) == 0) {
                        return type;
                    }
                }
            }
        }

        return ResidueType.UNKNOWN;
    }

    public static ResidueType fromOneLetter(MoleculeType chainType,
            char oneLetter) {
        oneLetter = Character.toUpperCase(oneLetter);

        for (ResidueType type : ResidueType.values()) {
            if (chainType == type.getChainType()
                    && oneLetter == type.getOneLetter()) {
                return type;
            }
        }

        return ResidueType.UNKNOWN;
    }

    public static ResidueType detect(Group residue) {
        int bestScore = 0;
        ResidueType bestType = ResidueType.UNKNOWN;

        for (ResidueType type : ResidueType.values()) {
            int score = 0;
            List<AtomName> listing = type.getResidueAtoms();

            if (listing != null) {
                for (AtomName atomType : listing) {
                    if (Helper.findAtom(residue, atomType) != null) {
                        score++;
                    }
                }

                if (score > bestScore) {
                    bestScore = score;
                    bestType = type;
                }
            }
        }

        return bestType;
    }
}
