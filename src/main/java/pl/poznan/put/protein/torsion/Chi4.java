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

public final class Chi4 extends AtomBasedTorsionAngleType {
  public static final Quadruplet<AtomName> ARGININE_ATOMS =
      new Quadruplet<>(AtomName.CG, AtomName.CD, AtomName.NE, AtomName.CZ);
  public static final Quadruplet<AtomName> LYSINE_ATOMS =
      new Quadruplet<>(AtomName.CG, AtomName.CD, AtomName.CE, AtomName.NZ);

  private static final Map<Quadruplet<AtomName>, Chi4> INSTANCE_CACHE = new HashMap<>();

  private Chi4(final Quadruplet<AtomName> atoms) {
    super(MoleculeType.PROTEIN, Unicode.CHI4, atoms, new Quadruplet<>(0, 0, 0, 0));
  }

  public static Chi4[] getInstances() {
    final List<Chi4> instances = new ArrayList<>();
    instances.add(Chi4.getInstance(Chi4.ARGININE_ATOMS));
    instances.add(Chi4.getInstance(Chi4.LYSINE_ATOMS));
    return instances.toArray(new Chi4[0]);
  }

  public static Chi4 getInstance(final Quadruplet<AtomName> atoms) {
    if (!Chi4.INSTANCE_CACHE.containsKey(atoms)) {
      Chi4.INSTANCE_CACHE.put(atoms, new Chi4(atoms));
    }
    return Chi4.INSTANCE_CACHE.get(atoms);
  }
}
