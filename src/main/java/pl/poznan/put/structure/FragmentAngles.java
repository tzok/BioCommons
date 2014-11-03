package pl.poznan.put.structure;

import java.util.Iterator;
import java.util.List;

public class FragmentAngles implements Iterable<ResidueAngles> {
    private final List<ResidueAngles> residueAngles;

    public FragmentAngles(List<ResidueAngles> residueAngles) {
        super();
        this.residueAngles = residueAngles;
    }

    public ResidueAngles get(int index) {
        return residueAngles.get(index);
    }

    public int getSize() {
        return residueAngles.size();
    }

    @Override
    public Iterator<ResidueAngles> iterator() {
        return residueAngles.iterator();
    }
}
