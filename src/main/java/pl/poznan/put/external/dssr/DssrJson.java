package pl.poznan.put.external.dssr;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableDssrJson.class)
@JsonDeserialize(as = ImmutableDssrJson.class)
public abstract class DssrJson {
  public abstract List<Pair> pairs();

  @JsonProperty("nts")
  public abstract List<Nucleotide> nucleotides();

  @Value.Lazy
  protected Map<String, Nucleotide> idNucleotideMap() {
    return nucleotides().stream().collect(Collectors.toMap(Nucleotide::id, Function.identity()));
  }

  public final Nucleotide findNucleotide(final String id) {
    return idNucleotideMap().get(id);
  }
}
