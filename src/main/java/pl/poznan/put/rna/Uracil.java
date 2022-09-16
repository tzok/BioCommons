package pl.poznan.put.rna;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.immutables.value.Value;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.analysis.ResidueComponent;

@Value.Immutable(singleton = true)
abstract class Uracil implements Pyrimidine {
  @Override
  public final String standardReferenceFrameString() {
    // source: http://ndbserver.rutgers.edu/ndbmodule/archives/reports/tsukuba/tsukuba.pdf
    return "ATOM 1 C1' U -2.481 5.354 0.000\n"
        + "ATOM 2 N1 U -1.284 4.500 0.000\n"
        + "ATOM 3 C2 U -1.462 3.131 0.000\n"
        + "ATOM 4 O2 U -2.563 2.608 0.000\n"
        + "ATOM 5 N3 U -0.302 2.397 0.000\n"
        + "ATOM 6 C4 U 0.989 2.884 0.000\n"
        + "ATOM 7 O4 U 1.935 2.094 -0.001\n"
        + "ATOM 8 C5 U 1.089 4.311 0.000\n"
        + "ATOM 9 C6 U -0.024 5.053 0.000";
  }

  @Override
  public final Set<AtomName> requiredAtoms() {
    return Stream.of(
            AtomName.N1,
            AtomName.C6,
            AtomName.H6,
            AtomName.C2,
            AtomName.O2,
            AtomName.N3,
            AtomName.H3,
            AtomName.C4,
            AtomName.O4,
            AtomName.C5,
            AtomName.H5)
        .collect(Collectors.toSet());
  }

  @Override
  public final List<ResidueComponent> moleculeComponents() {
    return Stream.of(ImmutablePhosphate.of(), ImmutableRibose.of(), ImmutableUracil.of())
        .collect(Collectors.toList());
  }

  @Override
  public final char oneLetterName() {
    return 'U';
  }

  @Override
  public final List<String> aliases() {
    return Stream.of("U", "URA", "URI", "DU").collect(Collectors.toList());
  }
}
