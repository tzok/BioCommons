package pl.poznan.put.helper;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public final class Constants {
    public static final String UNICODE_ALPHA = "\u03B1";
    public static final String UNICODE_BETA = "\u03B2";
    public static final String UNICODE_GAMMA = "\u03B3";
    public static final String UNICODE_DELTA = "\u03B4";
    public static final String UNICODE_EPSILON = "\u03B5";
    public static final String UNICODE_ZETA = "\u03B6";
    public static final String UNICODE_CHI = "\u03C7";
    public static final String UNICODE_TAU = "\u03C4";
    public static final String UNICODE_TAU0 = "\u03C40";
    public static final String UNICODE_TAU1 = "\u03C41";
    public static final String UNICODE_TAU2 = "\u03C42";
    public static final String UNICODE_TAU3 = "\u03C43";
    public static final String UNICODE_TAU4 = "\u03C44";
    public static final String UNICODE_ETA = "\u03B7";
    public static final String UNICODE_ETA_PRIM = "\u03B7'";
    public static final String UNICODE_THETA = "\u03B8";
    public static final String UNICODE_THETA_PRIM = "\u03B8'";
    public static final String UNICODE_PHI = "\u03D5";
    public static final String UNICODE_PSI = "\u03C8";
    public static final String UNICODE_OMEGA = "\u03C9";
    public static final String UNICODE_CHI1 = "\u03C71";
    public static final String UNICODE_CHI2 = "\u03C72";
    public static final String UNICODE_CHI3 = "\u03C73";
    public static final String UNICODE_CHI4 = "\u03C74";
    public static final String UNICODE_CHI5 = "\u03C75";
    public static final String UNICODE_CALPHA = "C\u03B1";
    public static final String UNICODE_DEGREE = "\u00B0";
    public static final String UNICODE_ANGSTROM = "\u00C5";
    public static final String UNICODE_PI = "\u03C0";

    public static final List<Color> COLORS = new ArrayList<Color>();

    static {
        float hueRed = 0.0f;
        float hueYellow = 0.25f;
        float hueGreen = 0.5f;
        float hueBlue = 0.75f;

        for (int i = 0; i < 5; i++) {
            Constants.COLORS.add(Color.getHSBColor(hueRed, 0.5f, 0.5f));
            Constants.COLORS.add(Color.getHSBColor(hueYellow, 0.5f, 0.5f));
            Constants.COLORS.add(Color.getHSBColor(hueGreen, 0.5f, 0.5f));
            Constants.COLORS.add(Color.getHSBColor(hueBlue, 0.5f, 0.5f));

            hueRed += 0.05;
            hueYellow += 0.05;
            hueGreen += 0.05;
            hueBlue += 0.05;
        }
    }

    public static List<RGB> colorsAsRGB() {
        List<RGB> result = new ArrayList<RGB>();
        for (Color c : Constants.COLORS) {
            result.add(RGB.newInstance(c.getRGBColorComponents(null)));
        }
        return result;
    }

    private Constants() {
    }
}
