package pl.poznan.put.protein.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.ImmutableAtomBasedTorsionAngleType;
import pl.poznan.put.torsion.TorsionAngleType;
import pl.poznan.put.types.ImmutableQuadruplet;
import pl.poznan.put.types.Quadruplet;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Chi3 {
  public static final Quadruplet<AtomName> ARGININE_ATOMS =
      ImmutableQuadruplet.of(AtomName.CB, AtomName.CG, AtomName.CD, AtomName.NE);
  public static final Quadruplet<AtomName> GLUTAMIC_ACID_ATOMS =
      ImmutableQuadruplet.of(AtomName.CB, AtomName.CG, AtomName.CD, AtomName.OE1);
  public static final Quadruplet<AtomName> GLUTAMINE_ATOMS =
      ImmutableQuadruplet.of(AtomName.CB, AtomName.CG, AtomName.CD, AtomName.OE1);
  public static final Quadruplet<AtomName> LYSINE_ATOMS =
      ImmutableQuadruplet.of(AtomName.CB, AtomName.CG, AtomName.CD, AtomName.CE);
  public static final Quadruplet<AtomName> METHIONINE_ATOMS =
      ImmutableQuadruplet.of(AtomName.CB, AtomName.CG, AtomName.SD, AtomName.CE);

  private static final Map<Quadruplet<AtomName>, TorsionAngleType> ANGLE_MAP =
      Stream.of(
              Chi3.ARGININE_ATOMS,
              Chi3.GLUTAMIC_ACID_ATOMS,
              Chi3.GLUTAMINE_ATOMS,
              Chi3.LYSINE_ATOMS,
              Chi3.METHIONINE_ATOMS)
          .collect(Collectors.toSet())
          .stream()
          .collect(
              Collectors.toMap(
                  Function.identity(),
                  quad ->
                      ImmutableAtomBasedTorsionAngleType.of(
                          MoleculeType.PROTEIN,
                          Unicode.CHI3,
                          "chi3",
                          quad,
                          ImmutableQuadruplet.of(0, 0, 0, 0))));

  private Chi3() {
    super();
  }

  public static Collection<TorsionAngleType> angleTypes() {
    return Chi3.ANGLE_MAP.values();
  }

  public static TorsionAngleType getInstance(final Quadruplet<AtomName> chiAtoms) {
    return Chi3.ANGLE_MAP.get(chiAtoms);
  }
}
