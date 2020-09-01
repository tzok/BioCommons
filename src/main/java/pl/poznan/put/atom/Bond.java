package pl.poznan.put.atom;

import org.apache.commons.collections4.map.MultiKeyMap;

/**
 * A utility class to work with atomic bonds' lengths. Data parsed from Charmm36 topology &amp;
 * parameter files.
 */
public final class Bond {
  private static final MultiKeyMap<AtomType, BondLength> MAP = new MultiKeyMap<>();
  private static final BondLength INVALID =
      ImmutableBondLength.of(
          Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

  static {
    Bond.MAP.put(AtomType.C, AtomType.C, ImmutableBondLength.of(1.320, 1.538, 1.463));
    Bond.MAP.put(AtomType.C, AtomType.H, ImmutableBondLength.of(1.070, 1.111, 1.098));
    Bond.MAP.put(AtomType.C, AtomType.N, ImmutableBondLength.of(1.300, 1.502, 1.396));
    Bond.MAP.put(AtomType.C, AtomType.O, ImmutableBondLength.of(1.205, 1.480, 1.359));
    Bond.MAP.put(AtomType.C, AtomType.S, ImmutableBondLength.of(1.816, 1.836, 1.820));
    Bond.MAP.put(AtomType.H, AtomType.C, ImmutableBondLength.of(1.070, 1.111, 1.098));
    Bond.MAP.put(AtomType.H, AtomType.N, ImmutableBondLength.of(0.976, 1.040, 1.005));
    Bond.MAP.put(AtomType.H, AtomType.O, ImmutableBondLength.of(0.960, 0.960, 0.960));
    Bond.MAP.put(AtomType.H, AtomType.S, ImmutableBondLength.of(1.325, 1.325, 1.325));
    Bond.MAP.put(AtomType.N, AtomType.C, ImmutableBondLength.of(1.300, 1.502, 1.396));
    Bond.MAP.put(AtomType.N, AtomType.H, ImmutableBondLength.of(0.976, 1.040, 1.005));
    Bond.MAP.put(AtomType.O, AtomType.C, ImmutableBondLength.of(1.205, 1.480, 1.359));
    Bond.MAP.put(AtomType.O, AtomType.H, ImmutableBondLength.of(0.960, 0.960, 0.960));
    Bond.MAP.put(AtomType.O, AtomType.P, ImmutableBondLength.of(1.480, 1.600, 1.553));
    Bond.MAP.put(AtomType.P, AtomType.O, ImmutableBondLength.of(1.480, 1.600, 1.553));
    Bond.MAP.put(AtomType.S, AtomType.C, ImmutableBondLength.of(1.816, 1.836, 1.820));
    Bond.MAP.put(AtomType.S, AtomType.H, ImmutableBondLength.of(1.325, 1.325, 1.325));
    Bond.MAP.put(AtomType.S, AtomType.S, ImmutableBondLength.of(2.029, 2.029, 2.029));
  }

  private Bond() {
    super();
  }

  /**
   * Return bond length between two atoms.
   *
   * @param left Type of first atom.
   * @param right Type of second atom.
   * @return An instance of {@link BondLength}.
   */
  public static BondLength length(final AtomType left, final AtomType right) {
    BondLength bondLength = Bond.MAP.get(left, right);

    if (bondLength == null) {
      bondLength = Bond.MAP.get(right, left);
    }

    if (bondLength == null) {
      bondLength = Bond.INVALID;
    }

    return bondLength;
  }
}
