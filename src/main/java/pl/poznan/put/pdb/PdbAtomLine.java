package pl.poznan.put.pdb;

import java.util.Locale;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.analysis.PdbResidueIdentifier;

public class PdbAtomLine implements ChainNumberICode {
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
    // @formatter:on
    private static final String FORMAT = "ATOM  %5d %4s%c%3s %c%4d%c   %8.3f%8.3f%8.3f%6.2f%6.2f          %2s%2s";

    public static PdbAtomLine parse(String line) throws PdbParsingException {
        if (line.length() < 80) {
            throw new PdbParsingException("PDB ATOM line is not at least 80 character long");
        }

        try {
            String recordName = line.substring(0, 6).trim();

            if (!"ATOM".equals(recordName) && !"HETATM".equals(recordName)) {
                throw new PdbParsingException("PDB line does not start with ATOM or HETATM");
            }

            int serialNumber = Integer.parseInt(line.substring(6, 11).trim());
            String atomName = line.substring(12, 16).trim();
            char alternateLocation = line.charAt(16);
            String residueName = line.substring(17, 20).trim();
            char chainIdentifier = line.charAt(21);
            int residueNumber = Integer.parseInt(line.substring(22, 26).trim());
            char insertionCode = line.charAt(26);
            double x = Double.parseDouble(line.substring(30, 38).trim());
            double y = Double.parseDouble(line.substring(38, 46).trim());
            double z = Double.parseDouble(line.substring(46, 54).trim());
            double occupancy = Double.parseDouble(line.substring(54, 60).trim());
            double temperatureFactor = Double.parseDouble(line.substring(60, 66).trim());
            String elementSymbol = line.substring(76, 78).trim();
            String charge = line.substring(78, 80).trim();
            return new PdbAtomLine(serialNumber, atomName, alternateLocation, residueName, chainIdentifier, residueNumber, insertionCode, x, y, z, occupancy, temperatureFactor, elementSymbol, charge);
        } catch (NumberFormatException e) {
            throw new PdbParsingException("Failed to parse PDB ATOM line", e);
        }
    }

    private final String recordName = "ATOM";
    private final int serialNumber;
    private final String atomName;
    private final char alternateLocation;
    private final String residueName;
    private final char chainIdentifier;
    private final int residueNumber;
    private final char insertionCode;
    private final double x;
    private final double y;
    private final double z;
    private final double occupancy;
    private final double temperatureFactor;
    private final String elementSymbol;
    private final String charge;

    public PdbAtomLine(int serialNumber, String atomName, char alternateLocation, String residueName, char chainIdentifier, int residueNumber, char insertionCode, double x, double y, double z, double occupancy, double temperatureFactor, String elementSymbol, String charge) {
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

    public String getRecordName() {
        return recordName;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public String getAtomName() {
        return atomName;
    }

    public char getAlternateLocation() {
        return alternateLocation;
    }

    public String getResidueName() {
        return residueName;
    }

    @Override
    public char getChainIdentifier() {
        return chainIdentifier;
    }

    @Override
    public int getResidueNumber() {
        return residueNumber;
    }

    @Override
    public char getInsertionCode() {
        return insertionCode;
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
        return String.format(Locale.US, PdbAtomLine.FORMAT, serialNumber, atomName, alternateLocation, residueName, chainIdentifier, residueNumber, insertionCode, x, y, z, occupancy, temperatureFactor, elementSymbol, charge);
    }

    @Override
    public PdbResidueIdentifier getResidueIdentifier() {
        return new PdbResidueIdentifier(chainIdentifier, residueNumber, insertionCode);
    }

    public PdbAtomLine replaceChainIdentifier(char chainIdentifierNew) {
        return new PdbAtomLine(serialNumber, atomName, alternateLocation, residueName, chainIdentifierNew, residueNumber, insertionCode, x, y, z, occupancy, temperatureFactor, elementSymbol, charge);
    }

    public AtomName detectAtomName() {
        return AtomName.fromString(atomName);
    }
}
