package pl.poznan.put.torsion;

import java.util.List;
import lombok.Data;
import pl.poznan.put.interfaces.DisplayableExportable;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbResidue;

@Data
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

  private final MoleculeType moleculeType;

  public static TorsionAngleType invalidInstance() {
    return TorsionAngleType.INVALID_INSTANCE;
  }

  public abstract TorsionAngleValue calculate(List<PdbResidue> residues, int currentIndex);
}
