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
public class CommonNumberFormat extends NumberFormat {
    private static final CommonNumberFormat INSTANCE = new CommonNumberFormat();

    public static CommonNumberFormat createInstance() {
        return CommonNumberFormat.INSTANCE;
    }

    /**
     * Format the number such that at most three digits in fractional part are
     * exposed.
     */
    public static String formatDouble(double value) {
        return CommonNumberFormat.INSTANCE.numberFormat.format(value);
    }

    private final NumberFormat numberFormat;

    @Override
    public StringBuffer format(double number, StringBuffer toAppendTo,
            FieldPosition pos) {
        return numberFormat.format(number, toAppendTo, pos);
    }

    @Override
    public StringBuffer format(long number, StringBuffer toAppendTo,
            FieldPosition pos) {
        return numberFormat.format(number, toAppendTo, pos);
    }

    @Override
    public Number parse(String source, ParsePosition parsePosition) {
        return numberFormat.parse(source, parsePosition);
    }

    private CommonNumberFormat() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        numberFormat = new DecimalFormat("###.###", symbols);
    }
}
