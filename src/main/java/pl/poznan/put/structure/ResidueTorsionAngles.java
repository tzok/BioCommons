package pl.poznan.put.structure;

import java.util.ArrayList;
import java.util.List;

import org.biojava.bio.structure.Group;

import pl.poznan.put.common.ResidueType;
import pl.poznan.put.torsion.AngleValue;
import pl.poznan.put.torsion.ChiTorsionAngle;
import pl.poznan.put.torsion.ChiTorsionAngleType;
import pl.poznan.put.torsion.TorsionAngle;

public class ResidueTorsionAngles {
    private final CompactFragment fragment;
    private final Group group;
    private final ResidueType residueType;
    private final List<AngleValue> angles;

    public ResidueTorsionAngles(CompactFragment fragment, Group group,
            ResidueType residueType, List<AngleValue> angles) {
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

    public int getSize() {
        return angles.size();
    }

    public AngleValue[] getChiAngleValues() {
        List<AngleValue> result = new ArrayList<AngleValue>();
        for (TorsionAngle torsionAngle : residueType.getChiTorsionAngles()) {
            result.add(getAngleValue(torsionAngle));
        }
        return result.toArray(new AngleValue[result.size()]);
    }

    public AngleValue getAngleValue(TorsionAngle torsionAngle) {
        for (AngleValue torsionAngleValue : angles) {
            if (torsionAngleValue.getAngle().equals(torsionAngle)) {
                return torsionAngleValue;
            }
        }
        return AngleValue.invalidInstance(torsionAngle);
    }

    public AngleValue getChiAngleValue(ChiTorsionAngleType angleType) {
        for (AngleValue torsionAngleValue : angles) {
            TorsionAngle torsionAngle = torsionAngleValue.getAngle();

            if (torsionAngle instanceof ChiTorsionAngle
                    && ((ChiTorsionAngle) torsionAngle).getType() == angleType) {
                return torsionAngleValue;
            }
        }
        return AngleValue.invalidInstance(angleType);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(Residue.fromGroup(group));

        for (AngleValue tav : angles) {
            builder.append('\t');
            builder.append(tav.getAngle());
            builder.append('\t');
            builder.append(Math.toDegrees(tav.getValue()));
        }

        return builder.toString();
    }
}
