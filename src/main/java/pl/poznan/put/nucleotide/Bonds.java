package pl.poznan.put.nucleotide;

import org.apache.commons.collections4.map.MultiKeyMap;

/*
 * Values are calculated from Charm36 parameters file
 */
public class Bonds {
    public static class Length {
        private final double min;
        private final double max;
        private final double avg;

        private Length(double min, double max, double avg) {
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

    private static final MultiKeyMap<AtomType, Bonds.Length> MAP =
            new MultiKeyMap<AtomType, Bonds.Length>();
    private static final Bonds.Length INVALID = new Length(
            Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY,
            Double.POSITIVE_INFINITY);

    static {
        Bonds.MAP.put(AtomType.C, AtomType.C, new Length(1.32, 1.529, 1.4428));
        Bonds.MAP.put(AtomType.C, AtomType.H, new Length(1.09, 1.111, 1.0312));
        Bonds.MAP.put(AtomType.C, AtomType.N, new Length(1.305, 1.48, 1.37917));
        Bonds.MAP.put(AtomType.C, AtomType.O, new Length(1.23, 1.48, 1.39594));
        // there are no bonds C-P

        // bond H-C already covered above
        // there are no bonds H-H
        Bonds.MAP.put(AtomType.H, AtomType.N, new Length(1.0, 1.04, 1.01333));
        Bonds.MAP.put(AtomType.H, AtomType.O, new Length(0.96, 0.96, 0.96));
        // there are no bonds H-P

        // bond N-C already covered above
        // bond N-H already covered above
        // there are no bonds N-N
        // there are no bonds N-O
        // there are no bonds N-P

        // bond O-C already covered above
        // bond O-H already covered above
        // bond O-N already covered above
        // there are no bonds O-O
        Bonds.MAP.put(AtomType.O, AtomType.P, new Length(1.48, 1.6, 1.5533));

        // bond P-C already covered above
        // bond P-H already covered above
        // bond P-N already covered above
        // bond P-O already covered above
        // there are no bonds P-P
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
