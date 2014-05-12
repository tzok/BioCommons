package pl.poznan.put.common;

import java.util.List;

import org.biojava.bio.structure.Chain;
import org.biojava.bio.structure.Group;

import pl.poznan.put.atoms.AtomName;
import pl.poznan.put.helper.Helper;
import pl.poznan.put.nucleic.RNABackboneAtoms;
import pl.poznan.put.protein.ProteinBackboneAtoms;

public enum ChainType {
    RNA(RNABackboneAtoms.getAtoms()), PROTEIN(ProteinBackboneAtoms.getAtoms()),
    UNKNOWN(null);

    private final List<AtomName> backboneAtoms;

    private ChainType(List<AtomName> backboneAtoms) {
        this.backboneAtoms = backboneAtoms;
    }

    public List<AtomName> getBackboneAtoms() {
        return backboneAtoms;
    }

    public static ChainType detect(Chain chain) {
        // decide upon first residue only!
        Group residue = chain.getAtomGroup(0);

        int bestScore = 0;
        ChainType bestType = ChainType.UNKNOWN;

        for (ChainType type : ChainType.values()) {
            int score = 0;
            List<AtomName> listing = type.getBackboneAtoms();

            if (listing != null) {
                for (AtomName atomType : listing) {
                    if (Helper.findAtom(residue, atomType) != null) {
                        score++;
                    }
                }

                if (score > bestScore) {
                    bestScore = score;
                    bestType = type;
                }
            }
        }

        return bestType;
    }
}
