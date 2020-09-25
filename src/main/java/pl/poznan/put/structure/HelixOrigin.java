package pl.poznan.put.structure;

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
