package pl.poznan.put.rna;

import org.immutables.value.Value;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.analysis.ResidueComponent;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Value.Immutable(singleton = true)
abstract class Cytosine implements Pyrimidine {
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
