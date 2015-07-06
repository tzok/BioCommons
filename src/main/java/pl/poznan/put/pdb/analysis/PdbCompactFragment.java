package pl.poznan.put.pdb.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import pl.poznan.put.torsion.MasterTorsionAngleType;
import pl.poznan.put.torsion.TorsionAngleType;
import pl.poznan.put.torsion.TorsionAngleValue;

public class PdbCompactFragment implements ResidueCollection {
    private final String name;
    private final List<PdbResidue> residues;
    private final Map<PdbResidue, List<TorsionAngleValue>> mapResidueAngleValue = new LinkedHashMap<PdbResidue, List<TorsionAngleValue>>();

    public PdbCompactFragment(String name, List<PdbResidue> residues) {
        super();
        this.name = name;
        this.residues = residues;

        for (int i = 0; i < residues.size(); i++) {
            PdbResidue residue = residues.get(i);
            List<TorsionAngleValue> values = new ArrayList<TorsionAngleValue>();

            for (TorsionAngleType type : residue.getTorsionAngleTypes()) {
                TorsionAngleValue value = type.calculate(residues, i);
                values.add(value);
            }

            mapResidueAngleValue.put(residue, values);
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public List<PdbResidue> getResidues() {
        return Collections.unmodifiableList(residues);
    }

    @Override
    public String toString() {
        PdbResidue first = residues.get(0);
        PdbResidue last = residues.get(residues.size() - 1);
        return first + " - " + last + " (count: " + residues.size() + ")";
    }

    public String toPdb() {
        StringBuilder builder = new StringBuilder();
        for (PdbResidue residue : residues) {
            builder.append(residue.toPdb());
        }
        return builder.toString();
    }

    public String toSequence() {
        StringBuilder builder = new StringBuilder();
        for (PdbResidue residue : residues) {
            builder.append(residue.getOneLetterName());
        }
        return builder.toString();
    }

    public PdbCompactFragment shift(int shift, int size) {
        return new PdbCompactFragment(name, residues.subList(shift, shift + size));
    }

    public Set<TorsionAngleType> commonTorsionAngleTypes() {
        Set<TorsionAngleType> set = new LinkedHashSet<TorsionAngleType>();
        for (Entry<PdbResidue, List<TorsionAngleValue>> entry : mapResidueAngleValue.entrySet()) {
            for (TorsionAngleValue angleValue : entry.getValue()) {
                set.add(angleValue.getAngleType());
            }
        }
        return set;
    }

    public TorsionAngleValue getTorsionAngleValue(PdbResidue residue,
            MasterTorsionAngleType masterType) {
        Collection<? extends TorsionAngleType> angleTypes = masterType.getAngleTypes();
        for (TorsionAngleValue angleValue : mapResidueAngleValue.get(residue)) {
            for (TorsionAngleType angleType : angleTypes) {
                if (angleType.equals(angleValue.getAngleType())) {
                    return angleValue;
                }
            }
        }

        TorsionAngleType first = angleTypes.iterator().next();
        return TorsionAngleValue.invalidInstance(first);
    }

    public MoleculeType getMoleculeType() {
        // in compact fragment, all residues have the same molecule type
        return residues.get(0).getMoleculeType();
    }

    public int size() {
        return residues.size();
    }
}
