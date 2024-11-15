package pl.poznan.put.rna;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import pl.poznan.put.atom.AtomName;

/** A phosphate, part of RNA backbone. */
@Value.Immutable(singleton = true)
@JsonSerialize(as = ImmutablePhosphate.class)
@JsonDeserialize(as = ImmutablePhosphate.class)
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
