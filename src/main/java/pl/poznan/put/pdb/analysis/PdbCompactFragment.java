package pl.poznan.put.pdb.analysis;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.poznan.put.pdb.ChainNumberICode;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.torsion.MasterTorsionAngleType;
import pl.poznan.put.torsion.TorsionAngleType;
import pl.poznan.put.torsion.TorsionAngleValue;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@XmlRootElement
public class PdbCompactFragment implements ResidueCollection {
  @XmlElement private String name;

  @XmlElement
  private Map<PdbResidueIdentifier, List<TorsionAngleValue>> mapResidueAngleValue =
      new LinkedHashMap<>();

  @XmlTransient private List<PdbResidue> residues;

  public PdbCompactFragment(final String name, final List<PdbResidue> residues) {
    super();
    this.name = name;
    this.residues = new ArrayList<>(residues);

    for (int i = 0; i < residues.size(); i++) {
      final PdbResidue residue = residues.get(i);
      final List<TorsionAngleValue> values = new ArrayList<>();

      for (final TorsionAngleType type : residue.getTorsionAngleTypes()) {
        final TorsionAngleValue value = type.calculate(residues, i);
        values.add(value);
      }

      mapResidueAngleValue.put(residue.getResidueIdentifier(), values);
    }
  }

  public final String toPdb() {
    final StringBuilder builder = new StringBuilder();
    for (final PdbResidue residue : residues) {
      builder.append(residue.toPdb());
    }
    return builder.toString();
  }

  public final String toSequence() {
    final StringBuilder builder = new StringBuilder();
    for (final PdbResidue residue : residues) {
      builder.append(residue.getOneLetterName());
    }
    return builder.toString();
  }

  public final PdbCompactFragment shift(final int shift, final int size) {
    return new PdbCompactFragment(name, residues.subList(shift, shift + size));
  }

  public final Set<TorsionAngleType> commonTorsionAngleTypes() {
    final Set<TorsionAngleType> set = new LinkedHashSet<>();
    mapResidueAngleValue
        .entrySet()
        .stream()
        .map(Map.Entry::getValue)
        .flatMap(Collection::stream)
        .map(TorsionAngleValue::getAngleType)
        .forEach(set::add);
    return set;
  }

  public final TorsionAngleValue getTorsionAngleValue(
      final ChainNumberICode chainNumberICode, final MasterTorsionAngleType masterType) {
    final Collection<? extends TorsionAngleType> angleTypes = masterType.getAngleTypes();

    for (final TorsionAngleValue angleValue :
        mapResidueAngleValue.get(chainNumberICode.getResidueIdentifier())) {
      for (final TorsionAngleType angleType : angleTypes) {
        if (Objects.equals(angleType, angleValue.getAngleType())) {
          return angleValue;
        }
      }
    }

    final TorsionAngleType first = angleTypes.iterator().next();
    return TorsionAngleValue.invalidInstance(first);
  }

  public final MoleculeType getMoleculeType() {
    // in compact fragment, all residues have the same molecule type
    return residues.get(0).getMoleculeType();
  }

  @Override
  public final PdbResidue findResidue(
      final String chainIdentifier, final int residueNumber, final String insertionCode) {
    return findResidue(new PdbResidueIdentifier(chainIdentifier, residueNumber, insertionCode));
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

  @Override
  public final boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if ((o == null) || (getClass() != o.getClass())) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    final PdbCompactFragment other = (PdbCompactFragment) o;
    return Objects.equals(name, other.name) && Objects.equals(residues, other.residues);
  }

  @Override
  public final int hashCode() {
    return Objects.hash(super.hashCode(), name, residues);
  }

  @Override
  public final String toString() {
    final PdbResidue first = residues.get(0);
    final PdbResidue last = residues.get(residues.size() - 1);
    return first + " - " + last + " (count: " + residues.size() + ')';
  }
}
