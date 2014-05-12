package pl.poznan.put.common;

import java.util.List;

import org.biojava.bio.structure.Group;

import pl.poznan.put.atoms.AtomName;
import pl.poznan.put.helper.Helper;
import pl.poznan.put.nucleic.RNAResidueAtoms;
import pl.poznan.put.protein.ProteinResidueAtoms;

public enum ResidueType {
    UNKNOWN(ChainType.UNKNOWN, 'X', "UNK"),
    ADENINE(ChainType.RNA, 'A', "ADE", "A"),
    GUANINE(ChainType.RNA, 'G', "GUA", "G"),
    CYTOSINE(ChainType.RNA, 'C', "CYT", "C"),
    URACIL(ChainType.RNA, 'U', "URA", "URI", "U"),
    THYMINE(ChainType.RNA, 'T', "THY", "T"),
    ALANINE(ChainType.PROTEIN, 'A', "ALA"),
    ARGININE(ChainType.PROTEIN, 'R', "ARG"),
    ASPARAGINE(ChainType.PROTEIN, 'N', "ASN"),
    ASPARTIC_ACID(ChainType.PROTEIN, 'D', "ASP"),
    CYSTEINE(ChainType.PROTEIN, 'C', "CYS"),
    GLUTAMINE(ChainType.PROTEIN, 'Q', "GLN"),
    GLUTAMIC_ACID(ChainType.PROTEIN, 'E', "GLU"),
    GLYCINE(ChainType.PROTEIN, 'G', "GLY"),
    HISTIDINE(ChainType.PROTEIN, 'H', "HIS", "HSD", "HSE", "HSP"),
    ISOLEUCINE(ChainType.PROTEIN, 'I', "ILE"),
    LEUCINE(ChainType.PROTEIN, 'L', "LEU"),
    LYSINE(ChainType.PROTEIN, 'K', "LYS"),
    METHIONINE(ChainType.PROTEIN, 'M', "MET"),
    PHENYLALANINE(ChainType.PROTEIN, 'F', "PHE"),
    PROLINE(ChainType.PROTEIN, 'P', "PRO"),
    SERINE(ChainType.PROTEIN, 'S', "SER"),
    THREONINE(ChainType.PROTEIN, 'T', "THR"),
    TRYPTOPHAN(ChainType.PROTEIN, 'W', "TRP"),
    TYROSINE(ChainType.PROTEIN, 'Y', "TYR"),
    VALINE(ChainType.PROTEIN, 'V', "VAL");

    private final ChainType chainType;
    private final char oneLetter;
    private final String[] names;
    private final List<AtomName> residueAtoms;

    private ResidueType(ChainType chainType, char oneLetter, String... names) {
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

    public ChainType getChainType() {
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

    public static ResidueType fromString(ChainType chainType, String pdbName) {
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

    public static ResidueType fromOneLetter(ChainType chainType, char oneLetter) {
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
