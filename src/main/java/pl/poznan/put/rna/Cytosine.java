package pl.poznan.put.rna;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.immutables.value.Value;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.analysis.ResidueComponent;

@Value.Immutable(singleton = true)
@JsonSerialize(as = ImmutableCytosine.class)
@JsonDeserialize(as = ImmutableCytosine.class)
abstract class Cytosine implements Pyrimidine {
  @Override
  public final String standardReferenceFrameString() {
    // source: http://ndbserver.rutgers.edu/ndbmodule/archives/reports/tsukuba/tsukuba.pdf
    return "ATOM 1 C1' C -2.477 5.402 0.000\n"
        + "ATOM 2 N1 C -1.285 4.542 0.000\n"
        + "ATOM 3 C2 C -1.472 3.158 0.000\n"
        + "ATOM 4 O2 C -2.628 2.709 0.001\n"
        + "ATOM 5 N3 C -0.391 2.344 0.000\n"
        + "ATOM 6 C4 C 0.837 2.868 0.000\n"
        + "ATOM 7 N4 C 1.875 2.027 0.001\n"
        + "ATOM 8 C5 C 1.056 4.275 0.000\n"
        + "ATOM 9 C6 C -0.023 5.068 0.000";
  }

  @Override
  public final Set<AtomName> requiredAtoms() {
    return Stream.of(
            AtomName.N1,
            AtomName.C6,
            AtomName.H6,
            AtomName.C5,
            AtomName.H5,
            AtomName.C2,
            AtomName.O2,
            AtomName.N3,
            AtomName.C4,
            AtomName.N4,
            AtomName.H41,
            AtomName.H42)
        .collect(Collectors.toSet());
  }

  @Override
  public final List<ResidueComponent> moleculeComponents() {
    return Stream.of(ImmutablePhosphate.of(), ImmutableRibose.of(), ImmutableCytosine.of())
        .collect(Collectors.toList());
  }

  @Override
  public final char oneLetterName() {
    return 'C';
  }

  @Override
  public final List<String> aliases() {
    return Stream.of("C", "CYT", "DC").collect(Collectors.toList());
  }
}
