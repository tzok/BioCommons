package pl.poznan.put.protein;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.ImmutableAtomBasedTorsionAngleType;
import pl.poznan.put.torsion.TorsionAngleType;
import pl.poznan.put.types.ImmutableQuadruple;
import pl.poznan.put.types.Quadruple;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class Chi4 {
  public static final Quadruple<AtomName> ARGININE_ATOMS =
      ImmutableQuadruple.of(AtomName.CG, AtomName.CD, AtomName.NE, AtomName.CZ);
  public static final Quadruple<AtomName> LYSINE_ATOMS =
      ImmutableQuadruple.of(AtomName.CG, AtomName.CD, AtomName.CE, AtomName.NZ);

  private static final Map<Quadruple<AtomName>, TorsionAngleType> ANGLE_MAP =
      Stream.of(Chi4.ARGININE_ATOMS, Chi4.LYSINE_ATOMS).collect(Collectors.toSet()).stream()
          .collect(
              Collectors.toMap(
                  Function.identity(),
                  quad ->
                      ImmutableAtomBasedTorsionAngleType.of(
                          MoleculeType.PROTEIN,
                          Unicode.CHI4,
                          "chi4",
                          quad,
                          ImmutableQuadruple.of(0, 0, 0, 0))));

  private Chi4() {
    super();
  }

  public static Collection<TorsionAngleType> angleTypes() {
    return Chi4.ANGLE_MAP.values();
  }

  public static TorsionAngleType getInstance(final Quadruple<AtomName> chiAtoms) {
    return Chi4.ANGLE_MAP.get(chiAtoms);
  }
}
