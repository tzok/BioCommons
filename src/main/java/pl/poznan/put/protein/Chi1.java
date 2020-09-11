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

final class Chi1 {
  public static final Quadruplet<AtomName> ARGININE_ATOMS =
      ImmutableQuadruplet.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
  public static final Quadruplet<AtomName> ASPARAGINE_ATOMS =
      ImmutableQuadruplet.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
  public static final Quadruplet<AtomName> ASPARTIC_ACID_ATOMS =
      ImmutableQuadruplet.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
  public static final Quadruplet<AtomName> CYSTEINE_ATOMS =
      ImmutableQuadruplet.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.SG);
  public static final Quadruplet<AtomName> GLUTAMIC_ACID_ATOMS =
      ImmutableQuadruplet.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
  public static final Quadruplet<AtomName> GLUTAMINE_ATOMS =
      ImmutableQuadruplet.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
  public static final Quadruplet<AtomName> HISTIDINE_ATOMS =
      ImmutableQuadruplet.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
  public static final Quadruplet<AtomName> ISOLEUCINE_ATOMS =
      ImmutableQuadruplet.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG1);
  public static final Quadruplet<AtomName> LEUCINE_ATOMS =
      ImmutableQuadruplet.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
  public static final Quadruplet<AtomName> LYSINE_ATOMS =
      ImmutableQuadruplet.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
  public static final Quadruplet<AtomName> METHIONINE_ATOMS =
      ImmutableQuadruplet.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
  public static final Quadruplet<AtomName> PHENYLALANINE_ATOMS =
      ImmutableQuadruplet.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
  public static final Quadruplet<AtomName> PROLINE_ATOMS =
      ImmutableQuadruplet.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
  public static final Quadruplet<AtomName> SERINE_ATOMS =
      ImmutableQuadruplet.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.OG);
  public static final Quadruplet<AtomName> THREONINE_ATOMS =
      ImmutableQuadruplet.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.OG1);
  public static final Quadruplet<AtomName> TRYPTOPHAN_ATOMS =
      ImmutableQuadruplet.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
  public static final Quadruplet<AtomName> TYROSINE_ATOMS =
      ImmutableQuadruplet.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG);
  public static final Quadruplet<AtomName> VALINE_ATOMS =
      ImmutableQuadruplet.of(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG1);

  private static final Map<Quadruplet<AtomName>, TorsionAngleType> ANGLE_MAP =
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
                          ImmutableQuadruplet.of(0, 0, 0, 0))));

  private Chi1() {
    super();
  }

  public static Collection<TorsionAngleType> angleTypes() {
    return Chi1.ANGLE_MAP.values();
  }

  public static TorsionAngleType getInstance(final Quadruplet<AtomName> chiAtoms) {
    return Chi1.ANGLE_MAP.get(chiAtoms);
  }
}
