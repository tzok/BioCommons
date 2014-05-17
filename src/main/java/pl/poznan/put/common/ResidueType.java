package pl.poznan.put.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.biojava.bio.structure.Group;

import pl.poznan.put.atoms.AtomName;
import pl.poznan.put.helper.StructureHelper;
import pl.poznan.put.nucleic.RNAChiTorsionAngle;
import pl.poznan.put.nucleic.RNAResidueAtoms;
import pl.poznan.put.protein.ProteinChiTorsionAngle;
import pl.poznan.put.protein.ProteinResidueAtoms;

public enum ResidueType {
    UNKNOWN(MoleculeType.UNKNOWN, null, 'X', "UNK"),
    ADENINE(MoleculeType.RNA, new AtomsBasedTorsionAngle[] { RNAChiTorsionAngle.PURINE }, 'A', "ADE", "A"),
    GUANINE(MoleculeType.RNA, new AtomsBasedTorsionAngle[] { RNAChiTorsionAngle.PURINE }, 'G', "GUA", "G"),
    CYTOSINE(MoleculeType.RNA, new AtomsBasedTorsionAngle[] { RNAChiTorsionAngle.PYRIMIDINE }, 'C', "CYT", "C"),
    URACIL(MoleculeType.RNA, new AtomsBasedTorsionAngle[] { RNAChiTorsionAngle.PYRIMIDINE }, 'U', "URA", "URI", "U"),
    THYMINE(MoleculeType.RNA, new AtomsBasedTorsionAngle[] { RNAChiTorsionAngle.PYRIMIDINE }, 'T', "THY", "T"),
    ALANINE(MoleculeType.PROTEIN, new AtomsBasedTorsionAngle[] {}, 'A', "ALA"),
    ARGININE(MoleculeType.PROTEIN, new AtomsBasedTorsionAngle[] { ProteinChiTorsionAngle.ARG_CHI1, ProteinChiTorsionAngle.ARG_CHI2, ProteinChiTorsionAngle.ARG_CHI3, ProteinChiTorsionAngle.ARG_CHI4, ProteinChiTorsionAngle.ARG_CHI5 }, 'R', "ARG"),
    ASPARAGINE(MoleculeType.PROTEIN, new AtomsBasedTorsionAngle[] { ProteinChiTorsionAngle.ASN_CHI1, ProteinChiTorsionAngle.ASN_CHI2 }, 'N', "ASN"),
    ASPARTIC_ACID(MoleculeType.PROTEIN, new AtomsBasedTorsionAngle[] { ProteinChiTorsionAngle.ASP_CHI1, ProteinChiTorsionAngle.ASP_CHI2 }, 'D', "ASP"),
    CYSTEINE(MoleculeType.PROTEIN, new AtomsBasedTorsionAngle[] { ProteinChiTorsionAngle.CYS_CHI1 }, 'C', "CYS"),
    GLUTAMINE(MoleculeType.PROTEIN, new AtomsBasedTorsionAngle[] { ProteinChiTorsionAngle.GLN_CHI1, ProteinChiTorsionAngle.GLN_CHI2, ProteinChiTorsionAngle.GLN_CHI3 }, 'Q', "GLN"),
    GLUTAMIC_ACID(MoleculeType.PROTEIN, new AtomsBasedTorsionAngle[] { ProteinChiTorsionAngle.GLU_CHI1, ProteinChiTorsionAngle.GLU_CHI2, ProteinChiTorsionAngle.GLU_CHI3 }, 'E', "GLU"),
    GLYCINE(MoleculeType.PROTEIN, new AtomsBasedTorsionAngle[] {}, 'G', "GLY"),
    HISTIDINE(MoleculeType.PROTEIN, new AtomsBasedTorsionAngle[] { ProteinChiTorsionAngle.HIS_CHI1, ProteinChiTorsionAngle.HIS_CHI2 }, 'H', "HIS", "HSD", "HSE", "HSP"),
    ISOLEUCINE(MoleculeType.PROTEIN, new AtomsBasedTorsionAngle[] { ProteinChiTorsionAngle.ILE_CHI1, ProteinChiTorsionAngle.ILE_CHI2 }, 'I', "ILE"),
    LEUCINE(MoleculeType.PROTEIN, new AtomsBasedTorsionAngle[] { ProteinChiTorsionAngle.LEU_CHI1, ProteinChiTorsionAngle.LEU_CHI2 }, 'L', "LEU"),
    LYSINE(MoleculeType.PROTEIN, new AtomsBasedTorsionAngle[] { ProteinChiTorsionAngle.LYS_CHI1, ProteinChiTorsionAngle.LYS_CHI2, ProteinChiTorsionAngle.LYS_CHI3, ProteinChiTorsionAngle.LYS_CHI4 }, 'K', "LYS"),
    METHIONINE(MoleculeType.PROTEIN, new AtomsBasedTorsionAngle[] { ProteinChiTorsionAngle.MET_CHI1, ProteinChiTorsionAngle.MET_CHI2, ProteinChiTorsionAngle.MET_CHI3 }, 'M', "MET"),
    PHENYLALANINE(MoleculeType.PROTEIN, new AtomsBasedTorsionAngle[] { ProteinChiTorsionAngle.PHE_CHI1, ProteinChiTorsionAngle.PHE_CHI2 }, 'F', "PHE"),
    PROLINE(MoleculeType.PROTEIN, new AtomsBasedTorsionAngle[] { ProteinChiTorsionAngle.PRO_CHI1, ProteinChiTorsionAngle.PRO_CHI2 }, 'P', "PRO"),
    SERINE(MoleculeType.PROTEIN, new AtomsBasedTorsionAngle[] { ProteinChiTorsionAngle.SER_CHI1 }, 'S', "SER"),
    THREONINE(MoleculeType.PROTEIN, new AtomsBasedTorsionAngle[] { ProteinChiTorsionAngle.THR_CHI1 }, 'T', "THR"),
    TRYPTOPHAN(MoleculeType.PROTEIN, new AtomsBasedTorsionAngle[] { ProteinChiTorsionAngle.TRP_CHI1, ProteinChiTorsionAngle.TRP_CHI2 }, 'W', "TRP"),
    TYROSINE(MoleculeType.PROTEIN, new AtomsBasedTorsionAngle[] { ProteinChiTorsionAngle.TYR_CHI1, ProteinChiTorsionAngle.TYR_CHI2 }, 'Y', "TYR"),
    VALINE(MoleculeType.PROTEIN, new AtomsBasedTorsionAngle[] { ProteinChiTorsionAngle.VAL_CHI1 }, 'V', "VAL");

    private final MoleculeType chainType;
    private final AtomsBasedTorsionAngle[] chiAngles;
    private final char oneLetter;
    private final String[] names;

    private ResidueType(MoleculeType chainType,
            AtomsBasedTorsionAngle[] chiAngles, char oneLetter, String... names) {
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

    public List<AtomsBasedTorsionAngle> getTorsionAngles() {
        if (this == UNKNOWN) {
            return null;
        }

        List<AtomsBasedTorsionAngle> result = new ArrayList<AtomsBasedTorsionAngle>();
        result.addAll(chainType.getBackboneTorsionAngles());
        result.addAll(Arrays.asList(chiAngles));
        return result;
    }

    public List<AtomsBasedTorsionAngle> getChiTorsionAngles() {
        return Arrays.asList(chiAngles);
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
                    if (StructureHelper.findAtom(residue, atomType) != null) {
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
