package pl.poznan.put.protein;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.biojava.bio.structure.Group;

import pl.poznan.put.atoms.AtomName;
import pl.poznan.put.helper.Helper;

public class AminoAcidAtoms {
    private static final Map<AminoAcidType, List<AtomName>> MAP = new HashMap<AminoAcidType, List<AtomName>>();

    static {
        List<AtomName> atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.CB);
        atoms.add(AtomName.HB1);
        atoms.add(AtomName.HB2);
        atoms.add(AtomName.HB3);
        AminoAcidAtoms.MAP.put(AminoAcidType.ALANINE, atoms);

        atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.CB);
        atoms.add(AtomName.HB1);
        atoms.add(AtomName.HB2);
        atoms.add(AtomName.CG);
        atoms.add(AtomName.HG1);
        atoms.add(AtomName.HG2);
        atoms.add(AtomName.CD);
        atoms.add(AtomName.HD1);
        atoms.add(AtomName.HD2);
        atoms.add(AtomName.NE);
        atoms.add(AtomName.HE);
        atoms.add(AtomName.CZ);
        atoms.add(AtomName.NH1);
        atoms.add(AtomName.HH11);
        atoms.add(AtomName.HH12);
        atoms.add(AtomName.NH2);
        atoms.add(AtomName.HH21);
        atoms.add(AtomName.HH22);
        AminoAcidAtoms.MAP.put(AminoAcidType.ARGININE, atoms);

        atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.CB);
        atoms.add(AtomName.HB1);
        atoms.add(AtomName.HB2);
        atoms.add(AtomName.CG);
        atoms.add(AtomName.OD1);
        atoms.add(AtomName.ND2);
        atoms.add(AtomName.HD21);
        atoms.add(AtomName.HD22);
        AminoAcidAtoms.MAP.put(AminoAcidType.ASPARAGINE, atoms);

        atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.CB);
        atoms.add(AtomName.HB1);
        atoms.add(AtomName.HB2);
        atoms.add(AtomName.CG);
        atoms.add(AtomName.OD1);
        atoms.add(AtomName.OD2);
        AminoAcidAtoms.MAP.put(AminoAcidType.ASPARTIC_ACID, atoms);

        atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.CB);
        atoms.add(AtomName.HB1);
        atoms.add(AtomName.HB2);
        atoms.add(AtomName.SG);
        atoms.add(AtomName.HG1);
        AminoAcidAtoms.MAP.put(AminoAcidType.CYSTEINE, atoms);

        atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.CB);
        atoms.add(AtomName.HB1);
        atoms.add(AtomName.HB2);
        atoms.add(AtomName.CG);
        atoms.add(AtomName.HG1);
        atoms.add(AtomName.HG2);
        atoms.add(AtomName.CD);
        atoms.add(AtomName.OE1);
        atoms.add(AtomName.NE2);
        atoms.add(AtomName.HE21);
        atoms.add(AtomName.HE22);
        AminoAcidAtoms.MAP.put(AminoAcidType.GLUTAMINE, atoms);

        atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.CB);
        atoms.add(AtomName.HB1);
        atoms.add(AtomName.HB2);
        atoms.add(AtomName.CG);
        atoms.add(AtomName.HG1);
        atoms.add(AtomName.HG2);
        atoms.add(AtomName.CD);
        atoms.add(AtomName.OE1);
        atoms.add(AtomName.OE2);
        AminoAcidAtoms.MAP.put(AminoAcidType.GLUTAMIC_ACID, atoms);

        atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.HA1);
        atoms.add(AtomName.HA2);
        AminoAcidAtoms.MAP.put(AminoAcidType.GLYCINE, atoms);

        atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.CB);
        atoms.add(AtomName.HB1);
        atoms.add(AtomName.HB2);
        atoms.add(AtomName.ND1);
        atoms.add(AtomName.HD1); // this is optional
        atoms.add(AtomName.CG);
        atoms.add(AtomName.CE1);
        atoms.add(AtomName.HE1);
        atoms.add(AtomName.NE2);
        atoms.add(AtomName.HE2); // this is optional
        atoms.add(AtomName.CD2);
        atoms.add(AtomName.HD2);
        AminoAcidAtoms.MAP.put(AminoAcidType.HISTIDINE, atoms);

        atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.CB);
        atoms.add(AtomName.HB);
        atoms.add(AtomName.CG2);
        atoms.add(AtomName.HG21);
        atoms.add(AtomName.HG22);
        atoms.add(AtomName.HG23);
        atoms.add(AtomName.CG1);
        atoms.add(AtomName.HG11);
        atoms.add(AtomName.HG12);
        atoms.add(AtomName.CD);
        atoms.add(AtomName.HD1);
        atoms.add(AtomName.HD2);
        atoms.add(AtomName.HD3);
        AminoAcidAtoms.MAP.put(AminoAcidType.ISOLEUCINE, atoms);

        atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.CB);
        atoms.add(AtomName.HB1);
        atoms.add(AtomName.HB2);
        atoms.add(AtomName.CG);
        atoms.add(AtomName.HG);
        atoms.add(AtomName.CD1);
        atoms.add(AtomName.HD11);
        atoms.add(AtomName.HD12);
        atoms.add(AtomName.HD13);
        atoms.add(AtomName.CD2);
        atoms.add(AtomName.HD21);
        atoms.add(AtomName.HD22);
        atoms.add(AtomName.HD23);
        AminoAcidAtoms.MAP.put(AminoAcidType.LEUCINE, atoms);

        atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.CB);
        atoms.add(AtomName.HB1);
        atoms.add(AtomName.HB2);
        atoms.add(AtomName.CG);
        atoms.add(AtomName.HG1);
        atoms.add(AtomName.HG2);
        atoms.add(AtomName.CD);
        atoms.add(AtomName.HD1);
        atoms.add(AtomName.HD2);
        atoms.add(AtomName.CE);
        atoms.add(AtomName.HE1);
        atoms.add(AtomName.HE2);
        atoms.add(AtomName.NZ);
        atoms.add(AtomName.HZ1);
        atoms.add(AtomName.HZ2);
        atoms.add(AtomName.HZ3);
        AminoAcidAtoms.MAP.put(AminoAcidType.LYSINE, atoms);

        atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.CB);
        atoms.add(AtomName.HB1);
        atoms.add(AtomName.HB2);
        atoms.add(AtomName.CG);
        atoms.add(AtomName.HG1);
        atoms.add(AtomName.HG2);
        atoms.add(AtomName.SD);
        atoms.add(AtomName.CE);
        atoms.add(AtomName.HE1);
        atoms.add(AtomName.HE2);
        atoms.add(AtomName.HE3);
        AminoAcidAtoms.MAP.put(AminoAcidType.METHIONINE, atoms);

        atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.CB);
        atoms.add(AtomName.HB1);
        atoms.add(AtomName.HB2);
        atoms.add(AtomName.CG);
        atoms.add(AtomName.CD1);
        atoms.add(AtomName.HD1);
        atoms.add(AtomName.CE1);
        atoms.add(AtomName.HE1);
        atoms.add(AtomName.CZ);
        atoms.add(AtomName.HZ);
        atoms.add(AtomName.CD2);
        atoms.add(AtomName.HD2);
        atoms.add(AtomName.CE2);
        atoms.add(AtomName.HE2);
        AminoAcidAtoms.MAP.put(AminoAcidType.PHENYLALANINE, atoms);

        atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.CB);
        atoms.add(AtomName.HB1);
        atoms.add(AtomName.HB2);
        atoms.add(AtomName.CD);
        atoms.add(AtomName.HD1);
        atoms.add(AtomName.HD2);
        atoms.add(AtomName.CG);
        atoms.add(AtomName.HG1);
        atoms.add(AtomName.HG2);
        AminoAcidAtoms.MAP.put(AminoAcidType.PROLINE, atoms);

        atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.CB);
        atoms.add(AtomName.HB1);
        atoms.add(AtomName.HB2);
        atoms.add(AtomName.OG);
        atoms.add(AtomName.HG1);
        AminoAcidAtoms.MAP.put(AminoAcidType.SERINE, atoms);

        atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.CB);
        atoms.add(AtomName.HB);
        atoms.add(AtomName.OG1);
        atoms.add(AtomName.HG1);
        atoms.add(AtomName.CG2);
        atoms.add(AtomName.HG21);
        atoms.add(AtomName.HG22);
        atoms.add(AtomName.HG23);
        AminoAcidAtoms.MAP.put(AminoAcidType.THREONINE, atoms);

        atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.CB);
        atoms.add(AtomName.HB1);
        atoms.add(AtomName.HB2);
        atoms.add(AtomName.CG);
        atoms.add(AtomName.CD1);
        atoms.add(AtomName.HD1);
        atoms.add(AtomName.NE1);
        atoms.add(AtomName.HE1);
        atoms.add(AtomName.CE2);
        atoms.add(AtomName.CD2);
        atoms.add(AtomName.CE3);
        atoms.add(AtomName.HE3);
        atoms.add(AtomName.CZ3);
        atoms.add(AtomName.HZ3);
        atoms.add(AtomName.CZ2);
        atoms.add(AtomName.HZ2);
        atoms.add(AtomName.CH2);
        atoms.add(AtomName.HH2);
        AminoAcidAtoms.MAP.put(AminoAcidType.TRYPTOPHAN, atoms);

        atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.CB);
        atoms.add(AtomName.HB1);
        atoms.add(AtomName.HB2);
        atoms.add(AtomName.CG);
        atoms.add(AtomName.CD1);
        atoms.add(AtomName.HD1);
        atoms.add(AtomName.CE1);
        atoms.add(AtomName.HE1);
        atoms.add(AtomName.CZ);
        atoms.add(AtomName.OH);
        atoms.add(AtomName.HH);
        atoms.add(AtomName.CD2);
        atoms.add(AtomName.HD2);
        atoms.add(AtomName.CE2);
        atoms.add(AtomName.HE2);
        AminoAcidAtoms.MAP.put(AminoAcidType.TYROSINE, atoms);

        atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.CB);
        atoms.add(AtomName.HB);
        atoms.add(AtomName.CG1);
        atoms.add(AtomName.HG11);
        atoms.add(AtomName.HG12);
        atoms.add(AtomName.HG13);
        atoms.add(AtomName.CG2);
        atoms.add(AtomName.HG21);
        atoms.add(AtomName.HG22);
        atoms.add(AtomName.HG23);
        AminoAcidAtoms.MAP.put(AminoAcidType.THREONINE, atoms);
    }

    public static List<AtomName> getAtoms(AminoAcidType type) {
        if (AminoAcidAtoms.MAP.containsKey(type)) {
            return AminoAcidAtoms.MAP.get(type);
        }

        return new ArrayList<AtomName>();
    }

    public static AminoAcidType guessType(Group residue) {
        int bestScore = 0;
        AminoAcidType bestType = AminoAcidType.UNKNOWN;

        for (Entry<AminoAcidType, List<AtomName>> entry : AminoAcidAtoms.MAP.entrySet()) {
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

    private AminoAcidAtoms() {
    }
}
