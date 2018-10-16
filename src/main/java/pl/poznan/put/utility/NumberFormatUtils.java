package pl.poznan.put.utility;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * A class to provide useful NumberFormat instances.
 *
 * @author tzok
 */
public final class NumberFormatUtils {
  private static final NumberFormat THREE_DECIMAL_DIGITS =
      new DecimalFormat(".###", new DecimalFormatSymbols(Locale.US));

  public static NumberFormat threeDecimalDigits() {
    return NumberFormatUtils.THREE_DECIMAL_DIGITS;
  }

  private NumberFormatUtils() {
    super();
  }
}
