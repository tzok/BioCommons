package pl.poznan.put.structure.tertiary;

import org.immutables.value.Value;
import pl.poznan.put.pdb.analysis.PdbModel;

import java.io.File;
import java.util.Comparator;

@Value.Immutable
public abstract class StructureInfo implements Comparable<StructureInfo> {
  @Value.Parameter(order = 1)
  public abstract PdbModel structure();

  @Value.Parameter(order = 2)
  public abstract File path();

  @Value.Parameter(order = 3)
  public abstract String name();

  @Override
  public final int compareTo(final StructureInfo t) {
    return Comparator.comparing(StructureInfo::name).compare(this, t);
  }
}
