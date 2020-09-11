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
