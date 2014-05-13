package pl.poznan.put.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.biojava.bio.structure.Group;

import pl.poznan.put.atoms.AtomName;
import pl.poznan.put.helper.Helper;
import pl.poznan.put.nucleic.RNAChiChooser;
import pl.poznan.put.nucleic.RNAResidueAtoms;
import pl.poznan.put.protein.ProteinChiChooser;
import pl.poznan.put.protein.ProteinResidueAtoms;

public enum ResidueType {
    UNKNOWN(MoleculeType.UNKNOWN, 'X', "UNK"),
    ADENINE(MoleculeType.RNA, 'A', "ADE", "A"),
    GUANINE(MoleculeType.RNA, 'G', "GUA", "G"),
    CYTOSINE(MoleculeType.RNA, 'C', "CYT", "C"),
    URACIL(MoleculeType.RNA, 'U', "URA", "URI", "U"),
    THYMINE(MoleculeType.RNA, 'T', "THY", "T"),
    ALANINE(MoleculeType.PROTEIN, 'A', "ALA"),
    ARGININE(MoleculeType.PROTEIN, 'R', "ARG"),
    ASPARAGINE(MoleculeType.PROTEIN, 'N', "ASN"),
    ASPARTIC_ACID(MoleculeType.PROTEIN, 'D', "ASP"),
    CYSTEINE(MoleculeType.PROTEIN, 'C', "CYS"),
    GLUTAMINE(MoleculeType.PROTEIN, 'Q', "GLN"),
    GLUTAMIC_ACID(MoleculeType.PROTEIN, 'E', "GLU"),
    GLYCINE(MoleculeType.PROTEIN, 'G', "GLY"),
    HISTIDINE(MoleculeType.PROTEIN, 'H', "HIS", "HSD", "HSE", "HSP"),
    ISOLEUCINE(MoleculeType.PROTEIN, 'I', "ILE"),
    LEUCINE(MoleculeType.PROTEIN, 'L', "LEU"),
    LYSINE(MoleculeType.PROTEIN, 'K', "LYS"),
    METHIONINE(MoleculeType.PROTEIN, 'M', "MET"),
    PHENYLALANINE(MoleculeType.PROTEIN, 'F', "PHE"),
    PROLINE(MoleculeType.PROTEIN, 'P', "PRO"),
    SERINE(MoleculeType.PROTEIN, 'S', "SER"),
    THREONINE(MoleculeType.PROTEIN, 'T', "THR"),
    TRYPTOPHAN(MoleculeType.PROTEIN, 'W', "TRP"),
    TYROSINE(MoleculeType.PROTEIN, 'Y', "TYR"),
    VALINE(MoleculeType.PROTEIN, 'V', "VAL");

    private static final ChiTorsionAngleChooser RNA_CHOOSER = new RNAChiChooser();
    private static final ChiTorsionAngleChooser PROTEIN_CHOOSER = new ProteinChiChooser();

    private final MoleculeType chainType;
    private final char oneLetter;
    private final String[] names;
    private final List<AtomName> residueAtoms;

    private ResidueType(MoleculeType chainType, char oneLetter, String... names) {
        this.chainType = chainType;
        this.oneLetter = oneLetter;
        this.names = names;

        switch (chainType) {
        case PROTEIN:
            residueAtoms = ProteinResidueAtoms.getAtoms(this);
            break;
        case RNA:
            residueAtoms = RNAResidueAtoms.getAtoms(this);
            break;
        case UNKNOWN:
        default:
            residueAtoms = null;
            break;
        }
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
        return residueAtoms;
    }

    public List<TorsionAngle> getTorsionAngles() {
        if (chainType == MoleculeType.UNKNOWN) {
            return null;
        }

        ChiTorsionAngleChooser chooser = chainType == MoleculeType.RNA ? RNA_CHOOSER
                : PROTEIN_CHOOSER;

        List<TorsionAngle> result = new ArrayList<TorsionAngle>();
        result.addAll(chainType.getMainTorsionAngles());
        TorsionAngle[] chiAngles = chooser.getChiAngles(this);
        if (chiAngles != null) {
            result.addAll(Arrays.asList(chiAngles));
        }
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
