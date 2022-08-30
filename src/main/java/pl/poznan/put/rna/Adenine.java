package pl.poznan.put.rna;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.immutables.value.Value;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.analysis.ResidueComponent;

@Value.Immutable(singleton = true)
abstract class Adenine implements Purine {
  @Override
  public final String standardReferenceFrameString() {
    // source: http://ndbserver.rutgers.edu/ndbmodule/archives/reports/tsukuba/tsukuba.pdf
    return "ATOM 1 C1' A -2.479 5.346 0.000\n"
        + "ATOM 2 N9 A -1.291 4.498 0.000\n"
        + "ATOM 3 C8 A 0.024 4.897 0.000\n"
        + "ATOM 4 N7 A 0.877 3.902 0.000\n"
        + "ATOM 5 C5 A 0.071 2.771 0.000\n"
        + "ATOM 6 C6 A 0.369 1.398 0.000\n"
        + "ATOM 7 N6 A 1.611 0.909 0.000\n"
        + "ATOM 8 N1 A -0.668 0.532 0.000\n"
        + "ATOM 9 C2 A -1.912 1.023 0.000\n"
        + "ATOM 10 N3 A -2.320 2.290 0.000\n"
        + "ATOM 11 C4 A -1.267 3.124 0.000";
  }

  @Override
  public final Set<AtomName> requiredAtoms() {
    return Stream.of(
            AtomName.N9,
            AtomName.C5,
            AtomName.N7,
            AtomName.C8,
            AtomName.H8,
            AtomName.N1,
            AtomName.C2,
            AtomName.H2,
            AtomName.N3,
            AtomName.C4,
            AtomName.C6,
            AtomName.N6,
            AtomName.H61,
            AtomName.H62)
        .collect(Collectors.toSet());
  }

  @Override
  public final List<ResidueComponent> moleculeComponents() {
    return Stream.of(ImmutablePhosphate.of(), ImmutableRibose.of(), ImmutableAdenine.of())
        .collect(Collectors.toList());
  }

  @Override
  public final char oneLetterName() {
    return 'A';
  }

  @Override
  public final List<String> aliases() {
    return Stream.of("A", "ADE", "DA").collect(Collectors.toList());
  }
}
