package pl.poznan.put.rna;

import org.immutables.value.Value;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.analysis.ResidueComponent;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Value.Immutable(singleton = true)
abstract class Guanine implements Purine {
  @Override
  public final String standardReferenceFrameString() {
    // source: http://ndbserver.rutgers.edu/ndbmodule/archives/reports/tsukuba/tsukuba.pdf
    return "ATOM 1 C1' G -2.477 5.399 0.000\n"
        + "ATOM 2 N9 G -1.289 4.551 0.000\n"
        + "ATOM 3 C8 G 0.023 4.962 0.000\n"
        + "ATOM 4 N7 G 0.870 3.969 0.000\n"
        + "ATOM 5 C5 G 0.071 2.833 0.000\n"
        + "ATOM 6 C6 G 0.424 1.460 0.000\n"
        + "ATOM 7 O6 G 1.554 0.955 0.000\n"
        + "ATOM 8 N1 G -0.700 0.641 0.000\n"
        + "ATOM 9 C2 G -1.999 1.087 0.000\n"
        + "ATOM 10 N2 G -2.949 0.139 -0.001\n"
        + "ATOM 11 N3 G -2.342 2.364 0.001\n"
        + "ATOM 12 C4 G -1.265 3.177 0.000";
  }

  @Override
  public final Set<AtomName> requiredAtoms() {
    return Stream.of(
            AtomName.N9,
            AtomName.C4,
            AtomName.N2,
            AtomName.H21,
            AtomName.H22,
            AtomName.N3,
            AtomName.C2,
            AtomName.N1,
            AtomName.H1,
            AtomName.C6,
            AtomName.O6,
            AtomName.C5,
            AtomName.N7,
            AtomName.C8,
            AtomName.H8)
        .collect(Collectors.toSet());
  }

  @Override
  public final List<ResidueComponent> moleculeComponents() {
    return Stream.of(ImmutablePhosphate.of(), ImmutableRibose.of(), ImmutableGuanine.of())
        .collect(Collectors.toList());
  }

  @Override
  public final char oneLetterName() {
    return 'G';
  }

  @Override
  public final List<String> aliases() {
    return Stream.of("G", "GUA", "DG").collect(Collectors.toList());
  }
}
