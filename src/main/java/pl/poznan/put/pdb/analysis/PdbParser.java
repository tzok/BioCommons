package pl.poznan.put.pdb.analysis;

import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbExpdtaLine;
import pl.poznan.put.pdb.PdbHeaderLine;
import pl.poznan.put.pdb.PdbModresLine;
import pl.poznan.put.pdb.PdbParsingException;
import pl.poznan.put.pdb.PdbRemark2Line;
import pl.poznan.put.pdb.PdbRemark465Line;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class PdbParser {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(PdbParser.class);

    private final List<PdbModresLine> modifiedResidues = new ArrayList<>();
    private final List<PdbRemark465Line> missingResidues = new ArrayList<>();
    private final Set<String> terminatedChainIdentifiers = new HashSet<>();
    private final Set<Integer> endedModelNumbers = new HashSet<>();
    private final Map<Integer, List<PdbAtomLine>> modelAtoms = new TreeMap<>();

    private final boolean strictMode;

    private PdbHeaderLine headerLine;
    private PdbExpdtaLine experimentalDataLine;
    private PdbRemark2Line resolutionLine;
    private char currentChainIdentifier;
    private int currentModelNumber;

    public PdbParser(boolean strictMode) {
        super();
        this.strictMode = strictMode;
    }

    public PdbParser() {
        super();
        this.strictMode = true;
    }

    public synchronized List<PdbModel> parse(String pdbFileContent)
            throws PdbParsingException {
        resetState();

        for (String line : pdbFileContent.split("\n")) {
            if (line.startsWith("MODEL")) {
                handleModelLine(line);
            } else if (line.startsWith("ATOM") || line.startsWith("HETATM")) {
                handleAtomLine(line);
            } else if (line.startsWith("TER   ")) {
                handleTerLine(line);
            } else if (line.startsWith("REMARK 465")) {
                handleMissingResidueLine(line);
            } else if (line.startsWith("MODRES")) {
                handleModifiedResidueLine(line);
            } else if (line.startsWith("HEADER")) {
                handleHeaderLine(line);
            } else if (line.startsWith("EXPDTA")) {
                handleExperimentalDataLine(line);
            } else if (line.startsWith("REMARK   2 RESOLUTION.")) {
                handleResolutionLine(line);
            }
        }

        List<PdbModel> result = new ArrayList<>();

        for (Entry<Integer, List<PdbAtomLine>> entry : modelAtoms.entrySet()) {
            int modelNumber = entry.getKey();
            List<PdbAtomLine> atoms = entry.getValue();
            PdbModel pdbModel = new PdbModel(headerLine, experimentalDataLine,
                                             resolutionLine, modelNumber, atoms,
                                             modifiedResidues, missingResidues);
            result.add(pdbModel);
        }

        return result;
    }

    private void resetState() {
        modifiedResidues.clear();
        missingResidues.clear();
        terminatedChainIdentifiers.clear();
        endedModelNumbers.clear();
        modelAtoms.clear();

        // on default, the ' ' chain id is terminated
        terminatedChainIdentifiers.add(" ");

        headerLine = PdbHeaderLine.emptyInstance();
        experimentalDataLine = PdbExpdtaLine.emptyInstance();
        currentChainIdentifier = 'a';
        currentModelNumber = 0;
    }

    private void handleModelLine(String line) {
        endedModelNumbers.add(currentModelNumber);

        String modelNumberString =
                line.length() > 14 ? line.substring(10, 14).trim()
                                   : line.substring(5).trim();
        int modelNumber = Integer.parseInt(modelNumberString);

        while (endedModelNumbers.contains(modelNumber)) {
            // model number has four digits
            modelNumber = RandomUtils.nextInt(1, 10000);
        }

        currentModelNumber = modelNumber;
        terminatedChainIdentifiers.clear();
    }

    private void handleAtomLine(String line) {
        try {
            PdbAtomLine atomLine = PdbAtomLine.parse(line, strictMode);

            if (terminatedChainIdentifiers
                    .contains(atomLine.getChainIdentifier())) {
                atomLine = atomLine.replaceChainIdentifier(
                        Character.toString(currentChainIdentifier));
            }

            if (!modelAtoms.containsKey(currentModelNumber)) {
                modelAtoms
                        .put(currentModelNumber, new ArrayList<PdbAtomLine>());
            }

            List<PdbAtomLine> atomList = modelAtoms.get(currentModelNumber);
            atomList.add(atomLine);
        } catch (PdbParsingException e) {
            LOGGER.warn("Invalid ATOM line: " + line, e);
        }
    }

    private void handleTerLine(String line) {
        String chain =
                line.length() > 21 ? Character.toString(line.charAt(21)) : " ";

        if (terminatedChainIdentifiers.contains(chain)) {
            chain = Character.toString(currentChainIdentifier++);
        }

        terminatedChainIdentifiers.add(chain);
    }

    private void handleMissingResidueLine(String line) {
        try {
            if (PdbRemark465Line.isCommentLine(line)) {
                return;
            }

            PdbRemark465Line remark465Line = PdbRemark465Line.parse(line);
            missingResidues.add(remark465Line);
        } catch (PdbParsingException e) {
            LOGGER.warn("Invalid REMARK 465 line: " + line, e);
        }
    }

    private void handleModifiedResidueLine(String line) {
        try {
            PdbModresLine modresLine = PdbModresLine.parse(line);
            modifiedResidues.add(modresLine);
        } catch (PdbParsingException e) {
            LOGGER.warn("Invalid MODRES line: " + line, e);
        }
    }

    private void handleHeaderLine(String line) {
        try {
            headerLine = PdbHeaderLine.parse(line);
        } catch (PdbParsingException e) {
            LOGGER.warn("Invalid HEADER line: " + line, e);
        }
    }

    private void handleExperimentalDataLine(String line) {
        try {
            experimentalDataLine = PdbExpdtaLine.parse(line);
        } catch (PdbParsingException e) {
            LOGGER.warn("Invalid EXPDTA line: " + line, e);
        }
    }

    private void handleResolutionLine(String line) {
        try {
            resolutionLine = PdbRemark2Line.parse(line);
        } catch (PdbParsingException e) {
            LOGGER.warn("Invalid REMARK   2 RESOLUTION. line: " + line, e);
        }
    }
}
