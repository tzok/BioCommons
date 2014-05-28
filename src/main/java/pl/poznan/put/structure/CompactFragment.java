package pl.poznan.put.structure;

import java.util.ArrayList;
import java.util.List;

import org.biojava.bio.structure.Atom;
import org.biojava.bio.structure.Chain;
import org.biojava.bio.structure.ChainImpl;
import org.biojava.bio.structure.Group;
import org.biojava.bio.structure.Structure;
import org.biojava.bio.structure.StructureImpl;

import pl.poznan.put.atoms.AtomName;
import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.common.ResidueType;
import pl.poznan.put.helper.StructureHelper;
import pl.poznan.put.helper.TorsionAnglesHelper;
import pl.poznan.put.helper.UniTypeQuadruplet;
import pl.poznan.put.torsion.AngleValue;
import pl.poznan.put.torsion.AtomsBasedTorsionAngle;
import pl.poznan.put.torsion.TorsionAngle;

public class CompactFragment {
    public static CompactFragment createShifted(CompactFragment origin,
            int shift, int size) {
        CompactFragment fragment = new CompactFragment(origin.parent,
                origin.moleculeType);
        for (int i = shift; i < shift + size; i++) {
            fragment.addGroup(origin.residues.get(i));
        }
        return fragment;
    }

    private final StructureSelection parent;
    private final MoleculeType moleculeType;
    private final List<Group> residues = new ArrayList<Group>();
    private final List<ResidueAngles> torsionAngles = new ArrayList<ResidueAngles>();
    private FragmentAngles fragmentAngles;

    public CompactFragment(StructureSelection parent, MoleculeType moleculeType) {
        super();
        this.parent = parent;
        this.moleculeType = moleculeType;
    }

    public MoleculeType getMoleculeType() {
        return moleculeType;
    }

    public void addGroup(Group residue) {
        residues.add(residue);
    }

    public Group getGroup(int index) {
        return residues.get(index);
    }

    public Residue getResidue(int index) {
        return Residue.fromGroup(residues.get(index));
    }

    public int getSize() {
        return residues.size();
    }

    public String getParentName() {
        return parent.getName();
    }

    public String getName() {
        return getParentName() + " " + moleculeType;
    }

    public String toPDB() {
        Chain chain = new ChainImpl();
        chain.setAtomGroups(residues);
        Structure structure = new StructureImpl(chain);
        return structure.toPDB();
    }

    @Override
    public String toString() {
        Residue first = Residue.fromGroup(residues.get(0));
        Residue last = Residue.fromGroup(residues.get(residues.size() - 1));
        return getName() + " " + first + " - " + last + " (count: "
                + residues.size() + ")";
    }

    // FIXME
    public FragmentAngles getFragmentAngles() {
        if (fragmentAngles == null) {
            calculateTorsionAngles();
        }
        return fragmentAngles;
    }

    private void calculateTorsionAngles() {
        for (int i = 0; i < residues.size(); i++) {
            Group group = residues.get(i);
            ResidueType residueType = ResidueType.fromString(moleculeType,
                    group.getPDBName());

            if (residueType == ResidueType.UNKNOWN) {
                residueType = ResidueType.detect(group);
            }

            List<AngleValue> values = new ArrayList<AngleValue>();

            if (residueType != ResidueType.UNKNOWN) {
                for (TorsionAngle angle : residueType.getTorsionAngles()) {
                    if (angle instanceof AtomsBasedTorsionAngle) {
                        values.add(calculateTorsionAngle(
                                (AtomsBasedTorsionAngle) angle, i));
                    }
                }
            }

            ResidueAngles result = new ResidueAngles(this, group, residueType,
                    values);
            torsionAngles.add(result);
        }

        fragmentAngles = new FragmentAngles(torsionAngles);
    }

    private AngleValue calculateTorsionAngle(AtomsBasedTorsionAngle angle, int i) {
        UniTypeQuadruplet<Integer> residueRule = angle.getResidueRule();
        int a = i + residueRule.a;
        if (a < 0 || a >= residues.size()) {
            return AngleValue.getInvalidInstance(angle);
        }

        int b = i + residueRule.b;
        if (b < 0 || b >= residues.size()) {
            return AngleValue.getInvalidInstance(angle);
        }

        int c = i + residueRule.c;
        if (c < 0 || c >= residues.size()) {
            return AngleValue.getInvalidInstance(angle);
        }

        int d = i + residueRule.d;
        if (d < 0 || d >= residues.size()) {
            return AngleValue.getInvalidInstance(angle);
        }

        UniTypeQuadruplet<AtomName> atomNames = angle.getAtoms();
        Atom aa = StructureHelper.findAtom(residues.get(a), atomNames.a);
        Atom ab = StructureHelper.findAtom(residues.get(b), atomNames.b);
        Atom ac = StructureHelper.findAtom(residues.get(c), atomNames.c);
        Atom ad = StructureHelper.findAtom(residues.get(d), atomNames.d);

        if (aa == null || ab == null || ac == null || ad == null) {
            return AngleValue.getInvalidInstance(angle);
        }

        double value = TorsionAnglesHelper.calculateTorsion(aa, ab, ac, ad);
        return new AngleValue(angle, value);
    }
}
