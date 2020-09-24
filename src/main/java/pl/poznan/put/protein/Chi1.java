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

final class Chi1 {
  public static final Quadruple<AtomName> ARGININE_ATOMS =
      ImmutableQuadruple.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
  public static final Quadruple<AtomName> ASPARAGINE_ATOMS =
      ImmutableQuadruple.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
  public static final Quadruple<AtomName> ASPARTIC_ACID_ATOMS =
      ImmutableQuadruple.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
  public static final Quadruple<AtomName> CYSTEINE_ATOMS =
      ImmutableQuadruple.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.SG);
  public static final Quadruple<AtomName> GLUTAMIC_ACID_ATOMS =
      ImmutableQuadruple.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
  public static final Quadruple<AtomName> GLUTAMINE_ATOMS =
      ImmutableQuadruple.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
  public static final Quadruple<AtomName> HISTIDINE_ATOMS =
      ImmutableQuadruple.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
  public static final Quadruple<AtomName> ISOLEUCINE_ATOMS =
      ImmutableQuadruple.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG1);
  public static final Quadruple<AtomName> LEUCINE_ATOMS =
      ImmutableQuadruple.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
  public static final Quadruple<AtomName> LYSINE_ATOMS =
      ImmutableQuadruple.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
  public static final Quadruple<AtomName> METHIONINE_ATOMS =
      ImmutableQuadruple.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
  public static final Quadruple<AtomName> PHENYLALANINE_ATOMS =
      ImmutableQuadruple.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
  public static final Quadruple<AtomName> PROLINE_ATOMS =
      ImmutableQuadruple.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
  public static final Quadruple<AtomName> SERINE_ATOMS =
      ImmutableQuadruple.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.OG);
  public static final Quadruple<AtomName> THREONINE_ATOMS =
      ImmutableQuadruple.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.OG1);
  public static final Quadruple<AtomName> TRYPTOPHAN_ATOMS =
      ImmutableQuadruple.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
  public static final Quadruple<AtomName> TYROSINE_ATOMS =
      ImmutableQuadruple.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
  public static final Quadruple<AtomName> VALINE_ATOMS =
      ImmutableQuadruple.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG1);

  private static final Map<Quadruple<AtomName>, TorsionAngleType> ANGLE_MAP =
      Stream.of(
              Chi1.ARGININE_ATOMS,
              Chi1.ASPARAGINE_ATOMS,
              Chi1.ASPARTIC_ACID_ATOMS,
              Chi1.CYSTEINE_ATOMS,
              Chi1.GLUTAMIC_ACID_ATOMS,
              Chi1.GLUTAMINE_ATOMS,
              Chi1.HISTIDINE_ATOMS,
              Chi1.ISOLEUCINE_ATOMS,
              Chi1.LEUCINE_ATOMS,
              Chi1.LYSINE_ATOMS,
              Chi1.METHIONINE_ATOMS,
              Chi1.PHENYLALANINE_ATOMS,
              Chi1.PROLINE_ATOMS,
              Chi1.SERINE_ATOMS,
              Chi1.THREONINE_ATOMS,
              Chi1.TRYPTOPHAN_ATOMS,
              Chi1.VALINE_ATOMS)
          .collect(Collectors.toSet())
          .stream()
          .collect(
              Collectors.toMap(
                  Function.identity(),
                  quad ->
                      ImmutableAtomBasedTorsionAngleType.of(
                          MoleculeType.PROTEIN,
                          Unicode.CHI1,
                          "chi1",
                          quad,
                          ImmutableQuadruple.of(0, 0, 0, 0))));

  private Chi1() {
    super();
  }

  public static Collection<TorsionAngleType> angleTypes() {
    return Chi1.ANGLE_MAP.values();
  }

  public static TorsionAngleType getInstance(final Quadruple<AtomName> chiAtoms) {
    return Chi1.ANGLE_MAP.get(chiAtoms);
  }
}
