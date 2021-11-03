package pl.poznan.put.external.dssr;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutablePair.class)
@JsonDeserialize(as = ImmutablePair.class)
/** A pair as represented in DSSR's JSON file. */
public interface Pair {
  String nt1();

  String nt2();

  @JsonProperty("Saenger")
  String saenger();

  @JsonProperty("LW")
  String lw();
}
