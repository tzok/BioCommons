package pl.poznan.put.structure.secondary.formats;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

@RequiredArgsConstructor
public abstract class AbstractStrand implements Strand {
  private final String name;

  @Override
  public final String getName() {
    return name;
  }

  @Override
  public final boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if ((o == null) || (getClass() != o.getClass())) {
      return false;
    }
    final Strand o1 = (Strand) o;
    return CollectionUtils.isEqualCollection(getSymbols(), o1.getSymbols());
  }

  @Override
  public final int hashCode() {
    return Objects.hash(getSymbols());
  }

  @Override
  public final String toString() {
    return String.format(">strand_%s\n%s\n%s", name, getSequence(), getStructure());
  }
}
