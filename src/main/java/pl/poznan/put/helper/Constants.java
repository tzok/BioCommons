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

    public static final List<Color> COLORS = new ArrayList<>();

    private static final String[] COLORS_HEX = new String[] { "#b20000", "#bf9f60", "#00e61f", "#0061f2", "#800066", "#590000", "#332b1a", "#60bf6c", "#001f4d", "#bf30a3", "#cc6666", "#ffeabf", "#00f2a2", "#3370cc", "#4d1341", "#332626", "#997a00", "#009966", "#26364d", "#f279da", "#e56739", "#ccc233", "#004d33", "#738299", "#ff00aa", "#8c3f23", "#4c4a26", "#003322", "#23318c", "#80406a", "#ffa280", "#999673", "#b6f2de", "#8f96bf", "#40303a", "#664133", "#f2ff40", "#7ca698", "#3030bf", "#330d21", "#f2c6b6", "#61661a", "#1d7362", "#1f0073", "#d9a3bf", "#ff6600", "#a0a653", "#36d9ce", "#a280ff", "#664d5a", "#592400", "#cfe673", "#003033", "#6953a6", "#590024", "#331400", "#99e600", "#1d6d73", "#201a33", "#992654", "#e57e39", "#def2b6", "#0085a6", "#c6b6f2", "#f279aa", "#bf8660", "#42a600", "#004759", "#534d66", "#bf0033", "#ff8800", "#143300", "#6cc3d9", "#1a0040", "#7f0022", "#402200", "#364d26", "#739199", "#3e2d59", "#f23d6d", "#cc8533", "#185900", "#39494d", "#8100f2", "#ff0022", "#8c5b23", "#a1ff80", "#002233", "#70008c", "#401016", "#8c7c69", "#518040", "#40bfff", "#b836d9", "#66333a", "#594f43", "#648060", "#40a6ff", "#eeb6f2", "#f2b6be", "#4c3300", "#324030", "#205380", "#967399", "#997378", "#f2b63d", "#8fbf8f", "#b6d6f2", "#ff00ee" };

    static {
        for (String hex : Constants.COLORS_HEX) {
            Constants.COLORS.add(Color.decode(hex));
        }
    }

    private Constants() {
    }
}
