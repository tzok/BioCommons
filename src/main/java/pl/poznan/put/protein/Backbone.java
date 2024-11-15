package pl.poznan.put.protein;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.analysis.ResidueComponent;

/** A backbone in a protein. */
@Value.Immutable(singleton = true)
@JsonSerialize(as = ImmutableBackbone.class)
@JsonDeserialize(as = ImmutableBackbone.class)
public interface Backbone extends ResidueComponent {
  @Override
  default Set<AtomName> requiredAtoms() {
    return Stream.of(AtomName.N, AtomName.HN, AtomName.CA, AtomName.HA, AtomName.C, AtomName.O)
        .collect(Collectors.toSet());
  }
}
