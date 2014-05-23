package pl.poznan.put.helper;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

import org.apache.commons.math3.fraction.ProperFractionFormat;

public class FractionAngleFormat extends NumberFormat {
    private static final FractionAngleFormat INSTANCE = new FractionAngleFormat();

    public static FractionAngleFormat createInstance() {
        return FractionAngleFormat.INSTANCE;
    }

    public static String formatDouble(double value) {
        return FractionAngleFormat.INSTANCE.format(value);
    }

    private final ProperFractionFormat fractionFormat = new ProperFractionFormat();

    @Override
    public StringBuffer format(double number, StringBuffer toAppendTo,
            FieldPosition pos) {
        if (number == 0) {
            return toAppendTo.append("0");
        } else if (number == Math.PI) {
            toAppendTo.append(Constants.UNICODE_PI);
            toAppendTo.append(" = 180");
            toAppendTo.append(Constants.UNICODE_DEGREE);
            return toAppendTo;
        }

        fractionFormat.format(number / Math.PI, toAppendTo, pos);
        toAppendTo.append(" * ");
        toAppendTo.append(Constants.UNICODE_PI);
        toAppendTo.append(" = ");
        toAppendTo.append(Math.round(Math.toDegrees(number)));
        toAppendTo.append(Constants.UNICODE_DEGREE);
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

    private FractionAngleFormat() {
    }
}