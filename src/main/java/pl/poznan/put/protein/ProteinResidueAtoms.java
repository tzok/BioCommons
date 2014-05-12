package pl.poznan.put.protein;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.poznan.put.atoms.AtomName;
import pl.poznan.put.common.ResidueType;

public class ProteinResidueAtoms {
    private static final Map<ResidueType, List<AtomName>> MAP = new HashMap<ResidueType, List<AtomName>>();

    static {
        List<AtomName> atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.CB);
        atoms.add(AtomName.HB1);
        atoms.add(AtomName.HB2);
        atoms.add(AtomName.HB3);
        ProteinResidueAtoms.MAP.put(ResidueType.ALANINE, atoms);

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
        ProteinResidueAtoms.MAP.put(ResidueType.ARGININE, atoms);

        atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.CB);
        atoms.add(AtomName.HB1);
        atoms.add(AtomName.HB2);
        atoms.add(AtomName.CG);
        atoms.add(AtomName.OD1);
        atoms.add(AtomName.ND2);
        atoms.add(AtomName.HD21);
        atoms.add(AtomName.HD22);
        ProteinResidueAtoms.MAP.put(ResidueType.ASPARAGINE, atoms);

        atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.CB);
        atoms.add(AtomName.HB1);
        atoms.add(AtomName.HB2);
        atoms.add(AtomName.CG);
        atoms.add(AtomName.OD1);
        atoms.add(AtomName.OD2);
        ProteinResidueAtoms.MAP.put(ResidueType.ASPARTIC_ACID, atoms);

        atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.CB);
        atoms.add(AtomName.HB1);
        atoms.add(AtomName.HB2);
        atoms.add(AtomName.SG);
        atoms.add(AtomName.HG1);
        ProteinResidueAtoms.MAP.put(ResidueType.CYSTEINE, atoms);

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
        ProteinResidueAtoms.MAP.put(ResidueType.GLUTAMINE, atoms);

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
        ProteinResidueAtoms.MAP.put(ResidueType.GLUTAMIC_ACID, atoms);

        atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.HA1);
        atoms.add(AtomName.HA2);
        ProteinResidueAtoms.MAP.put(ResidueType.GLYCINE, atoms);

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
        ProteinResidueAtoms.MAP.put(ResidueType.HISTIDINE, atoms);

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
        ProteinResidueAtoms.MAP.put(ResidueType.ISOLEUCINE, atoms);

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
        ProteinResidueAtoms.MAP.put(ResidueType.LEUCINE, atoms);

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
        ProteinResidueAtoms.MAP.put(ResidueType.LYSINE, atoms);

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
        ProteinResidueAtoms.MAP.put(ResidueType.METHIONINE, atoms);

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
        ProteinResidueAtoms.MAP.put(ResidueType.PHENYLALANINE, atoms);

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
        ProteinResidueAtoms.MAP.put(ResidueType.PROLINE, atoms);

        atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.CB);
        atoms.add(AtomName.HB1);
        atoms.add(AtomName.HB2);
        atoms.add(AtomName.OG);
        atoms.add(AtomName.HG1);
        ProteinResidueAtoms.MAP.put(ResidueType.SERINE, atoms);

        atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.CB);
        atoms.add(AtomName.HB);
        atoms.add(AtomName.OG1);
        atoms.add(AtomName.HG1);
        atoms.add(AtomName.CG2);
        atoms.add(AtomName.HG21);
        atoms.add(AtomName.HG22);
        atoms.add(AtomName.HG23);
        ProteinResidueAtoms.MAP.put(ResidueType.THREONINE, atoms);

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
        ProteinResidueAtoms.MAP.put(ResidueType.TRYPTOPHAN, atoms);

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
        ProteinResidueAtoms.MAP.put(ResidueType.TYROSINE, atoms);

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
        ProteinResidueAtoms.MAP.put(ResidueType.THREONINE, atoms);
    }

    public static List<AtomName> getAtoms(ResidueType residueType) {
        if (residueType == null || !ProteinResidueAtoms.MAP.containsKey(residueType)) {
            return null;
        }

        return ProteinResidueAtoms.MAP.get(residueType);

    }

    private ProteinResidueAtoms() {
    }
}
