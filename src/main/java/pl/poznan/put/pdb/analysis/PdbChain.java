package pl.poznan.put.pdb.analysis;

import org.biojava.nbio.structure.Chain;
import org.biojava.nbio.structure.Group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PdbChain implements Comparable<PdbChain> {
    private final String identifier;
    private final List<PdbResidue> residues;
    private final MoleculeType moleculeType;
    public PdbChain(String identifier, List<PdbResidue> residues) {
        super();
        this.identifier = identifier;
        this.residues = residues;
        this.moleculeType = PdbChain.assertMoleculeType(residues);
    }

    private static MoleculeType assertMoleculeType(List<PdbResidue> residues) {
        int rnaCounter = 0;
        int proteinCounter = 0;

        for (PdbResidue residue : residues) {
            switch (residue.getMoleculeType()) {
                case PROTEIN:
                    proteinCounter += 1;
                    break;
                case RNA:
                    rnaCounter += 1;
                    break;
                case UNKNOWN:
                default:
                    break;
            }
        }

        return rnaCounter > proteinCounter ? MoleculeType.RNA
                                           : MoleculeType.PROTEIN;
    }

    public static PdbChain fromBioJavaChain(Chain chain) {
        List<PdbResidue> residues = new ArrayList<>();
        for (Group group : chain.getAtomGroups()) {
            residues.add(PdbResidue.fromBioJavaGroup(group));
        }
        return new PdbChain(chain.getChainID(), residues);
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<PdbResidue> getResidues() {
        return Collections.unmodifiableList(residues);
    }

    @Override
    public int hashCode() {
        int result = identifier.hashCode();
        result = 31 * result + residues.hashCode();
        result = 31 * result + moleculeType.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PdbChain pdbChain = (PdbChain) o;
        return identifier.equals(pdbChain.identifier) && residues
                .equals(pdbChain.residues)
               && moleculeType == pdbChain.moleculeType;
    }

    @Override
    public String toString() {
        return String.valueOf(identifier);
    }

    @Override
    public int compareTo(PdbChain o) {
        if (o == null) {
            throw new NullPointerException();
        }
        return identifier.compareTo(o.identifier);
    }

    public String getSequence() {
        StringBuilder builder = new StringBuilder();
        for (PdbResidue residue : residues) {
            builder.append(residue.getOneLetterName());
        }
        return builder.toString();
    }

    public MoleculeType getMoleculeType() {
        return moleculeType;
    }
}
