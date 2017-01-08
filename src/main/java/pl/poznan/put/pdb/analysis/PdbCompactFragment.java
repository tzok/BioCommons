package pl.poznan.put.pdb.analysis;

import org.apache.commons.collections4.CollectionUtils;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.torsion.MasterTorsionAngleType;
import pl.poznan.put.torsion.TorsionAngleType;
import pl.poznan.put.torsion.TorsionAngleValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class PdbCompactFragment implements ResidueCollection {
    private final String name;
    private final List<PdbResidue> residues;
    private final Map<PdbResidue, List<TorsionAngleValue>>
            mapResidueAngleValue = new LinkedHashMap<>();

    public PdbCompactFragment(
            final String name, final List<PdbResidue> residues) {
        super();
        this.name = name;
        this.residues = new ArrayList<>(residues);

        for (int i = 0; i < residues.size(); i++) {
            PdbResidue residue = residues.get(i);
            List<TorsionAngleValue> values = new ArrayList<>();

            for (final TorsionAngleType type : residue.getTorsionAngleTypes()) {
                TorsionAngleValue value = type.calculate(residues, i);
                values.add(value);
            }

            mapResidueAngleValue.put(residue, values);
        }
    }

    public final String getName() {
        return name;
    }

    @Override
    public final List<PdbResidue> getResidues() {
        return Collections.unmodifiableList(residues);
    }

    @Override
    public final PdbResidue findResidue(
            final String chainIdentifier, final int residueNumber,
            final String insertionCode) {
        return findResidue(
                new PdbResidueIdentifier(chainIdentifier, residueNumber,
                                         insertionCode));
    }

    @Override
    public final PdbResidue findResidue(final PdbResidueIdentifier query) {
        for (final PdbResidue residue : residues) {
            if (Objects.equals(query, residue.getResidueIdentifier())) {
                return residue;
            }
        }
        throw new IllegalArgumentException("Failed to find residue: " + query);
    }

    public final String toPdb() {
        StringBuilder builder = new StringBuilder();
        for (final PdbResidue residue : residues) {
            builder.append(residue.toPdb());
        }
        return builder.toString();
    }

    public final String toSequence() {
        StringBuilder builder = new StringBuilder();
        for (final PdbResidue residue : residues) {
            builder.append(residue.getOneLetterName());
        }
        return builder.toString();
    }

    public final PdbCompactFragment shift(final int shift, final int size) {
        return new PdbCompactFragment(name,
                                      residues.subList(shift, shift + size));
    }

    public final Set<TorsionAngleType> commonTorsionAngleTypes() {
        Set<TorsionAngleType> set = new LinkedHashSet<>();
        for (final Map.Entry<PdbResidue, List<TorsionAngleValue>> entry :
                mapResidueAngleValue
                .entrySet()) {
            for (final TorsionAngleValue angleValue : entry.getValue()) {
                set.add(angleValue.getAngleType());
            }
        }
        return set;
    }

    public final TorsionAngleValue getTorsionAngleValue(
            final PdbResidue residue, final MasterTorsionAngleType masterType) {
        Collection<? extends TorsionAngleType> angleTypes =
                masterType.getAngleTypes();
        for (final TorsionAngleValue angleValue : mapResidueAngleValue
                .get(residue)) {
            for (final TorsionAngleType angleType : angleTypes) {
                if (Objects.equals(angleType, angleValue.getAngleType())) {
                    return angleValue;
                }
            }
        }

        TorsionAngleType first = angleTypes.iterator().next();
        return TorsionAngleValue.invalidInstance(first);
    }

    public final MoleculeType getMoleculeType() {
        // in compact fragment, all residues have the same molecule type
        return residues.get(0).getMoleculeType();
    }

    public final int size() {
        return residues.size();
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((name == null) ? 0 : name.hashCode());
        result = (prime * result) + ((residues == null) ? 0
                                                        : residues.hashCode());
        return result;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PdbCompactFragment other = (PdbCompactFragment) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!Objects.equals(name, other.name)) {
            return false;
        }
        if (residues == null) {
            if (other.residues != null) {
                return false;
            }
        } else if (!CollectionUtils
                .isEqualCollection(residues, other.residues)) {
            return false;
        }
        return true;
    }

    @Override
    public final String toString() {
        PdbResidue first = residues.get(0);
        PdbResidue last = residues.get(residues.size() - 1);
        return first + " - " + last + " (count: " + residues.size() + ')';
    }
}
