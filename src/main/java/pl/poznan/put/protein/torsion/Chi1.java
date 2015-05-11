package pl.poznan.put.protein.torsion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.type.AtomBasedTorsionAngleType;
import pl.poznan.put.types.Quadruplet;

public class Chi1 extends AtomBasedTorsionAngleType {
    public static final Quadruplet<AtomName> ARGININE_ATOMS = new Quadruplet<AtomName>(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
    public static final Quadruplet<AtomName> ASPARAGINE_ATOMS = new Quadruplet<AtomName>(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
    public static final Quadruplet<AtomName> ASPARTIC_ACID_ATOMS = new Quadruplet<AtomName>(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
    public static final Quadruplet<AtomName> CYSTEINE_ATOMS = new Quadruplet<AtomName>(AtomName.N, AtomName.CA, AtomName.CB, AtomName.SG);
    public static final Quadruplet<AtomName> GLUTAMIC_ACID_ATOMS = new Quadruplet<AtomName>(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
    public static final Quadruplet<AtomName> GLUTAMINE_ATOMS = new Quadruplet<AtomName>(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
    public static final Quadruplet<AtomName> HISTIDINE_ATOMS = new Quadruplet<AtomName>(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
    public static final Quadruplet<AtomName> ISOLEUCINE_ATOMS = new Quadruplet<AtomName>(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG1);
    public static final Quadruplet<AtomName> LEUCINE_ATOMS = new Quadruplet<AtomName>(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
    public static final Quadruplet<AtomName> LYSINE_ATOMS = new Quadruplet<AtomName>(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
    public static final Quadruplet<AtomName> METHIONINE_ATOMS = new Quadruplet<AtomName>(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
    public static final Quadruplet<AtomName> PHENYLALANINE_ATOMS = new Quadruplet<AtomName>(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
    public static final Quadruplet<AtomName> PROLINE_ATOMS = new Quadruplet<AtomName>(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
    public static final Quadruplet<AtomName> SERINE_ATOMS = new Quadruplet<AtomName>(AtomName.N, AtomName.CA, AtomName.CB, AtomName.OG);
    public static final Quadruplet<AtomName> THREONINE_ATOMS = new Quadruplet<AtomName>(AtomName.N, AtomName.CA, AtomName.CB, AtomName.OG1);
    public static final Quadruplet<AtomName> TRYPTOPHAN_ATOMS = new Quadruplet<AtomName>(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
    public static final Quadruplet<AtomName> TYROSINE_ATOMS = new Quadruplet<AtomName>(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
    public static final Quadruplet<AtomName> VALINE_ATOMS = new Quadruplet<AtomName>(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG1);

    private static final Map<Quadruplet<AtomName>, Chi1> INSTANCE_CACHE = new HashMap<Quadruplet<AtomName>, Chi1>();

    public static Chi1 getInstance(Quadruplet<AtomName> atoms) {
        if (!Chi1.INSTANCE_CACHE.containsKey(atoms)) {
            Chi1.INSTANCE_CACHE.put(atoms, new Chi1(atoms));
        }
        return Chi1.INSTANCE_CACHE.get(atoms);
    }

    private Chi1(Quadruplet<AtomName> atoms) {
        super(MoleculeType.PROTEIN, Unicode.CHI1, atoms, new Quadruplet<Integer>(0, 0, 0, 0));
    }

    public static Chi1[] getInstances() {
        List<Chi1> instances = new ArrayList<Chi1>();
        instances.add(Chi1.getInstance(Chi1.ARGININE_ATOMS));
        instances.add(Chi1.getInstance(Chi1.ASPARAGINE_ATOMS));
        instances.add(Chi1.getInstance(Chi1.ASPARTIC_ACID_ATOMS));
        instances.add(Chi1.getInstance(Chi1.CYSTEINE_ATOMS));
        instances.add(Chi1.getInstance(Chi1.GLUTAMIC_ACID_ATOMS));
        instances.add(Chi1.getInstance(Chi1.GLUTAMINE_ATOMS));
        instances.add(Chi1.getInstance(Chi1.HISTIDINE_ATOMS));
        instances.add(Chi1.getInstance(Chi1.ISOLEUCINE_ATOMS));
        instances.add(Chi1.getInstance(Chi1.LEUCINE_ATOMS));
        instances.add(Chi1.getInstance(Chi1.LYSINE_ATOMS));
        instances.add(Chi1.getInstance(Chi1.METHIONINE_ATOMS));
        instances.add(Chi1.getInstance(Chi1.PHENYLALANINE_ATOMS));
        instances.add(Chi1.getInstance(Chi1.PROLINE_ATOMS));
        instances.add(Chi1.getInstance(Chi1.SERINE_ATOMS));
        instances.add(Chi1.getInstance(Chi1.THREONINE_ATOMS));
        instances.add(Chi1.getInstance(Chi1.TRYPTOPHAN_ATOMS));
        instances.add(Chi1.getInstance(Chi1.VALINE_ATOMS));
        return instances.toArray(new Chi1[instances.size()]);
    }
}
