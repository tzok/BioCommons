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

final class Chi2 {
  public static final Quadruple<AtomName> ARGININE_ATOMS =
      ImmutableQuadruple.of(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD);
  public static final Quadruple<AtomName> ASPARAGINE_ATOMS =
      ImmutableQuadruple.of(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.OD1);
  public static final Quadruple<AtomName> ASPARTIC_ACID_ATOMS =
      ImmutableQuadruple.of(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.OD1);
  public static final Quadruple<AtomName> GLUTAMIC_ACID_ATOMS =
      ImmutableQuadruple.of(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD);
  public static final Quadruple<AtomName> GLUTAMINE_ATOMS =
      ImmutableQuadruple.of(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD);
  public static final Quadruple<AtomName> HISTIDINE_ATOMS =
      ImmutableQuadruple.of(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.ND1);
  public static final Quadruple<AtomName> ISOLEUCINE_ATOMS =
      ImmutableQuadruple.of(AtomName.CA, AtomName.CB, AtomName.CG1, AtomName.CD1);
  public static final Quadruple<AtomName> LEUCINE_ATOMS =
      ImmutableQuadruple.of(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD1);
  public static final Quadruple<AtomName> LYSINE_ATOMS =
      ImmutableQuadruple.of(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD);
  public static final Quadruple<AtomName> METHIONINE_ATOMS =
      ImmutableQuadruple.of(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.SD);
  public static final Quadruple<AtomName> PHENYLALANINE_ATOMS =
      ImmutableQuadruple.of(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD1);
  public static final Quadruple<AtomName> PROLINE_ATOMS =
      ImmutableQuadruple.of(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD);
  public static final Quadruple<AtomName> TRYPTOPHAN_ATOMS =
      ImmutableQuadruple.of(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD1);
  public static final Quadruple<AtomName> TYROSINE_ATOMS =
      ImmutableQuadruple.of(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD1);

  private static final Map<Quadruple<AtomName>, TorsionAngleType> ANGLE_MAP =
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
                          ImmutableQuadruple.of(0, 0, 0, 0))));

  private Chi2() {
    super();
  }

  public static Collection<TorsionAngleType> angleTypes() {
    return Chi2.ANGLE_MAP.values();
  }

  public static TorsionAngleType getInstance(final Quadruple<AtomName> chiAtoms) {
    return Chi2.ANGLE_MAP.get(chiAtoms);
  }
}
