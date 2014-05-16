package pl.poznan.put.structure;

import java.util.ArrayList;
import java.util.List;

import org.biojava.bio.structure.Chain;
import org.biojava.bio.structure.Group;
import org.biojava.bio.structure.Structure;

import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.helper.StructureHelper;

public class StructureSelectionFactory {
    public static StructureSelection create(String name, Structure structure) {
        List<Group> residues = StructureSelectionFactory.getAllResidues(structure);
        return new StructureSelection(name, residues);
    }

    public static StructureSelection create(Structure structure) {
        List<Group> residues = StructureSelectionFactory.getAllResidues(structure);
        String name;

        if (residues.size() > 0) {
            name = residues.get(0).getChain().getParent().getPDBCode();
        } else {
            name = "-";
        }

        return new StructureSelection(name, residues);
    }

    private static List<Group> getAllResidues(Structure structure) {
        List<Group> residues = new ArrayList<Group>();

        for (Chain chain : structure.getChains()) {
            for (Group group : chain.getAtomGroups()) {
                StructureHelper.mergeAltLocs(group);
                if (MoleculeType.detect(group) != MoleculeType.UNKNOWN) {
                    residues.add(group);
                }
            }
        }

        return residues;
    }

    private StructureSelectionFactory() {
    }
}
