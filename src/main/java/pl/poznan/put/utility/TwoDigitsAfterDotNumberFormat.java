package pl.poznan.put.utility;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import org.apache.commons.math3.util.Precision;

/**
 * A NumberFormat extension and a suitable static method to format numbers leaving at most three
 * digits in the fractional part.
 *
 * @author tzok
 */
public final class TwoDigitsAfterDotNumberFormat extends NumberFormat {
  private static final long serialVersionUID = 1131105371632338733L;
  private static final TwoDigitsAfterDotNumberFormat INSTANCE = new TwoDigitsAfterDotNumberFormat();

  private final NumberFormat numberFormat;

  private TwoDigitsAfterDotNumberFormat() {
    super();
    final DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    symbols.setDecimalSeparator('.');
    numberFormat = new DecimalFormat("###.##", symbols);
  }

  public static String formatDouble(final double value) {
    return TwoDigitsAfterDotNumberFormat.INSTANCE.numberFormat.format(Precision.round(value, 2));
  }

  @Override
  public StringBuffer format(
      final double v, final StringBuffer stringBuffer, final FieldPosition fieldPosition) {
    return numberFormat.format(v, stringBuffer, fieldPosition);
  }

  @Override
  public StringBuffer format(
      final long l, final StringBuffer stringBuffer, final FieldPosition fieldPosition) {
    return numberFormat.format(l, stringBuffer, fieldPosition);
  }

  @Override
  public Number parse(final String s, final ParsePosition parsePosition) {
    return numberFormat.parse(s, parsePosition);
  }
}
