package pl.poznan.put.pdb.analysis;

import org.biojava.nbio.structure.Chain;
import org.biojava.nbio.structure.Group;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PdbChain implements Comparable<PdbChain>, Serializable {
    private static final long serialVersionUID = -954932883855490919L;

    private final String identifier;
    private final List<PdbResidue> residues;
    private final MoleculeType moleculeType;

    public PdbChain(final String identifier, final List<PdbResidue> residues) {
        super();
        this.identifier = identifier;
        this.residues = new ArrayList<>(residues);
        moleculeType = PdbChain.assertMoleculeType(residues);
    }

    private static MoleculeType assertMoleculeType(
            final Iterable<PdbResidue> residues) {
        int rnaCounter = 0;
        int proteinCounter = 0;

        for (final PdbResidue residue : residues) {
            switch (residue.getMoleculeType()) {
                case PROTEIN:
                    proteinCounter += 1;
                    break;
                case RNA:
                    rnaCounter += 1;
                    break;
                case UNKNOWN:
                    break;
            }
        }

        return (rnaCounter > proteinCounter) ? MoleculeType.RNA
                                             : MoleculeType.PROTEIN;
    }

    public static PdbChain fromBioJavaChain(final Chain chain) {
        final List<PdbResidue> residues = new ArrayList<>();
        for (final Group group : chain.getAtomGroups()) {
            residues.add(PdbResidue.fromBioJavaGroup(group));
        }
        return new PdbChain(chain.getId(), residues);
    }

    public final String getIdentifier() {
        return identifier;
    }

    public final List<PdbResidue> getResidues() {
        return Collections.unmodifiableList(residues);
    }

    @Override
    public final int hashCode() {
        int result = identifier.hashCode();
        result = (31 * result) + residues.hashCode();
        result = (31 * result) + moleculeType.hashCode();
        return result;
    }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }

        final PdbChain pdbChain = (PdbChain) o;
        return Objects.equals(identifier, pdbChain.identifier) && Objects
                .equals(residues, pdbChain.residues) && (moleculeType
                                                         == pdbChain
                                                                 .moleculeType);
    }

    @Override
    public final String toString() {
        return String.valueOf(identifier);
    }

    @Override
    public final int compareTo(@Nonnull final PdbChain t) {
        return identifier.compareTo(t.identifier);
    }

    public final String getSequence() {
        final StringBuilder builder = new StringBuilder();
        for (final PdbResidue residue : residues) {
            builder.append(residue.getOneLetterName());
        }
        return builder.toString();
    }

    public final MoleculeType getMoleculeType() {
        return moleculeType;
    }
}
