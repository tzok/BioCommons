package pl.poznan.put.rna;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.immutables.value.Value;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.analysis.ResidueComponent;

@Value.Immutable(singleton = true)
abstract class Thymine implements Pyrimidine {
  @Override
  public final String standardReferenceFrameString() {
    // source: http://ndbserver.rutgers.edu/ndbmodule/archives/reports/tsukuba/tsukuba.pdf
    return "ATOM 1 C1' T -2.481 5.354 0.000\n"
        + "ATOM 2 N1 T -1.284 4.500 0.000\n"
        + "ATOM 3 C2 T -1.462 3.135 0.000\n"
        + "ATOM 4 O2 T -2.562 2.608 0.000\n"
        + "ATOM 5 N3 T -0.298 2.407 0.000\n"
        + "ATOM 6 C4 T 0.994 2.897 0.000\n"
        + "ATOM 7 O4 T 1.944 2.119 0.000\n"
        + "ATOM 8 C5 T 1.106 4.338 0.000\n"
        + "ATOM 9 C5M T 2.466 4.961 0.001\n"
        + "ATOM 10 C6 T -0.024 5.057 0.000";
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
            AtomName.C5M,
            AtomName.H51,
            AtomName.H52,
            AtomName.H53)
        .collect(Collectors.toSet());
  }

  @Override
  public final List<ResidueComponent> moleculeComponents() {
    return Stream.of(ImmutablePhosphate.of(), ImmutableRibose.of(), ImmutableThymine.of())
        .collect(Collectors.toList());
  }

  @Override
  public final char oneLetterName() {
    return 'T';
  }

  @Override
  public final List<String> aliases() {
    return Stream.of("T", "THY", "DT").collect(Collectors.toList());
  }
}
