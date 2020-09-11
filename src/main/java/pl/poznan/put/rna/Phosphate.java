package pl.poznan.put.rna;

import org.immutables.value.Value;
import pl.poznan.put.atom.AtomName;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Value.Immutable(singleton = true)
public abstract class Phosphate implements NucleicAcidResidueComponent {
  @Override
  public final NucleotideComponentType nucleotideComponentType() {
    return NucleotideComponentType.PHOSPHATE;
  }

  @Override
  public final Set<AtomName> requiredAtoms() {
    return Stream.of(AtomName.P, AtomName.O1P, AtomName.O2P, AtomName.O3p, AtomName.O5p)
        .collect(Collectors.toSet());
  }

  @Override
  public final Set<AtomName> additionalAtoms() {
    return Stream.of(
            AtomName.O3P,
            AtomName.PA,
            AtomName.O1A,
            AtomName.O2A,
            AtomName.O3A,
            AtomName.PB,
            AtomName.O1B,
            AtomName.O2B,
            AtomName.O3B,
            AtomName.PC,
            AtomName.O1C,
            AtomName.O2C,
            AtomName.O3C,
            AtomName.PG,
            AtomName.O1G,
            AtomName.O2G,
            AtomName.O3G)
        .collect(Collectors.toSet());
  }
}
