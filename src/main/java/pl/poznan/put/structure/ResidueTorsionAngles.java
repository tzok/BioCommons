package pl.poznan.put.structure;

import java.util.List;

import org.biojava.bio.structure.Group;

import pl.poznan.put.common.ResidueType;
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

    public List<TorsionAngleValue> getAngles() {
        return angles;
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
