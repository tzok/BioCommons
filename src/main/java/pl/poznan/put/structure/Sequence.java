package pl.poznan.put.structure;

import java.util.ArrayList;
import java.util.List;

public class Sequence {
    private final StringBuilder builder = new StringBuilder();
    private final List<Residue> residues = new ArrayList<Residue>();

    public void addResidues(List<Residue> residueList) {
        for (Residue residue : residueList) {
            addResidue(residue);
        }
    }

    public void addResidue(Residue residue) {
        builder.append(residue.getResidueNameOneLetter());
        residues.add(residue);
    }

    public List<Residue> getResidues() {
        return residues;
    }

    public int getLength() {
        return builder.length();
    }

    @Override
    public String toString() {
        return builder.toString();
    }

    public String substring(int start, int end) {
        return builder.substring(start, end);
    }
}
