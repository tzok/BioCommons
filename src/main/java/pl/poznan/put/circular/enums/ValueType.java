package pl.poznan.put.circular.enums;

/** Type of value passed as regular double. */
public enum ValueType {
  DEGREES,
  RADIANS;

  /**
   * Return value in radians. If it was given in radians, return it as it is. If it was given in
   * degrees, return {@link Math#toRadians(double)} result.
   *
   * @param value Value to be transformed.
   * @return Value in radians.
   */
  public double toRadians(final double value) {
    switch (this) {
      case DEGREES:
        return Math.toRadians(value);
      case RADIANS:
        return value;
      default:
        throw new IllegalArgumentException("Only degrees and radians are handled");
    }
  }
}
