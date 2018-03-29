package pl.poznan.put.circular;

import java.io.Serializable;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.math3.util.MathUtils;
import pl.poznan.put.circular.enums.ValueType;
import pl.poznan.put.utility.AngleFormat;
import pl.poznan.put.utility.TwoDigitsAfterDotNumberFormat;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public abstract class Circular implements Comparable<Circular>, Serializable {
  private static final long serialVersionUID = -4674646476160594025L;

  @XmlElement private final double radians;

  protected Circular(final double value, final ValueType valueType) {
    super();
    radians = valueType.toRadians(value);
  }

  public final double getRadians() {
    return radians;
  }

  public final double getDegrees() {
    return Math.toDegrees(radians);
  }

  public final double getDegrees360() {
    return Math.toDegrees(getRadians2PI());
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
    return Double.compare(circular.radians, radians) == 0;
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
          TwoDigitsAfterDotNumberFormat.formatDouble(radians),
          AngleFormat.degreesRoundedToHundredth(radians));
    }
    return "invalid";
  }

  public final boolean isValid() {
    return !Double.isNaN(radians);
  }

  @Override
  public final int compareTo(final Circular t) {
    if (equals(t)) {
      return 0;
    }
    return Double.compare(radians, t.radians);
  }
}
