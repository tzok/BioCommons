package pl.poznan.put.protein;

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

final class Chi2 {
  public static final Quadruplet<AtomName> ARGININE_ATOMS =
      ImmutableQuadruplet.of(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD);
  public static final Quadruplet<AtomName> ASPARAGINE_ATOMS =
      ImmutableQuadruplet.of(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.OD1);
  public static final Quadruplet<AtomName> ASPARTIC_ACID_ATOMS =
      ImmutableQuadruplet.of(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.OD1);
  public static final Quadruplet<AtomName> GLUTAMIC_ACID_ATOMS =
      ImmutableQuadruplet.of(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD);
  public static final Quadruplet<AtomName> GLUTAMINE_ATOMS =
      ImmutableQuadruplet.of(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD);
  public static final Quadruplet<AtomName> HISTIDINE_ATOMS =
      ImmutableQuadruplet.of(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.ND1);
  public static final Quadruplet<AtomName> ISOLEUCINE_ATOMS =
      ImmutableQuadruplet.of(AtomName.CA, AtomName.CB, AtomName.CG1, AtomName.CD1);
  public static final Quadruplet<AtomName> LEUCINE_ATOMS =
      ImmutableQuadruplet.of(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD1);
  public static final Quadruplet<AtomName> LYSINE_ATOMS =
      ImmutableQuadruplet.of(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD);
  public static final Quadruplet<AtomName> METHIONINE_ATOMS =
      ImmutableQuadruplet.of(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.SD);
  public static final Quadruplet<AtomName> PHENYLALANINE_ATOMS =
      ImmutableQuadruplet.of(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD1);
  public static final Quadruplet<AtomName> PROLINE_ATOMS =
      ImmutableQuadruplet.of(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD);
  public static final Quadruplet<AtomName> TRYPTOPHAN_ATOMS =
      ImmutableQuadruplet.of(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD1);
  public static final Quadruplet<AtomName> TYROSINE_ATOMS =
      ImmutableQuadruplet.of(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD1);

  private static final Map<Quadruplet<AtomName>, TorsionAngleType> ANGLE_MAP =
      Stream.of(
              Chi2.ARGININE_ATOMS,
              Chi2.ASPARAGINE_ATOMS,
              Chi2.ASPARTIC_ACID_ATOMS,
              Chi2.GLUTAMIC_ACID_ATOMS,
              Chi2.GLUTAMINE_ATOMS,
              Chi2.HISTIDINE_ATOMS,
              Chi2.ISOLEUCINE_ATOMS,
              Chi2.LEUCINE_ATOMS,
              Chi2.LYSINE_ATOMS,
              Chi2.METHIONINE_ATOMS,
              Chi2.PHENYLALANINE_ATOMS,
              Chi2.PROLINE_ATOMS,
              Chi2.TRYPTOPHAN_ATOMS)
          .collect(Collectors.toSet())
          .stream()
          .collect(
              Collectors.toMap(
                  Function.identity(),
                  quad ->
                      ImmutableAtomBasedTorsionAngleType.of(
                          MoleculeType.PROTEIN,
                          Unicode.CHI2,
                          "chi2",
                          quad,
                          ImmutableQuadruplet.of(0, 0, 0, 0))));

  private Chi2() {
    super();
  }

  public static Collection<TorsionAngleType> angleTypes() {
    return Chi2.ANGLE_MAP.values();
  }

  public static TorsionAngleType getInstance(final Quadruplet<AtomName> chiAtoms) {
    return Chi2.ANGLE_MAP.get(chiAtoms);
  }
}
