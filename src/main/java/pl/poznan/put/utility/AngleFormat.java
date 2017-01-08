package pl.poznan.put.utility;

import org.apache.commons.math3.fraction.ProperFractionFormat;
import org.apache.commons.math3.util.Precision;
import pl.poznan.put.constant.Unicode;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

public class AngleFormat extends NumberFormat {
    private static final AngleFormat INSTANCE = new AngleFormat();
    private static final long serialVersionUID = -5889250902960438432L;
    private final ProperFractionFormat fractionFormat =
            new ProperFractionFormat();

    private AngleFormat() {
        super();
    }

    public static AngleFormat createInstance() {
        return AngleFormat.INSTANCE;
    }

    public static String formatDisplayLong(final double radians) {
        if (Double.isNaN(radians)) {
            return "NaN";
        }
        if (Double.isInfinite(radians)) {
            return Unicode.INFINITY;
        }
        return AngleFormat.INSTANCE.format(radians);
    }

    public static String formatDisplayShort(final double radians) {
        if (Double.isNaN(radians)) {
            return "NaN";
        }
        if (Double.isInfinite(radians)) {
            return Unicode.INFINITY;
        }
        return CommonNumberFormat.formatDouble(Math.toDegrees(radians))
               + Unicode.DEGREE;
    }

    public static String formatExport(final double radians) {
        return Double.toString(Math.toDegrees(radians));
    }

    @Override
    public final StringBuffer format(final double v,
                                     final StringBuffer stringBuffer,
                                     final FieldPosition fieldPosition) {
        if (Precision.equals(v, 0.0)) {
            return stringBuffer.append('0');
        }
        if (Precision.equals(v, Math.PI)) {
            stringBuffer.append(Unicode.PI);
            stringBuffer.append(" = 180");
            stringBuffer.append(Unicode.DEGREE);
            return stringBuffer;
        }

        fractionFormat.format(v / Math.PI, stringBuffer, fieldPosition);
        stringBuffer.append(" * ");
        stringBuffer.append(Unicode.PI);
        stringBuffer.append(" = ");
        stringBuffer.append(Math.round(Math.toDegrees(v)));
        stringBuffer.append(Unicode.DEGREE);
        return stringBuffer;
    }

    @Override
    public final StringBuffer format(final long l,
                                     final StringBuffer stringBuffer,
                                     final FieldPosition fieldPosition) {
        return fractionFormat.format(l, stringBuffer, fieldPosition);
    }

    @Override
    public final Number parse(final String s,
                              final ParsePosition parsePosition) {
        return fractionFormat.parse(s, parsePosition);
    }
}