package pl.poznan.put.structure;

import java.util.List;

public class FragmentAngles {
    private final List<ResidueAngles> residueAngles;

    public FragmentAngles(List<ResidueAngles> residueAngles) {
        super();
        this.residueAngles = residueAngles;
    }

    public ResidueAngles get(int index) {
        return residueAngles.get(index);
    }
}
