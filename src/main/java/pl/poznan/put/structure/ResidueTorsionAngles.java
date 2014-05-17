package pl.poznan.put.structure;

import java.util.ArrayList;
import java.util.List;

import org.biojava.bio.structure.Group;

import pl.poznan.put.common.ChiTorsionAngle;
import pl.poznan.put.common.ChiTorsionAngleType;
import pl.poznan.put.common.ResidueType;
import pl.poznan.put.common.TorsionAngle;
import pl.poznan.put.common.TorsionAngleValue;

public class ResidueTorsionAngles {
    private final CompactFragment fragment;
    private final Group group;
    private final ResidueType residueType;
    private final List<TorsionAngleValue> angles;

    public ResidueTorsionAngles(CompactFragment fragment, Group group,
            ResidueType residueType, List<TorsionAngleValue> angles) {
        this.fragment = fragment;
        this.group = group;
        this.residueType = residueType;
        this.angles = angles;
    }

    public CompactFragment getFragment() {
        return fragment;
    }

    public Group getGroup() {
        return group;
    }

    public ResidueType getResidueType() {
        return residueType;
    }

    public List<TorsionAngleValue> getChiAngleValues() {
        List<TorsionAngleValue> result = new ArrayList<TorsionAngleValue>();

        for (TorsionAngle torsionAngle : residueType.getChiTorsionAngles()) {
            result.add(getAngleValue(torsionAngle));
        }

        return result;
    }

    public TorsionAngleValue getAngleValue(TorsionAngle torsionAngle) {
        for (TorsionAngleValue torsionAngleValue : angles) {
            if (torsionAngleValue.getTorsionAngle().equals(torsionAngle)) {
                return torsionAngleValue;
            }
        }
        return TorsionAngleValue.invalidInstance(torsionAngle);
    }

    public TorsionAngleValue getChiAngleValue(ChiTorsionAngleType angleType) {
        for (TorsionAngleValue torsionAngleValue : angles) {
            TorsionAngle torsionAngle = torsionAngleValue.getTorsionAngle();

            if (torsionAngle instanceof ChiTorsionAngle
                    && ((ChiTorsionAngle) torsionAngle).getType() == angleType) {
                return torsionAngleValue;
            }
        }
        return TorsionAngleValue.invalidInstance(angleType);
    }

    public int getSize() {
        return angles.size();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(Residue.fromGroup(group));

        for (TorsionAngleValue tav : angles) {
            builder.append('\t');
            builder.append(tav.getTorsionAngle());
            builder.append('\t');
            builder.append(Math.toDegrees(tav.getValue()));
        }

        return builder.toString();
    }
}
