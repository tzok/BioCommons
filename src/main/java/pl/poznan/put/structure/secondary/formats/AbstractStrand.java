package pl.poznan.put.structure.secondary.formats;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractStrand implements Strand {
  private final String name;

  @Override
  public final String getName() {
    return name;
  }

  @Override
  public final String toString() {
    return String.format(">strand_%s\n%s\n%s", name, getSequence(), getStructure());
  }
}
