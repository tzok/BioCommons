package pl.poznan.put.circular.samples;

import pl.poznan.put.circular.Angle;

class TrigonometricMoment {
  private final Angle meanDirection;
  private final double meanResultantLength;

  TrigonometricMoment(final Angle meanDirection, final double meanResultantLength) {
    super();
    this.meanDirection = meanDirection;
    this.meanResultantLength = meanResultantLength;
  }

  public final Angle getMeanDirection() {
    return meanDirection;
  }

  public final double getMeanResultantLength() {
    return meanResultantLength;
  }

  @Override
  public final String toString() {
    return "TrigonometricMoment [meanDirection="
        + meanDirection
        + ", meanResultantLength="
        + meanResultantLength
        + ']';
  }
}
