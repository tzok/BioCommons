package pl.poznan.put.notation;

import java.util.Objects;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.pdb.analysis.ResidueInformationProvider;
import pl.poznan.put.rna.base.NucleobaseType;

/**
 * Base-ribose notation. Zirbel, C. L., et al (2009). Classification and energetics of the
 * base-phosphate interactions in RNA. Nucleic Acids Research, 37(15), 4898–4918.
 * http://doi.org/10.1093/nar/gkp468
 */
public enum BR {
  _0("0BR", "n0BR", "0RB", "n0RB"),
  _1("1BR", "n1BR", "1RB", "n1RB"),
  _2("2BR", "n2BR", "2RB", "n2RB"),
  _3("3BR", "n3BR", "3RB", "n3RB"),
  _4("4BR", "n4BR", "4RB", "n4RB"),
  _5("5BR", "n5BR", "5RB", "n5RB"),
  _6("6BR", "n6BR", "6RB", "n6RB"),
  _7("7BR", "n7BR", "7RB", "n7RB"),
  _8("8BR", "n8BR", "8RB", "n8RB"),
  _9("9BR", "n9BR", "9RB", "n9RB"),
  UNKNOWN("UNKNOWN");

  private final String[] displayNames;

  BR(final String... displayNames) {
    this.displayNames = displayNames;
  }

  public String getDisplayName() {
    return displayNames[0];
  }

  public static BR detect(final PdbResidue base, final PdbResidue ribose) {
    final ResidueInformationProvider provider = base.getResidueInformationProvider();
    if (!(provider instanceof NucleobaseType)) {
      throw new IllegalArgumentException("Provided residue is not a nucleotide");
    }

    final NucleobaseType nucleobaseType = (NucleobaseType) provider;
    switch (nucleobaseType) {
      case ADENINE:
        return BR.detectForAdenine(base, ribose);
      case CYTOSINE:
        return BR.detectForCytosine(base, ribose);
      case GUANINE:
        return BR.detectForGuanine(base, ribose);
      case URACIL:
        return BR.detectForUracil(base, ribose);
      case THYMINE:
      default:
        throw new IllegalArgumentException("Only RNA nucleotides are supported");
    }
  }

  private static BR detectForUracil(final PdbResidue base, final PdbResidue ribose) {
    // FIXME: implement this
    return BR.UNKNOWN;
  }

  private static BR detectForGuanine(final PdbResidue base, final PdbResidue ribose) {
    // FIXME: implement this
    return BR.UNKNOWN;
  }

  private static BR detectForCytosine(final PdbResidue base, final PdbResidue ribose) {
    // FIXME: implement this
    return BR.UNKNOWN;
  }

  private static BR detectForAdenine(final PdbResidue base, final PdbResidue ribose) {
    // FIXME: implement this
    return BR.UNKNOWN;
  }

  public static BR fromString(final String candidate) {
    for (final BR br : BR.values()) {
      for (final String displayName : br.displayNames) {
        if (Objects.equals(displayName, candidate)) {
          return br;
        }
      }
    }
    return BR.UNKNOWN;
  }
}
