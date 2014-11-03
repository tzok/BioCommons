package pl.poznan.put.structure;

import java.util.Iterator;
import java.util.List;

import org.biojava.bio.structure.Group;

import pl.poznan.put.common.ResidueType;
import pl.poznan.put.torsion.AngleValue;
import pl.poznan.put.torsion.ChiTorsionAngle;
import pl.poznan.put.torsion.ChiTorsionAngleType;
import pl.poznan.put.torsion.TorsionAngle;

public class ResidueAngles implements Iterable<AngleValue> {
    private final CompactFragment fragment;
    private final Group group;
    private final ResidueType residueType;
    private final List<AngleValue> angles;

    public ResidueAngles(CompactFragment fragment, Group group, ResidueType residueType, List<AngleValue> angles) {
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

    public AngleValue getAngleValue(TorsionAngle torsionAngle) {
        for (AngleValue angleValue : angles) {
            TorsionAngle angle = angleValue.getAngle();

            if (torsionAngle instanceof ChiTorsionAngleType) {
                ChiTorsionAngleType type = (ChiTorsionAngleType) torsionAngle;

                if (angle instanceof ChiTorsionAngle && ((ChiTorsionAngle) angle).getType() == type) {
                    return angleValue;
                }
            } else if (angle.equals(torsionAngle)) {
                return angleValue;
            }
        }

        return AngleValue.getInvalidInstance(torsionAngle);
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

    @Override
    public Iterator<AngleValue> iterator() {
        return angles.iterator();
    }
}
