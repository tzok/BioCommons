package pl.poznan.put.utility;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

/**
 * A NumberFormat extension and a suitable static method to format numbers
 * leaving at most three digits in the fractional part.
 *
 * @author tzok
 */
public final class CommonNumberFormat extends NumberFormat {
    private static final long serialVersionUID = 1131105371632338733L;
    private static final CommonNumberFormat INSTANCE = new CommonNumberFormat();
    private final NumberFormat numberFormat;

    private CommonNumberFormat() {
        super();
        final DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        numberFormat = new DecimalFormat("###.##", symbols);
    }

    /**
     * Format the number such that at most three digits in fractional part are
     * exposed.
     */
    public static String formatDouble(final double value) {
        return CommonNumberFormat.INSTANCE.numberFormat.format(value);
    }

    @Override
    public StringBuffer format(final double v, final StringBuffer stringBuffer,
                               final FieldPosition fieldPosition) {
        return numberFormat.format(v, stringBuffer, fieldPosition);
    }

    @Override
    public StringBuffer format(final long l, final StringBuffer stringBuffer,
                               final FieldPosition fieldPosition) {
        return numberFormat.format(l, stringBuffer, fieldPosition);
    }

    @Override
    public Number parse(final String s, final ParsePosition parsePosition) {
        return numberFormat.parse(s, parsePosition);
    }
}
