package pl.poznan.put.rna;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.immutables.value.Value;
import pl.poznan.put.atom.AtomName;

/** A ribose, part of RNA backbone. */
@Value.Immutable(singleton = true)
@JsonSerialize(as = ImmutableRibose.class)
@JsonDeserialize(as = ImmutableRibose.class)
public abstract class Ribose implements Sugar {
  @Override
  public final Set<AtomName> requiredAtoms() {
    return Stream.of(
            AtomName.C5p,
            AtomName.H5p,
            AtomName.H5pp,
            AtomName.C4p,
            AtomName.H4p,
            AtomName.O4p,
            AtomName.C3p,
            AtomName.H3p,
            AtomName.C2p,
            AtomName.O2p,
            AtomName.H2p,
            AtomName.H2pp,
            AtomName.C1p,
            AtomName.H1p)
        .collect(Collectors.toSet());
  }
}
