package pl.poznan.put.atom;

import lombok.Data;
import org.apache.commons.collections4.map.MultiKeyMap;

/*
 * Values are taken from Charm36 parameters file
 */
public final class Bond {
  private static final MultiKeyMap<AtomType, Length> MAP = new MultiKeyMap<>();
  private static final Length INVALID =
      new Length(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

  static {
    Bond.MAP.put(AtomType.C, AtomType.C, new Length(1.320, 1.538, 1.463));
    Bond.MAP.put(AtomType.C, AtomType.H, new Length(1.070, 1.111, 1.098));
    Bond.MAP.put(AtomType.C, AtomType.N, new Length(1.300, 1.502, 1.396));
    Bond.MAP.put(AtomType.C, AtomType.O, new Length(1.205, 1.480, 1.359));
    Bond.MAP.put(AtomType.C, AtomType.S, new Length(1.816, 1.836, 1.820));
    Bond.MAP.put(AtomType.H, AtomType.C, new Length(1.070, 1.111, 1.098));
    Bond.MAP.put(AtomType.H, AtomType.N, new Length(0.976, 1.040, 1.005));
    Bond.MAP.put(AtomType.H, AtomType.O, new Length(0.960, 0.960, 0.960));
    Bond.MAP.put(AtomType.H, AtomType.S, new Length(1.325, 1.325, 1.325));
    Bond.MAP.put(AtomType.N, AtomType.C, new Length(1.300, 1.502, 1.396));
    Bond.MAP.put(AtomType.N, AtomType.H, new Length(0.976, 1.040, 1.005));
    Bond.MAP.put(AtomType.O, AtomType.C, new Length(1.205, 1.480, 1.359));
    Bond.MAP.put(AtomType.O, AtomType.H, new Length(0.960, 0.960, 0.960));
    Bond.MAP.put(AtomType.O, AtomType.P, new Length(1.480, 1.600, 1.553));
    Bond.MAP.put(AtomType.P, AtomType.O, new Length(1.480, 1.600, 1.553));
    Bond.MAP.put(AtomType.S, AtomType.C, new Length(1.816, 1.836, 1.820));
    Bond.MAP.put(AtomType.S, AtomType.H, new Length(1.325, 1.325, 1.325));
    Bond.MAP.put(AtomType.S, AtomType.S, new Length(2.029, 2.029, 2.029));
  }

  private Bond() {
    super();
  }

  /**
   * Return bond length between two atoms.
   *
   * @param left Type of first atom.
   * @param right Type of second atom.
   * @return An instance of {@link Length}.
   */
  public static Length length(final AtomType left, final AtomType right) {
    Length length = Bond.MAP.get(left, right);

    if (length == null) {
      length = Bond.MAP.get(right, left);
    }

    if (length == null) {
      length = Bond.INVALID;
    }

    return length;
  }

  @Data
  public static class Length {
    private final double min;
    private final double max;
    private final double avg;
  }
}
