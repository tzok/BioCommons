package pl.poznan.put.rna;

import org.immutables.value.Value;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.analysis.ResidueComponent;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Value.Immutable(singleton = true)
abstract class Adenine implements Purine {
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
