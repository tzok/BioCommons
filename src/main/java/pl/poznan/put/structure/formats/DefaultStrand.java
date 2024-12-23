package pl.poznan.put.structure.formats;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import org.immutables.value.Value;
import pl.poznan.put.structure.DotBracketSymbol;

/** A default implementation of a strand. */
@Value.Immutable
@JsonSerialize(as = ImmutableDefaultStrand.class)
@JsonDeserialize(as = ImmutableDefaultStrand.class)
public abstract class DefaultStrand implements Strand {
  @Override
  @Value.Parameter(order = 1)
  public abstract String name();

  @Override
  @Value.Parameter(order = 2)
  public abstract List<DotBracketSymbol> symbols();

  @Override
  public final String toString() {
    return String.format(">strand_%s\n%s\n%s", name(), sequence(), structure());
  }
}
