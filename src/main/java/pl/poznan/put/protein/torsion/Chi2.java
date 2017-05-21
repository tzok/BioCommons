package pl.poznan.put.protein.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.AtomBasedTorsionAngleType;
import pl.poznan.put.types.Quadruplet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Chi2 extends AtomBasedTorsionAngleType {
    public static final Quadruplet<AtomName> ARGININE_ATOMS =
            new Quadruplet<>(AtomName.CA, AtomName.CB, AtomName.CG,
                             AtomName.CD);
    public static final Quadruplet<AtomName> ASPARAGINE_ATOMS =
            new Quadruplet<>(AtomName.CA, AtomName.CB, AtomName.CG,
                             AtomName.OD1);
    public static final Quadruplet<AtomName> ASPARTIC_ACID_ATOMS =
            new Quadruplet<>(AtomName.CA, AtomName.CB, AtomName.CG,
                             AtomName.OD1);
    public static final Quadruplet<AtomName> GLUTAMIC_ACID_ATOMS =
            new Quadruplet<>(AtomName.CA, AtomName.CB, AtomName.CG,
                             AtomName.CD);
    public static final Quadruplet<AtomName> GLUTAMINE_ATOMS =
            new Quadruplet<>(AtomName.CA, AtomName.CB, AtomName.CG,
                             AtomName.CD);
    public static final Quadruplet<AtomName> HISTIDINE_ATOMS =
            new Quadruplet<>(AtomName.CA, AtomName.CB, AtomName.CG,
                             AtomName.ND1);
    public static final Quadruplet<AtomName> ISOLEUCINE_ATOMS =
            new Quadruplet<>(AtomName.CA, AtomName.CB, AtomName.CG1,
                             AtomName.CD1);
    public static final Quadruplet<AtomName> LEUCINE_ATOMS =
            new Quadruplet<>(AtomName.CA, AtomName.CB, AtomName.CG,
                             AtomName.CD1);
    public static final Quadruplet<AtomName> LYSINE_ATOMS =
            new Quadruplet<>(AtomName.CA, AtomName.CB, AtomName.CG,
                             AtomName.CD);
    public static final Quadruplet<AtomName> METHIONINE_ATOMS =
            new Quadruplet<>(AtomName.CA, AtomName.CB, AtomName.CG,
                             AtomName.SD);
    public static final Quadruplet<AtomName> PHENYLALANINE_ATOMS =
            new Quadruplet<>(AtomName.CA, AtomName.CB, AtomName.CG,
                             AtomName.CD1);
    public static final Quadruplet<AtomName> PROLINE_ATOMS =
            new Quadruplet<>(AtomName.CA, AtomName.CB, AtomName.CG,
                             AtomName.CD);
    public static final Quadruplet<AtomName> TRYPTOPHAN_ATOMS =
            new Quadruplet<>(AtomName.CA, AtomName.CB, AtomName.CG,
                             AtomName.CD1);
    public static final Quadruplet<AtomName> TYROSINE_ATOMS =
            new Quadruplet<>(AtomName.CA, AtomName.CB, AtomName.CG,
                             AtomName.CD1);

    private static final Map<Quadruplet<AtomName>, Chi2> INSTANCE_CACHE =
            new HashMap<>();

    private Chi2(final Quadruplet<AtomName> atoms) {
        super(MoleculeType.PROTEIN, Unicode.CHI2, atoms,
              new Quadruplet<>(0, 0, 0, 0));
    }

    public static Chi2[] getInstances() {
        final List<Chi2> instances = new ArrayList<>();
        instances.add(Chi2.getInstance(Chi2.ARGININE_ATOMS));
        instances.add(Chi2.getInstance(Chi2.ASPARAGINE_ATOMS));
        instances.add(Chi2.getInstance(Chi2.ASPARTIC_ACID_ATOMS));
        instances.add(Chi2.getInstance(Chi2.GLUTAMIC_ACID_ATOMS));
        instances.add(Chi2.getInstance(Chi2.GLUTAMINE_ATOMS));
        instances.add(Chi2.getInstance(Chi2.HISTIDINE_ATOMS));
        instances.add(Chi2.getInstance(Chi2.ISOLEUCINE_ATOMS));
        instances.add(Chi2.getInstance(Chi2.LEUCINE_ATOMS));
        instances.add(Chi2.getInstance(Chi2.LYSINE_ATOMS));
        instances.add(Chi2.getInstance(Chi2.METHIONINE_ATOMS));
        instances.add(Chi2.getInstance(Chi2.PHENYLALANINE_ATOMS));
        instances.add(Chi2.getInstance(Chi2.PROLINE_ATOMS));
        instances.add(Chi2.getInstance(Chi2.TRYPTOPHAN_ATOMS));
        return instances.toArray(new Chi2[instances.size()]);
    }

    public static Chi2 getInstance(final Quadruplet<AtomName> atoms) {
        if (!Chi2.INSTANCE_CACHE.containsKey(atoms)) {
            Chi2.INSTANCE_CACHE.put(atoms, new Chi2(atoms));
        }
        return Chi2.INSTANCE_CACHE.get(atoms);
    }
}
