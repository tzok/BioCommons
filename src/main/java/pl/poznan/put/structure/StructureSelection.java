package pl.poznan.put.structure;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.biojava.bio.structure.Group;

import pl.poznan.put.common.MoleculeType;

public class StructureSelection {
    private final String name;
    private final List<Group> residues;

    public StructureSelection(String name, List<Group> residues) {
        super();
        this.name = name;
        this.residues = residues;
    }

    public String getName() {
        return name;
    }

    public List<Group> getResidues() {
        return residues;
    }

    public int getSize() {
        return residues.size();
    }

    public void computeTorsionAngles() {
        getCompactFragments();
    }

    public List<CompactFragment> getCompactFragments() {
        List<CompactFragment> result = new ArrayList<CompactFragment>();

        if (residues.size() == 0) {
            return result;
        }

        Group first = residues.get(0);
        CompactFragment current = new CompactFragment(this,
                MoleculeType.detect(first));
        current.addResidue(first);

        for (int i = 0; i < residues.size() - 1; i++) {
            Group r1 = residues.get(i);
            Group r2 = residues.get(i + 1);
            MoleculeType c1 = MoleculeType.detect(r1);
            MoleculeType c2 = MoleculeType.detect(r2);

            if (c1 == c2 && c1.areConnected(r1, r2)) {
                current.addResidue(r2);
            } else {
                result.add(current);
                current = new CompactFragment(this, c2);
                current.addResidue(r2);
            }
        }

        result.add(current);
        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (name == null ? 0 : name.hashCode());
        result = prime * result + (residues == null ? 0 : residues.hashCode());
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        StructureSelection other = (StructureSelection) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (residues == null) {
            if (other.residues != null) {
                return false;
            }
        } else if (!CollectionUtils.isEqualCollection(residues, other.residues)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        Residue first = Residue.fromGroup(residues.get(0));
        Residue last = Residue.fromGroup(residues.get(residues.size() - 1));
        return first + " - " + last + " (count: " + residues.size() + ")";
    }
}
