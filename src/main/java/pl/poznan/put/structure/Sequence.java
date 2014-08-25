package pl.poznan.put.structure;

import java.util.ArrayList;
import java.util.List;

public class Sequence {
    private final StringBuilder builder = new StringBuilder();
    private final List<Residue> residues = new ArrayList<>();

    public static Sequence fromCompactFragment(CompactFragment fragment) {
        Sequence sequence = new Sequence();

        for (int i = 0; i < fragment.getSize(); i++) {
            sequence.addResidue(fragment.getResidue(i));
        }

        return sequence;
    }

    public void addResidues(List<Residue> residueList) {
        for (Residue residue : residueList) {
            addResidue(residue);
        }
    }

    public void addResidue(Residue residue) {
        builder.append(residue.getResidueNameOneLetter());
        residues.add(residue);
    }

    public Residue getResidue(int index) {
        return residues.get(index);
    }

    public int getSize() {
        return residues.size();
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
