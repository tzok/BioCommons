package pl.poznan.put.structure;

/** An information whether a base pair originated from a helix. */
public enum HelixOrigin {
  UNKNOWN,
  TRUE,
  FALSE;

  @Override
  public String toString() {
    switch (this) {
      case FALSE:
        return "false";
      case TRUE:
        return "true";
      case UNKNOWN:
      default:
        return "unknown";
    }
  }

  /** @return A one letter representation of this instance. */
  public String toOneLetter() {
    switch (this) {
      case FALSE:
        return "N";
      case TRUE:
        return "Y";
      case UNKNOWN:
      default:
        return "?";
    }
  }
}
