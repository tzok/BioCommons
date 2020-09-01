package pl.poznan.put.atom;

/** Enumeration of all atom types to be found in PDB and mmCIF files. */
public enum AtomType {
  C(true),
  H(false),
  N(true),
  O(true),
  P(true),
  S(true),
  OTHER(true);

  private final boolean heavy;

  AtomType(final boolean isHeavy) {
    this.heavy = isHeavy;
  }

  /**
   * Check if this atom is heavy i.e. not a hydrogen.
   *
   * @return True if this atom type represents something else than a hydrogen.
   */
  public boolean isHeavy() {
    return heavy;
  }
}
