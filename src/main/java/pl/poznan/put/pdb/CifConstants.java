package pl.poznan.put.pdb;

/** Useful constants when working with mmCIF files. */
public final class CifConstants {
  public static final String CIF_LOOP =
      "loop_\n"
          + "_atom_site.group_PDB\n"
          + "_atom_site.id\n"
          + "_atom_site.auth_atom_id\n"
          + "_atom_site.label_alt_id\n"
          + "_atom_site.auth_comp_id\n"
          + "_atom_site.auth_asym_id\n"
          + "_atom_site.auth_seq_id\n"
          + "_atom_site.pdbx_PDB_ins_code\n"
          + "_atom_site.Cartn_x\n"
          + "_atom_site.Cartn_y\n"
          + "_atom_site.Cartn_z\n"
          + "_atom_site.occupancy\n"
          + "_atom_site.B_iso_or_equiv\n"
          + "_atom_site.type_symbol\n"
          + "_atom_site.pdbx_formal_charge";

  private CifConstants() {
    super();
  }
}
