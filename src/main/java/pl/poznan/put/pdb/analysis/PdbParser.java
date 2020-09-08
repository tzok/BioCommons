package pl.poznan.put.pdb.analysis;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.poznan.put.pdb.ImmutablePdbExpdtaLine;
import pl.poznan.put.pdb.ImmutablePdbHeaderLine;
import pl.poznan.put.pdb.ImmutablePdbRemark2Line;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbExpdtaLine;
import pl.poznan.put.pdb.PdbHeaderLine;
import pl.poznan.put.pdb.PdbModresLine;
import pl.poznan.put.pdb.PdbParsingException;
import pl.poznan.put.pdb.PdbRemark2Line;
import pl.poznan.put.pdb.PdbRemark465Line;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.pdb.PdbTitleLine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class PdbParser {
  private static final Logger LOGGER = LoggerFactory.getLogger(PdbParser.class);

  private final List<PdbModresLine> modifiedResidues = new ArrayList<>();
  private final List<PdbRemark465Line> missingResidues = new ArrayList<>();
  private final Collection<PdbResidueIdentifier> processedIdentifiers = new HashSet<>();
  private final List<PdbAtomLine> chainTerminatedAfter = new ArrayList<>();
  private final Collection<Integer> endedModelNumbers = new HashSet<>();
  private final Map<Integer, List<PdbAtomLine>> modelAtoms = new TreeMap<>();
  private final Collection<PdbTitleLine> titleLines = new ArrayList<>();

  private final boolean strictMode;

  private Optional<PdbHeaderLine> headerLine = Optional.empty();
  private Optional<PdbExpdtaLine> experimentalDataLine = Optional.empty();
  private Optional<PdbRemark2Line> resolutionLine = Optional.empty();
  private Optional<PdbResidueIdentifier> currentIdentifier = Optional.empty();
  private int currentModelNumber;

  public PdbParser(final boolean strictMode) {
    super();
    this.strictMode = strictMode;
  }

  public PdbParser() {
    super();
    strictMode = true;
  }

  public final synchronized List<StructureModel> parse(final String structureContent) {
    resetState();

    for (final String line : structureContent.split("\n")) {
      if (line.startsWith("MODEL")) {
        handleModelLine(line);
      } else if (line.startsWith("ATOM") || line.startsWith("HETATM")) {
        handleAtomLine(line);
      } else if (line.startsWith("TER   ")) {
        handleTerLine();
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

    final String titleBuilder =
        titleLines.stream().map(PdbTitleLine::title).collect(Collectors.joining());

    final List<StructureModel> result = new ArrayList<>();

    for (final Map.Entry<Integer, List<PdbAtomLine>> entry : modelAtoms.entrySet()) {
      final int modelNumber = entry.getKey();
      final List<PdbAtomLine> atoms = entry.getValue();
      final StructureModel structureModel =
          ImmutablePdbModel.of(
              headerLine.orElse(ImmutablePdbHeaderLine.of("", new Date(0L), "")),
              experimentalDataLine.orElse(ImmutablePdbExpdtaLine.of(Collections.emptyList())),
              resolutionLine.orElse(ImmutablePdbRemark2Line.of(Double.NaN)),
              modelNumber,
              atoms,
              modifiedResidues,
              missingResidues,
              titleBuilder,
              chainTerminatedAfter);
      result.add(structureModel);
    }

    return result;
  }

  private void resetState() {
    modifiedResidues.clear();
    missingResidues.clear();
    processedIdentifiers.clear();
    chainTerminatedAfter.clear();
    endedModelNumbers.clear();
    modelAtoms.clear();
    titleLines.clear();

    headerLine = Optional.empty();
    experimentalDataLine = Optional.empty();
    currentModelNumber = 0;
    currentIdentifier = Optional.empty();
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

    processedIdentifiers.clear();
    chainTerminatedAfter.clear();
    currentIdentifier = Optional.empty();
  }

  private void handleAtomLine(final String line) {
    try {
      final PdbAtomLine atomLine = PdbAtomLine.parse(line, strictMode);
      final PdbResidueIdentifier identifier = atomLine.toResidueIdentifer();

      if (processedIdentifiers.contains(identifier)) {
        PdbParser.LOGGER.warn("Duplicate residue, ignoring it: {}", identifier);
        return;
      }

      if (currentIdentifier.isPresent() && !identifier.equals(currentIdentifier.get())) {
        processedIdentifiers.add(currentIdentifier.get());
        currentIdentifier = Optional.of(identifier);
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
      if (((CollectionUtils.isEmpty(titleLines)) && (StringUtils.isBlank(titleLine.continuation())))
          || (StringUtils.isNotBlank(titleLine.continuation()))) {
        titleLines.add(titleLine);
      }
    } catch (final PdbParsingException e) {
      PdbParser.LOGGER.warn("Invalid TITLE line: {}", line, e);
    }
  }

  private void handleTerLine() {
    final List<PdbAtomLine> atomLines = modelAtoms.get(currentModelNumber);
    chainTerminatedAfter.add(atomLines.get(atomLines.size() - 1));
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
      headerLine = Optional.of(PdbHeaderLine.parse(line));
    } catch (final PdbParsingException e) {
      PdbParser.LOGGER.warn("Invalid HEADER line: {}", line, e);
    }
  }

  private void handleExperimentalDataLine(final String line) {
    try {
      experimentalDataLine = Optional.of(PdbExpdtaLine.parse(line));
    } catch (final PdbParsingException e) {
      PdbParser.LOGGER.warn("Invalid EXPDTA line: {}", line, e);
    }
  }

  private void handleResolutionLine(final String line) {
    try {
      resolutionLine = Optional.of(PdbRemark2Line.parse(line));
    } catch (final PdbParsingException e) {
      PdbParser.LOGGER.warn("Invalid REMARK   2 RESOLUTION. line: {}", line, e);
    }
  }
}
