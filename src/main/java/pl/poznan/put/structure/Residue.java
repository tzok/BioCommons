package pl.poznan.put.structure;

import java.io.Serializable;

import org.biojava.bio.structure.Group;
import org.biojava.bio.structure.ResidueNumber;

import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.common.ResidueType;

public class Residue implements Comparable<Residue>, Serializable {
    private static final long serialVersionUID = -3434793294284494678L;

    public static Residue fromGroup(Group group) {
        ResidueNumber resn = group.getResidueNumber();

        char chain = group.getChainId().charAt(0);
        String residueName = group.getPDBName().trim();
        int residueNumber = resn.getSeqNum();
        char insertionCode = ' ';
        if (resn.getInsCode() != null) {
            insertionCode = resn.getInsCode();
        }

        MoleculeType moleculeType = MoleculeType.detect(group);
        ResidueType residueType = ResidueType.fromString(moleculeType,
                residueName);

        if (residueType == ResidueType.UNKNOWN) {
            residueType = ResidueType.detect(group);
        }

        return new Residue(residueType, chain, residueName, residueNumber,
                insertionCode, false);
    }

    private final ResidueType residueType;
    private final char chain;
    private final String residueName;
    private final int residueNumber;
    private final char insertionCode;
    private final boolean isMissing;

    // by default this equals to the last letter of the residue name
    // if a MODRES is found in PDB, this should be set by a setter method
    private char residueNameOneLetter;

    public Residue(ResidueType residueType, char chain, String residueName,
            int residueNumber, char insertionCode, boolean isMissing) {
        super();
        this.residueType = residueType;
        this.chain = chain;
        this.residueName = residueName;
        this.residueNumber = residueNumber;
        this.insertionCode = insertionCode;
        this.isMissing = isMissing;

        residueNameOneLetter = residueType.getOneLetter();
    }

    public ResidueType getResidueType() {
        return residueType;
    }

    public char getChain() {
        return chain;
    }

    public String getResidueName() {
        return residueName;
    }

    public char getResidueNameOneLetter() {
        return residueNameOneLetter;
    }

    public int getResidueNumber() {
        return residueNumber;
    }

    public char getInsertionCode() {
        return insertionCode;
    }

    public boolean isMissing() {
        return isMissing;
    }

    public void setResidueNameOneLetter(char residueNameOneLetter) {
        this.residueNameOneLetter = residueNameOneLetter;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(chain);
        builder.append('.');
        builder.append(residueName);
        builder.append(residueNumber);
        if (insertionCode != ' ') {
            builder.append('^');
            builder.append(insertionCode);
        }
        return builder.toString();
    }

    @Override
    public int compareTo(Residue o) {
        if (equals(o)) {
            return 0;
        }

        if (chain < o.chain) {
            return -1;
        }
        if (chain > o.chain) {
            return 1;
        }

        if (residueNumber < o.residueNumber) {
            return -1;
        }
        if (residueNumber > o.residueNumber) {
            return 1;
        }

        if (insertionCode < o.insertionCode) {
            return -1;
        }
        if (insertionCode > o.insertionCode) {
            return 1;
        }

        return 0;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + chain;
        result = prime * result + insertionCode;
        result = prime * result + (isMissing ? 1231 : 1237);
        result = prime * result
                + ((residueName == null) ? 0 : residueName.hashCode());
        result = prime * result + residueNameOneLetter;
        result = prime * result + residueNumber;
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
        if (!(obj instanceof Residue)) {
            return false;
        }
        Residue other = (Residue) obj;
        if (chain != other.chain) {
            return false;
        }
        if (insertionCode != other.insertionCode) {
            return false;
        }
        if (isMissing != other.isMissing) {
            return false;
        }
        if (residueName == null) {
            if (other.residueName != null) {
                return false;
            }
        } else if (!residueName.equals(other.residueName)) {
            return false;
        }
        if (residueNameOneLetter != other.residueNameOneLetter) {
            return false;
        }
        if (residueNumber != other.residueNumber) {
            return false;
        }
        return true;
    }
}
