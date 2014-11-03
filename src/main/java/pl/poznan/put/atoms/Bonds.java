package pl.poznan.put.atoms;

import org.apache.commons.collections4.map.MultiKeyMap;

/*
 * Values are calculated from Charm36 parameters file
 */
public class Bonds {
    public static class Length {
        private final double min;
        private final double max;
        private final double avg;

        Length(double min, double max, double avg) {
            super();
            this.min = min;
            this.max = max;
            this.avg = avg;
        }

        public double getMin() {
            return min;
        }

        public double getMax() {
            return max;
        }

        public double getAvg() {
            return avg;
        }
    }

    private static final MultiKeyMap<AtomType, Bonds.Length> MAP = new MultiKeyMap<AtomType, Length>();
    private static final Bonds.Length INVALID = new Length(
            Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY,
            Double.POSITIVE_INFINITY);

    static {
        Bonds.MAP.put(AtomType.C, AtomType.C, new Length(1.320, 1.538, 1.463));
        Bonds.MAP.put(AtomType.C, AtomType.H, new Length(1.070, 1.111, 1.098));
        Bonds.MAP.put(AtomType.C, AtomType.N, new Length(1.300, 1.502, 1.396));
        Bonds.MAP.put(AtomType.C, AtomType.O, new Length(1.205, 1.480, 1.359));
        Bonds.MAP.put(AtomType.C, AtomType.S, new Length(1.816, 1.836, 1.820));
        Bonds.MAP.put(AtomType.H, AtomType.C, new Length(1.070, 1.111, 1.098));
        Bonds.MAP.put(AtomType.H, AtomType.N, new Length(0.976, 1.040, 1.005));
        Bonds.MAP.put(AtomType.H, AtomType.O, new Length(0.960, 0.960, 0.960));
        Bonds.MAP.put(AtomType.H, AtomType.S, new Length(1.325, 1.325, 1.325));
        Bonds.MAP.put(AtomType.N, AtomType.C, new Length(1.300, 1.502, 1.396));
        Bonds.MAP.put(AtomType.N, AtomType.H, new Length(0.976, 1.040, 1.005));
        Bonds.MAP.put(AtomType.O, AtomType.C, new Length(1.205, 1.480, 1.359));
        Bonds.MAP.put(AtomType.O, AtomType.H, new Length(0.960, 0.960, 0.960));
        Bonds.MAP.put(AtomType.O, AtomType.P, new Length(1.480, 1.600, 1.553));
        Bonds.MAP.put(AtomType.P, AtomType.O, new Length(1.480, 1.600, 1.553));
        Bonds.MAP.put(AtomType.S, AtomType.C, new Length(1.816, 1.836, 1.820));
        Bonds.MAP.put(AtomType.S, AtomType.H, new Length(1.325, 1.325, 1.325));
        Bonds.MAP.put(AtomType.S, AtomType.S, new Length(2.029, 2.029, 2.029));
    }

    public static Bonds.Length length(AtomType left, AtomType right) {
        Length length = Bonds.MAP.get(left, right);

        if (length == null) {
            length = Bonds.MAP.get(right, left);
        }

        if (length == null) {
            length = Bonds.INVALID;
        }

        return length;
    }
}
