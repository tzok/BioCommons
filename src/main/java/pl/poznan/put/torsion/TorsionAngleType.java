package pl.poznan.put.torsion;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.poznan.put.interfaces.DisplayableExportable;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbResidue;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@XmlRootElement
public abstract class TorsionAngleType implements DisplayableExportable {
  private static final TorsionAngleType INVALID_INSTANCE =
      new TorsionAngleType(MoleculeType.UNKNOWN) {
        @Override
        public String getLongDisplayName() {
          return "Invalid";
        }

        @Override
        public String getShortDisplayName() {
          return "Invalid";
        }

        @Override
        public String getExportName() {
          return "Invalid";
        }

        @Override
        public TorsionAngleValue calculate(
            final List<PdbResidue> residues, final int currentIndex) {
          return TorsionAngleValue.invalidInstance(this);
        }
      };

  @XmlElement private MoleculeType moleculeType;

  protected TorsionAngleType(final MoleculeType moleculeType) {
    super();
    this.moleculeType = moleculeType;
  }

  public static TorsionAngleType invalidInstance() {
    return TorsionAngleType.INVALID_INSTANCE;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if ((o == null) || (getClass() != o.getClass())) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    final TorsionAngleType other = (TorsionAngleType) o;
    return moleculeType == other.moleculeType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), moleculeType);
  }

  public abstract TorsionAngleValue calculate(List<PdbResidue> residues, int currentIndex);
}
