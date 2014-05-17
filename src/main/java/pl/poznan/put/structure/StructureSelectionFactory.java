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
        return StructureSelectionFactory.create(name, structure.getChains());
    }

    public static StructureSelection create(String name, List<Chain> chains) {
        List<Group> residues = StructureSelectionFactory.getAllResidues(chains);
        return new StructureSelection(name, residues);
    }

    private static List<Group> getAllResidues(List<Chain> chains) {
        List<Group> residues = new ArrayList<Group>();

        for (Chain chain : chains) {
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
