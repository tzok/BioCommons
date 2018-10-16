package pl.poznan.put.circular;

import java.io.Serializable;
import java.util.Objects;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.Precision;
import pl.poznan.put.circular.enums.ValueType;
import pl.poznan.put.utility.AngleFormat;
import pl.poznan.put.utility.NumberFormatUtils;

public abstract class Circular implements Comparable<Circular>, Serializable {
  private static final long serialVersionUID = -4674646476160594025L;

  private final double radians;

  protected Circular(final double value, final ValueType valueType) {
    super();
    radians = valueType.toRadians(value);
  }

  public final double getRadians() {
    return radians;
  }

  public final double getDegrees() {
    return FastMath.toDegrees(radians);
  }

  public final double getDegrees360() {
    return FastMath.toDegrees(getRadians2PI());
  }

  public final double getRadians2PI() {
    return (radians < 0.0) ? (radians + MathUtils.TWO_PI) : radians;
  }

  @Override
  public final boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if ((o == null) || (getClass() != o.getClass())) {
      return false;
    }
    final Circular circular = (Circular) o;
    return Precision.equals(getRadians2PI(), circular.getRadians2PI(), 1.0e-3);
  }

  @Override
  public final int hashCode() {
    return Objects.hash(radians);
  }

  @Override
  public String toString() {
    if (isValid()) {
      return String.format(
          "%s rad, %s",
          NumberFormatUtils.threeDecimalDigits().format(radians),
          AngleFormat.degreesRoundedToHundredth(radians));
    }
    return "invalid";
  }

  public final boolean isValid() {
    return !Double.isNaN(radians);
  }

  @Override
  public final int compareTo(final Circular t) {
    return Double.compare(getRadians2PI(), t.getRadians2PI());
  }
}
