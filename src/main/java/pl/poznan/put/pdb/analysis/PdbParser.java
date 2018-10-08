package pl.poznan.put.pdb.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbExpdtaLine;
import pl.poznan.put.pdb.PdbHeaderLine;
import pl.poznan.put.pdb.PdbModresLine;
import pl.poznan.put.pdb.PdbParsingException;
import pl.poznan.put.pdb.PdbRemark2Line;
import pl.poznan.put.pdb.PdbRemark465Line;
import pl.poznan.put.pdb.PdbTitleLine;

public class PdbParser implements StructureParser {
  private static final Logger LOGGER = LoggerFactory.getLogger(PdbParser.class);

  private final List<PdbModresLine> modifiedResidues = new ArrayList<>();
  private final List<PdbRemark465Line> missingResidues = new ArrayList<>();
  private final Collection<String> terminatedChainIdentifiers = new HashSet<>();
  private final Collection<Integer> endedModelNumbers = new HashSet<>();
  private final Map<Integer, List<PdbAtomLine>> modelAtoms = new TreeMap<>();
  private final Collection<PdbTitleLine> titleLines = new ArrayList<>();

  private final boolean strictMode;

  private PdbHeaderLine headerLine;
  private PdbExpdtaLine experimentalDataLine;
  private PdbRemark2Line resolutionLine;
  private char currentChainIdentifier;
  private int currentModelNumber;

  public PdbParser(final boolean strictMode) {
    super();
    this.strictMode = strictMode;
  }

  public PdbParser() {
    super();
    strictMode = true;
  }

  @Override
  public synchronized List<PdbModel> parse(final String structureContent)
      throws PdbParsingException {
    resetState();

    for (final String line : structureContent.split("\n")) {
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
      } else if (line.startsWith("TITLE ")) {
        handleTitleLine(line);
      }
    }

    final StringBuilder titleBuilder = new StringBuilder();
    for (final PdbTitleLine titleLine : titleLines) {
      titleBuilder.append(titleLine.getTitle());
    }

    final List<PdbModel> result = new ArrayList<>();

    for (final Map.Entry<Integer, List<PdbAtomLine>> entry : modelAtoms.entrySet()) {
      final int modelNumber = entry.getKey();
      final List<PdbAtomLine> atoms = entry.getValue();
      final PdbModel pdbModel =
          new PdbModel(
              headerLine,
              experimentalDataLine,
              resolutionLine,
              modelNumber,
              atoms,
              modifiedResidues,
              missingResidues,
              titleBuilder.toString());
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
    titleLines.clear();

    // on default, the ' ' chain id is terminated
    terminatedChainIdentifiers.add(" ");

    headerLine = PdbHeaderLine.emptyInstance();
    experimentalDataLine = PdbExpdtaLine.emptyInstance();
    currentChainIdentifier = 'a';
    currentModelNumber = 0;
  }

  private void handleModelLine(final String line) {
    endedModelNumbers.add(currentModelNumber);

    final String modelNumberString =
        (line.length() > 14) ? line.substring(10, 14).trim() : line.substring(5).trim();
    int modelNumber = Integer.parseInt(modelNumberString);

    while (endedModelNumbers.contains(modelNumber)) {
      // model number has four digits
      modelNumber = RandomUtils.nextInt(1, 10000);
    }

    currentModelNumber = modelNumber;
    terminatedChainIdentifiers.clear();
  }

  private void handleAtomLine(final String line) {
    try {
      PdbAtomLine atomLine = PdbAtomLine.parse(line, strictMode);

      if (terminatedChainIdentifiers.contains(atomLine.getChainIdentifier())) {
        atomLine = atomLine.replaceChainIdentifier(Character.toString(currentChainIdentifier));
      }

      if (!modelAtoms.containsKey(currentModelNumber)) {
        modelAtoms.put(currentModelNumber, new ArrayList<>());
      }

      final List<PdbAtomLine> atomList = modelAtoms.get(currentModelNumber);
      atomList.add(atomLine);
    } catch (final PdbParsingException e) {
      PdbParser.LOGGER.warn("Invalid ATOM line: {}", line, e);
    }
  }

  private void handleTitleLine(final String line) {
    try {
      final PdbTitleLine titleLine = PdbTitleLine.parse(line);
      if (((CollectionUtils.isEmpty(titleLines))
              && (StringUtils.isBlank(titleLine.getContinuation())))
          || (StringUtils.isNotBlank(titleLine.getContinuation()))) {
        titleLines.add(titleLine);
      }
    } catch (final PdbParsingException e) {
      PdbParser.LOGGER.warn("Invalid TITLE line: {}", line, e);
    }
  }

  private void handleTerLine(final CharSequence line) {
    String chain = (line.length() > 21) ? Character.toString(line.charAt(21)) : " ";

    if (terminatedChainIdentifiers.contains(chain)) {
      chain = Character.toString(currentChainIdentifier);
      currentChainIdentifier++;
    }

    terminatedChainIdentifiers.add(chain);
  }

  private void handleMissingResidueLine(final String line) {
    try {
      if (PdbRemark465Line.isCommentLine(line)) {
        return;
      }

      final PdbRemark465Line remark465Line = PdbRemark465Line.parse(line);
      missingResidues.add(remark465Line);
    } catch (final PdbParsingException e) {
      PdbParser.LOGGER.warn("Invalid REMARK 465 line: {}", line, e);
    }
  }

  private void handleModifiedResidueLine(final String line) {
    try {
      final PdbModresLine modresLine = PdbModresLine.parse(line);
      modifiedResidues.add(modresLine);
    } catch (final PdbParsingException e) {
      PdbParser.LOGGER.warn("Invalid MODRES line: {}", line, e);
    }
  }

  private void handleHeaderLine(final String line) {
    try {
      headerLine = PdbHeaderLine.parse(line);
    } catch (final PdbParsingException e) {
      PdbParser.LOGGER.warn("Invalid HEADER line: {}", line, e);
    }
  }

  private void handleExperimentalDataLine(final String line) {
    try {
      experimentalDataLine = PdbExpdtaLine.parse(line);
    } catch (final PdbParsingException e) {
      PdbParser.LOGGER.warn("Invalid EXPDTA line: {}", line, e);
    }
  }

  private void handleResolutionLine(final String line) {
    try {
      resolutionLine = PdbRemark2Line.parse(line);
    } catch (final PdbParsingException e) {
      PdbParser.LOGGER.warn("Invalid REMARK   2 RESOLUTION. line: {}", line, e);
    }
  }
}
