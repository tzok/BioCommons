package pl.poznan.put.nucleotide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.biojava.bio.structure.Group;

import pl.poznan.put.helper.Helper;

public class NucleotideAtoms {
    private static final Map<NucleotideType, List<AtomName>> MAP =
            new HashMap<NucleotideType, List<AtomName>>();

    static {
        List<AtomName> atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.N9);
        atoms.add(AtomName.C4);
        atoms.add(AtomName.N2);
        atoms.add(AtomName.H21);
        atoms.add(AtomName.H22);
        atoms.add(AtomName.N3);
        atoms.add(AtomName.C2);
        atoms.add(AtomName.N1);
        atoms.add(AtomName.H1);
        atoms.add(AtomName.C6);
        atoms.add(AtomName.O6);
        atoms.add(AtomName.C5);
        atoms.add(AtomName.N7);
        atoms.add(AtomName.C8);
        atoms.add(AtomName.H8);
        NucleotideAtoms.MAP.put(NucleotideType.GUANINE, atoms);

        atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.N9);
        atoms.add(AtomName.C5);
        atoms.add(AtomName.N7);
        atoms.add(AtomName.C8);
        atoms.add(AtomName.H8);
        atoms.add(AtomName.N1);
        atoms.add(AtomName.C2);
        atoms.add(AtomName.H2);
        atoms.add(AtomName.N3);
        atoms.add(AtomName.C4);
        atoms.add(AtomName.C6);
        atoms.add(AtomName.N6);
        atoms.add(AtomName.H61);
        atoms.add(AtomName.H62);
        NucleotideAtoms.MAP.put(NucleotideType.ADENINE, atoms);

        atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.N1);
        atoms.add(AtomName.C6);
        atoms.add(AtomName.H6);
        atoms.add(AtomName.C5);
        atoms.add(AtomName.H5);
        atoms.add(AtomName.C2);
        atoms.add(AtomName.O2);
        atoms.add(AtomName.N3);
        atoms.add(AtomName.C4);
        atoms.add(AtomName.N4);
        atoms.add(AtomName.H41);
        atoms.add(AtomName.H42);
        NucleotideAtoms.MAP.put(NucleotideType.CYTOSINE, atoms);

        atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.N1);
        atoms.add(AtomName.C6);
        atoms.add(AtomName.H6);
        atoms.add(AtomName.C2);
        atoms.add(AtomName.O2);
        atoms.add(AtomName.N3);
        atoms.add(AtomName.H3);
        atoms.add(AtomName.C4);
        atoms.add(AtomName.O4);
        atoms.add(AtomName.C5);
        atoms.add(AtomName.C5M);
        atoms.add(AtomName.H51);
        atoms.add(AtomName.H52);
        atoms.add(AtomName.H53);
        NucleotideAtoms.MAP.put(NucleotideType.THYMINE, atoms);

        atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.N1);
        atoms.add(AtomName.C6);
        atoms.add(AtomName.H6);
        atoms.add(AtomName.C2);
        atoms.add(AtomName.O2);
        atoms.add(AtomName.N3);
        atoms.add(AtomName.H3);
        atoms.add(AtomName.C4);
        atoms.add(AtomName.O4);
        atoms.add(AtomName.C5);
        atoms.add(AtomName.H5);
        NucleotideAtoms.MAP.put(NucleotideType.URACIL, atoms);
    }

    public static List<AtomName> getAtoms(NucleotideType type) {
        if (NucleotideAtoms.MAP.containsKey(type)) {
            return NucleotideAtoms.MAP.get(type);
        }

        return new ArrayList<AtomName>();
    }

    public static NucleotideType guessType(Group residue) {
        int bestScore = 0;
        NucleotideType bestType = NucleotideType.UNKNOWN;

        for (Entry<NucleotideType, List<AtomName>> entry : NucleotideAtoms.MAP.entrySet()) {
            int score = 0;

            for (AtomName atomType : entry.getValue()) {
                if (Helper.findAtom(residue, atomType) != null) {
                    score++;
                }
            }

            if (score > bestScore) {
                bestScore = score;
                bestType = entry.getKey();
            }
        }

        return bestType;
    }

    private NucleotideAtoms() {
    }
}
