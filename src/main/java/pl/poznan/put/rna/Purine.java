package pl.poznan.put.rna;

import java.util.List;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.rna.torsion.Chi;
import pl.poznan.put.types.Quadruplet;

public abstract class Purine extends Base {
    protected Purine(List<AtomName> atoms, String longName, char oneLetterName,
            String... names) {
        super(atoms, longName, oneLetterName, names);
        torsionAngleTypes.add(Chi.getPurineInstance());
    }

    @Override
    public Quadruplet<AtomName> getChiAtoms() {
        return Chi.PURINE_ATOMS;
    }
}
