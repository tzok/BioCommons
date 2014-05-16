package pl.poznan.put.structure;

import java.util.ArrayList;
import java.util.List;

import org.biojava.bio.structure.Atom;
import org.biojava.bio.structure.Group;

import pl.poznan.put.atoms.AtomName;
import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.common.ResidueType;
import pl.poznan.put.common.AtomsBasedTorsionAngle;
import pl.poznan.put.common.TorsionAngle;
import pl.poznan.put.common.TorsionAngleValue;
import pl.poznan.put.helper.StructureHelper;
import pl.poznan.put.helper.TorsionAnglesHelper;
import pl.poznan.put.helper.UniTypeQuadruplet;

public class CompactFragment {
    private final MoleculeType chainType;
    private final List<Group> residues = new ArrayList<Group>();
    private final List<ResidueTorsionAngles> torsionAngles = new ArrayList<ResidueTorsionAngles>();

    public CompactFragment(MoleculeType chainType) {
        super();
        this.chainType = chainType;
    }

    public MoleculeType getChainType() {
        return chainType;
    }

    public void addResidue(Group residue) {
        residues.add(residue);
    }

    public List<ResidueTorsionAngles> getTorsionAngles() {
        if (torsionAngles.size() != residues.size()) {
            calculateTorsionAngles();
        }

        return torsionAngles;
    }

    public int getSize() {
        return residues.size();
    }

    public static CompactFragment shift(CompactFragment origin, int bestShift,
            int size) {
        CompactFragment fragment = new CompactFragment(origin.chainType);

        for (int i = bestShift; i < bestShift + size; i++) {
            fragment.addResidue(origin.residues.get(i));
        }

        return fragment;
    }

    private void calculateTorsionAngles() {
        for (int i = 0; i < residues.size(); i++) {
            Group group = residues.get(i);
            ResidueType residueType = ResidueType.fromString(chainType,
                    group.getPDBName());

            if (residueType == ResidueType.UNKNOWN) {
                residueType = ResidueType.detect(group);
            }

            List<TorsionAngleValue> values = new ArrayList<TorsionAngleValue>();

            if (residueType != ResidueType.UNKNOWN) {
                for (TorsionAngle angle : residueType.getTorsionAngles()) {
                    if (angle instanceof AtomsBasedTorsionAngle) {
                        values.add(calculateTorsionAngle(
                                (AtomsBasedTorsionAngle) angle, i));
                    }
                }
            }

            ResidueTorsionAngles result = new ResidueTorsionAngles(this, group,
                    residueType, values);
            torsionAngles.add(result);
        }
    }

    private TorsionAngleValue calculateTorsionAngle(
            AtomsBasedTorsionAngle angle, int i) {
        UniTypeQuadruplet<Integer> residueRule = angle.getResidueRule();
        int a = i + residueRule.a;
        if (a < 0 || a >= residues.size()) {
            return TorsionAngleValue.invalidInstance(angle);
        }

        int b = i + residueRule.b;
        if (b < 0 || b >= residues.size()) {
            return TorsionAngleValue.invalidInstance(angle);
        }

        int c = i + residueRule.c;
        if (c < 0 || c >= residues.size()) {
            return TorsionAngleValue.invalidInstance(angle);
        }

        int d = i + residueRule.d;
        if (d < 0 || d >= residues.size()) {
            return TorsionAngleValue.invalidInstance(angle);
        }

        UniTypeQuadruplet<AtomName> atomNames = angle.getAtoms();
        Atom aa = StructureHelper.findAtom(residues.get(a), atomNames.a);
        Atom ab = StructureHelper.findAtom(residues.get(b), atomNames.b);
        Atom ac = StructureHelper.findAtom(residues.get(c), atomNames.c);
        Atom ad = StructureHelper.findAtom(residues.get(d), atomNames.d);

        if (aa == null || ab == null || ac == null || ad == null) {
            return TorsionAngleValue.invalidInstance(angle);
        }

        double value = TorsionAnglesHelper.calculateTorsion(aa, ab, ac, ad);
        return new TorsionAngleValue(angle, value);
    }

    @Override
    public String toString() {
        Residue first = Residue.fromGroup(residues.get(0));
        Residue last = Residue.fromGroup(residues.get(residues.size() - 1));
        return chainType + " " + first + " - " + last + " (count: "
                + residues.size() + ")";
    }
}
