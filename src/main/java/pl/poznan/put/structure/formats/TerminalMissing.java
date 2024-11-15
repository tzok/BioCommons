package pl.poznan.put.structure.formats;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import org.immutables.value.Value;
import pl.poznan.put.structure.DotBracketSymbol;

/** A colecction of missing dot-bracket symbols at 5' or 3' end. */
@Value.Immutable
@JsonSerialize(as = ImmutableTerminalMissing.class)
@JsonDeserialize(as = ImmutableTerminalMissing.class)
public abstract class TerminalMissing {
  /**
   * @return The list of missing symbols.
   */
  @Value.Parameter(order = 1)
  public abstract List<DotBracketSymbol> symbols();
}
