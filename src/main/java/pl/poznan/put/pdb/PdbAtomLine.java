package pl.poznan.put.pdb;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.biojava.nbio.structure.Atom;
import org.biojava.nbio.structure.AtomImpl;
import org.biojava.nbio.structure.Element;
import org.biojava.nbio.structure.Group;
import org.biojava.nbio.structure.HetatomImpl;
import org.biojava.nbio.structure.ResidueNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.poznan.put.atom.AtomName;

import java.io.Serializable;
import java.util.Locale;

public class PdbAtomLine implements Serializable, ChainNumberICode {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(PdbAtomLine.class);

    // @formatter:off
    /*
        COLUMNS        DATA  TYPE    FIELD        DEFINITION
        -------------------------------------------------------------------------------------
         1 -  6        Record name   "ATOM  "
         7 - 11        Integer       serial       Atom  serial number.
        13 - 16        Atom          name         Atom name.
        17             Character     altLoc       Alternate location indicator.
        18 - 20        Residue name  resName      Residue name.
        22             Character     chainID      Chain identifier.
        23 - 26        Integer       resSeq       Residue sequence number.
        27             AChar         iCode        Code for insertion of residues.
        31 - 38        Real(8.3)     x            Orthogonal coordinates for X in Angstroms.
        39 - 46        Real(8.3)     y            Orthogonal coordinates for Y in Angstroms.
        47 - 54        Real(8.3)     z            Orthogonal coordinates for Z in Angstroms.
        55 - 60        Real(6.2)     occupancy    Occupancy.
        61 - 66        Real(6.2)     tempFactor   Temperature  factor.
        77 - 78        LString(2)    element      Element symbol, right-justified.
        79 - 80        LString(2)    charge       Charge  on the atom.
     */
    private static final String FORMAT_ATOM_4_CHARACTER
            = "ATOM  %5d %-4s%c%3s %c%4d%c   %8.3f%8.3f%8.3f%6.2f%6.2f         %2s%2s";
    private static final String FORMAT
            = "ATOM  %5d  %-3s%c%3s %c%4d%c   %8.3f%8.3f%8.3f%6.2f%6.2f        %2s%2s";
    public static final String CIF_LOOP
            = "loop_\n" +
              "_atom_site.id\n" +
              "_atom_site.auth_atom_id\n" +
              "_atom_site.label_alt_id\n" +
              "_atom_site.auth_comp_id\n" +
              "_atom_site.auth_asym_id\n" +
              "_atom_site.auth_seq_id\n" +
              "_atom_site.pdbx_PDB_ins_code\n" +
              "_atom_site.Cartn_x\n" +
              "_atom_site.Cartn_y\n" +
              "_atom_site.Cartn_z\n" +
              "_atom_site.occupancy\n" +
              "_atom_site.B_iso_or_equiv\n" +
              "_atom_site.type_symbol\n" +
              "_atom_site.pdbx_formal_charge";
    // @formatter:on

    private static final String RECORD_NAME = "ATOM";
    private final int serialNumber;
    private final String atomName;
    private final String alternateLocation;
    private final String residueName;
    private final String chainIdentifier;
    private final int residueNumber;
    private final String insertionCode;
    private final double x;
    private final double y;
    private final double z;
    private final double occupancy;
    private final double temperatureFactor;
    private final String elementSymbol;
    private final String charge;

    public PdbAtomLine(int serialNumber, String atomName,
                       String alternateLocation, String residueName,
                       String chainIdentifier, int residueNumber,
                       String insertionCode, double x, double y, double z,
                       double occupancy, double temperatureFactor,
                       String elementSymbol, String charge) {
        super();
        this.serialNumber = serialNumber;
        this.atomName = atomName;
        this.alternateLocation = alternateLocation;
        this.residueName = residueName;
        this.chainIdentifier = chainIdentifier;
        this.residueNumber = residueNumber;
        this.insertionCode = insertionCode;
        this.x = x;
        this.y = y;
        this.z = z;
        this.occupancy = occupancy;
        this.temperatureFactor = temperatureFactor;
        this.elementSymbol = elementSymbol;
        this.charge = charge;
    }

    public static PdbAtomLine fromBioJavaAtom(Atom atom) {
        Group group = atom.getGroup();
        String residueName = group.getPDBName();
        ResidueNumber residueNumberObject = group.getResidueNumber();
        String chainIdentifier = residueNumberObject.getChainId();
        int residueNumber = residueNumberObject.getSeqNum();
        String insertionCode = residueNumberObject.getInsCode() == null ? " "
                                                                        :
                               Character
                                       .toString(residueNumberObject
                                                         .getInsCode());

        int serialNumber = atom.getPDBserial();
        String atomName = atom.getName();
        String alternateLocation = atom.getAltLoc() == null ? " " : Character
                .toString(atom.getAltLoc());
        double x = atom.getX();
        double y = atom.getY();
        double z = atom.getZ();
        double occupancy = atom.getOccupancy();
        double temperatureFactor = atom.getTempFactor();
        String elementSymbol = atom.getElement().name();
        String charge = "";
        return new PdbAtomLine(serialNumber, atomName, alternateLocation,
                               residueName, chainIdentifier, residueNumber,
                               insertionCode, x, y, z, occupancy,
                               temperatureFactor, elementSymbol, charge);
    }

    public static PdbAtomLine parse(String line) throws PdbParsingException {
        return PdbAtomLine.parse(line, true);
    }

    public static PdbAtomLine parse(String line, boolean strictMode)
            throws PdbParsingException {
        // in non-strict mode, only up to X, Y, Z fields are required, rest is
        // optional
        if (strictMode && line.length() < 80
                || !strictMode && line.length() < 54) {
            throw new PdbParsingException("PDB ATOM line is too short");
        }

        try {
            String recordName = line.substring(0, 6).trim();

            if (!"ATOM".equals(recordName) && !"HETATM".equals(recordName)) {
                throw new PdbParsingException(
                        "PDB line does not start with ATOM or HETATM");
            }

            int serialNumber = Integer.parseInt(line.substring(6, 11).trim());
            String atomName = line.substring(12, 16).trim();
            String alternateLocation = Character.toString(line.charAt(16));
            String residueName = line.substring(17, 20).trim();
            String chainIdentifier = Character.toString(line.charAt(21));
            int residueNumber = Integer.parseInt(line.substring(22, 26).trim());
            String insertionCode = Character.toString(line.charAt(26));
            double x = Double.parseDouble(line.substring(30, 38).trim());
            double y = Double.parseDouble(line.substring(38, 46).trim());
            double z = Double.parseDouble(line.substring(46, 54).trim());

            double occupancy = line.length() >= 60 && StringUtils
                    .isNotBlank(line.substring(54, 60)) ? Double.parseDouble(
                    line.substring(54, 60).trim()) : 0;
            double temperatureFactor = line.length() >= 66 && StringUtils
                    .isNotBlank(line.substring(60, 66)) ? Double.parseDouble(
                    line.substring(60, 66).trim()) : 0;
            String elementSymbol = line.length() >= 78 ? line.substring(76, 78)
                                                             .trim() : "";
            String charge = line.length() >= 80 ? line.substring(78, 80).trim()
                                                : "";

            return new PdbAtomLine(serialNumber, atomName, alternateLocation,
                                   residueName, chainIdentifier, residueNumber,
                                   insertionCode, x, y, z, occupancy,
                                   temperatureFactor, elementSymbol, charge);
        } catch (NumberFormatException e) {
            throw new PdbParsingException("Failed to parse PDB ATOM line", e);
        }
    }

    public static String getRecordName() {
        return PdbAtomLine.RECORD_NAME;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public String getAtomName() {
        return atomName;
    }

    public String getAlternateLocation() {
        return alternateLocation;
    }

    public String getResidueName() {
        return residueName;
    }

    @Override
    public String getChainIdentifier() {
        return chainIdentifier;
    }

    @Override
    public int getResidueNumber() {
        return residueNumber;
    }

    @Override
    public String getInsertionCode() {
        return insertionCode;
    }

    @Override
    public PdbResidueIdentifier getResidueIdentifier() {
        return new PdbResidueIdentifier(chainIdentifier, residueNumber,
                                        insertionCode);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getOccupancy() {
        return occupancy;
    }

    public double getTemperatureFactor() {
        return temperatureFactor;
    }

    public String getElementSymbol() {
        return elementSymbol;
    }

    public String getCharge() {
        return charge;
    }

    @Override
    public String toString() {
        if (alternateLocation.length() != 1) {
            PdbAtomLine.LOGGER
                    .error("Field 'alternateLocation' is longer than 1 char. "
                                   + "Only first letter will be taken");
        }
        if (chainIdentifier.length() != 1) {
            PdbAtomLine.LOGGER
                    .error("Field 'chainIdentifier' is longer than 1 char. "
                                   + "Only first letter will be taken");
        }
        if (insertionCode.length() != 1) {
            PdbAtomLine.LOGGER
                    .error("Field 'insertionCode' is longer than 1 char. Only"
                                   + " first letter will be taken");
        }

        String format = atomName.length() == 4
                        ? PdbAtomLine.FORMAT_ATOM_4_CHARACTER
                        : PdbAtomLine.FORMAT;
        return String.format(Locale.US, format, serialNumber, atomName,
                             alternateLocation.charAt(0), residueName,
                             chainIdentifier.charAt(0), residueNumber,
                             insertionCode.charAt(0), x, y, z, occupancy,
                             temperatureFactor, elementSymbol, charge);
    }

    public PdbAtomLine replaceSerialNumber(int serialNumberNew) {
        return new PdbAtomLine(serialNumberNew, atomName, alternateLocation,
                               residueName, chainIdentifier, residueNumber,
                               insertionCode, x, y, z, occupancy,
                               temperatureFactor, elementSymbol, charge);
    }

    public PdbAtomLine replaceChainIdentifier(String chainIdentifierNew) {
        return new PdbAtomLine(serialNumber, atomName, alternateLocation,
                               residueName, chainIdentifierNew, residueNumber,
                               insertionCode, x, y, z, occupancy,
                               temperatureFactor, elementSymbol, charge);
    }

    public PdbAtomLine replaceResidueNumber(int residueNumberNew) {
        return new PdbAtomLine(serialNumber, atomName, alternateLocation,
                               residueName, chainIdentifier, residueNumberNew,
                               insertionCode, x, y, z, occupancy,
                               temperatureFactor, elementSymbol, charge);
    }

    public AtomName detectAtomName() {
        return AtomName.fromString(atomName);
    }

    public double distanceTo(PdbAtomLine other) {
        Vector3D v1 = new Vector3D(x, y, z);
        Vector3D v2 = new Vector3D(other.x, other.y, other.z);
        return v1.distance(v2);
    }

    public Atom toBioJavaAtom() throws CifPdbIncompatibilityException {
        if (alternateLocation.length() != 1) {
            throw new CifPdbIncompatibilityException(
                    "Cannot convert to PDB. Field 'alternateLocation' is "
                            + "longer than 1 char");
        }
        if (insertionCode.length() != 1) {
            throw new CifPdbIncompatibilityException(
                    "Cannot convert to PDB. Field 'insertionCode' is longer "
                            + "than 1 char");
        }

        Group group = new HetatomImpl();
        group.setResidueNumber(String.valueOf(chainIdentifier), residueNumber,
                               " ".equals(insertionCode) ? null : insertionCode
                                       .charAt(0));
        group.setPDBName(residueName);

        Atom atom = new AtomImpl();
        atom.setPDBserial(serialNumber);
        atom.setName(atomName.length() == 4 ? atomName
                                            : String.format(" %-3s", atomName));
        atom.setAltLoc(alternateLocation.charAt(0));
        atom.setX(x);
        atom.setY(y);
        atom.setZ(z);
        atom.setOccupancy((float) occupancy);
        atom.setTempFactor((float) temperatureFactor);
        atom.setElement(Element.valueOfIgnoreCase(elementSymbol));
        atom.setGroup(group);
        return atom;
    }

    public String toCif() {
        StringBuilder builder = new StringBuilder();
        builder.append(serialNumber).append(' ');
        builder.append(atomName).append(' ');
        if (StringUtils.isNotBlank(alternateLocation)) {
            builder.append(alternateLocation).append(' ');
        } else {
            builder.append(". ");
        }
        builder.append(residueName).append(' ');
        builder.append(chainIdentifier).append(' ');
        builder.append(residueNumber).append(' ');
        if (StringUtils.isNotBlank(insertionCode)) {
            builder.append(insertionCode).append(' ');
        } else {
            builder.append("? ");
        }
        builder.append(x).append(' ');
        builder.append(y).append(' ');
        builder.append(z).append(' ');
        builder.append(occupancy).append(' ');
        builder.append(temperatureFactor).append(' ');
        builder.append(elementSymbol).append(' ');
        if (StringUtils.isNotBlank(charge)) {
            builder.append(charge).append(' ');
        } else {
            builder.append('?');
        }
        return builder.toString();
    }
}
