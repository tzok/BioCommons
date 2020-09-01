package pl.poznan.put.interfaces;

/**
 * A set of methods that any kind of result has to implement to be displayable in UI or exportable
 * to output file.
 */
public interface DisplayableExportable {
  /** @return A long name to be shown in UI, may contain Unicode. */
  String getLongDisplayName();

  /** @return A short name to be shown in UI summary. */
  String getShortDisplayName();

  /** @return A name to be used during export to output file, should be ASCII only. */
  String getExportName();
}
