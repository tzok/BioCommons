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

  public int getValue() {
    return value;
  }
}
