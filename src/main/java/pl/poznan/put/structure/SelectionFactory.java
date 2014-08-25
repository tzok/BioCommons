package pl.poznan.put.structure;

import java.util.ArrayList;
import java.util.List;

import org.biojava.bio.structure.Chain;
import org.biojava.bio.structure.Group;
import org.biojava.bio.structure.Structure;

import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.helper.StructureHelper;

public class SelectionFactory {
    public static StructureSelection create(String name, Structure structure) {
        return SelectionFactory.create(name, structure.getChains());
    }

    public static StructureSelection create(String name, List<Chain> chains) {
        List<Group> residues = SelectionFactory.getAllResidues(chains);
        return new StructureSelection(name, residues);
    }

    public static StructureSelection create(String name, Chain chain) {
        List<Group> residues = SelectionFactory.getAllResidues(chain);
        return new StructureSelection(name, residues);
    }

    private static List<Group> getAllResidues(List<Chain> chains) {
        List<Group> residues = new ArrayList<>();
        for (Chain chain : chains) {
            residues.addAll(SelectionFactory.getAllResidues(chain));
        }
        return residues;
    }

    private static List<Group> getAllResidues(Chain chain) {
        List<Group> residues = new ArrayList<>();

        for (Group group : chain.getAtomGroups()) {
            StructureHelper.mergeAltLocs(group);
            if (MoleculeType.detect(group) != MoleculeType.UNKNOWN) {
                residues.add(group);
            }
        }

        return residues;
    }

    private SelectionFactory() {
    }
}
