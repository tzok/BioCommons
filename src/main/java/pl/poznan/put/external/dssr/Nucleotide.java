package pl.poznan.put.external.dssr;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Optional;
import org.immutables.value.Value;
import pl.poznan.put.pdb.ImmutablePdbNamedResidueIdentifier;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;

@Value.Immutable
@JsonSerialize(as = ImmutableNucleotide.class)
@JsonDeserialize(as = ImmutableNucleotide.class)
public interface Nucleotide {
  @JsonProperty("nt_id")
  String id();

  @JsonProperty("chain_name")
  String chainName();

  @JsonProperty("nt_code")
  String oneLetterName();

  @JsonProperty("nt_resnum")
  int residueNumber();

  default PdbNamedResidueIdentifier toNamedResidueIdentifer() {
    final String[] split = id().split("\\^");
    return ImmutablePdbNamedResidueIdentifier.of(
        chainName(),
        residueNumber(),
        split.length == 1 ? Optional.empty() : Optional.of(split[1]),
        oneLetterName().charAt(0));
  }
}
