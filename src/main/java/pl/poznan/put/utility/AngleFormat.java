package pl.poznan.put.utility;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

import org.apache.commons.math3.fraction.ProperFractionFormat;

import pl.poznan.put.constant.Unicode;

public class AngleFormat extends NumberFormat {
    private static final AngleFormat INSTANCE = new AngleFormat();

    public static AngleFormat createInstance() {
        return AngleFormat.INSTANCE;
    }

    public static String formatDisplayLong(double radians) {
        if (Double.isNaN(radians)) {
            return "NaN";
        }
        if (Double.isInfinite(radians)) {
            return Unicode.INFINITY;
        }
        return AngleFormat.INSTANCE.format(radians);
    }

    public static String formatDisplayShort(double radians) {
        if (Double.isNaN(radians)) {
            return "NaN";
        }
        if (Double.isInfinite(radians)) {
            return Unicode.INFINITY;
        }
        return CommonNumberFormat.formatDouble(Math.toDegrees(radians)) + Unicode.DEGREE;
    }

    public static String formatExport(double radians) {
        return Double.toString(Math.toDegrees(radians));
    }

    private final ProperFractionFormat fractionFormat = new ProperFractionFormat();

    @Override
    public StringBuffer format(double number, StringBuffer toAppendTo,
            FieldPosition pos) {
        if (number == 0) {
            return toAppendTo.append("0");
        } else if (number == Math.PI) {
            toAppendTo.append(Unicode.PI);
            toAppendTo.append(" = 180");
            toAppendTo.append(Unicode.DEGREE);
            return toAppendTo;
        }

        fractionFormat.format(number / Math.PI, toAppendTo, pos);
        toAppendTo.append(" * ");
        toAppendTo.append(Unicode.PI);
        toAppendTo.append(" = ");
        toAppendTo.append(Math.round(Math.toDegrees(number)));
        toAppendTo.append(Unicode.DEGREE);
        return toAppendTo;
    }

    @Override
    public StringBuffer format(long number, StringBuffer toAppendTo,
            FieldPosition pos) {
        return fractionFormat.format(number, toAppendTo, pos);
    }

    @Override
    public Number parse(String source, ParsePosition parsePosition) {
        return fractionFormat.parse(source, parsePosition);
    }

    private AngleFormat() {
    }
}