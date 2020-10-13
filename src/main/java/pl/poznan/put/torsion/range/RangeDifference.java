package pl.poznan.put.torsion.range;

/** A distance between {@link TorsionRange} objects. */
public enum RangeDifference {
  EQUAL(0),
  SIMILAR(1),
  DIFFERENT(2),
  OPPOSITE(3),
  INVALID(-1);

  private final int value;

  RangeDifference(final int value) {
    this.value = value;
  }

  /**
   * Parses value 0, 1, 2, or 3 as EQUAL, SIMILAR, DIFFERENT and OPPOSITE respectively. Value
   * different to 0, 1, 2 or 3 results in INVALID.
   *
   * @param value A value of 0, 1, 2 or 3 (all other are treated as invalid).
   * @return An instance of this enum.
   */
  public static RangeDifference fromValue(final int value) {
    switch (value) {
      case 0:
        return RangeDifference.EQUAL;
      case 1:
        return RangeDifference.SIMILAR;
      case 2:
        return RangeDifference.DIFFERENT;
      case 3:
        return RangeDifference.OPPOSITE;
      default:
        return RangeDifference.INVALID;
    }
  }

  /** @return 0 for EQUAL, 1 for SIMILAR, 2 for DIFFERENT, 3 for OPPOSITE and -1 for INVALID. */
  public int getValue() {
    return value;
  }
}
