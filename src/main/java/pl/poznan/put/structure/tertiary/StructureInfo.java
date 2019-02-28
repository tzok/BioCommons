package pl.poznan.put.structure.tertiary;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import pl.poznan.put.pdb.analysis.PdbModel;

import java.io.File;
import java.util.Comparator;

@Data
public class StructureInfo implements Comparable<StructureInfo> {
  private final PdbModel structure;
  private final File path;
  private final String name;

  private final Comparator<StructureInfo> comparator = Comparator.comparing(StructureInfo::getName);

  @Override
  public final int compareTo(final @NotNull StructureInfo t) {
    return comparator.compare(this, t);
  }
}
