package pl.poznan.put.pdb.analysis;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.rcsb.cif.CifIO;
import org.rcsb.cif.model.FloatColumn;
import org.rcsb.cif.model.IntColumn;
import org.rcsb.cif.model.StrColumn;
import org.rcsb.cif.schema.StandardSchemata;
import org.rcsb.cif.schema.mm.AtomSite;
import org.rcsb.cif.schema.mm.Exptl;
import org.rcsb.cif.schema.mm.MmCifBlock;
import org.rcsb.cif.schema.mm.MmCifFile;
import org.rcsb.cif.schema.mm.NdbStructNaBasePair;
import org.rcsb.cif.schema.mm.PdbxStructModResidue;
import org.rcsb.cif.schema.mm.PdbxUnobsOrZeroOccResidues;
import pl.poznan.put.notation.LeontisWesthof;
import pl.poznan.put.notation.Saenger;
import pl.poznan.put.pdb.ExperimentalTechnique;
import pl.poznan.put.pdb.ImmutablePdbAtomLine;
import pl.poznan.put.pdb.ImmutablePdbExpdtaLine;
import pl.poznan.put.pdb.ImmutablePdbHeaderLine;
import pl.poznan.put.pdb.ImmutablePdbModresLine;
import pl.poznan.put.pdb.ImmutablePdbNamedResidueIdentifier;
import pl.poznan.put.pdb.ImmutablePdbRemark2Line;
import pl.poznan.put.pdb.ImmutablePdbRemark465Line;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbExpdtaLine;
import pl.poznan.put.pdb.PdbHeaderLine;
import pl.poznan.put.pdb.PdbModresLine;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.pdb.PdbRemark2Line;
import pl.poznan.put.pdb.PdbRemark465Line;
import pl.poznan.put.structure.BasePair;
import pl.poznan.put.structure.ImmutableBasePair;
import pl.poznan.put.structure.ImmutableQuantifiedBasePair;
import pl.poznan.put.structure.QuantifiedBasePair;

/** A parser of mmCIF format. */
public final class CifParser {
  private PdbHeaderLine header;
  private PdbExpdtaLine experimentalData;
  private PdbRemark2Line resolution;
  private List<PdbModresLine> modifiedResidues;
  private List<PdbRemark465Line> missingResidues;
  private String title;
  private List<QuantifiedBasePair> basePairs;
  private Map<Integer, List<PdbAtomLine>> modelAtoms;

  public CifParser() {
    super();
  }

  /**
   * Parses content in mmCIF format.
   *
   * @param structureContent A string with data in mmCIF format.
   * @return A parsed object representing a molecular structure.
   * @throws IOException When parsing of the data fails.
   */
  public synchronized List<CifModel> parse(final String structureContent) throws IOException {
    header = ImmutablePdbHeaderLine.of("", new Date(), "");
    experimentalData = ImmutablePdbExpdtaLine.of(Collections.emptyList());
    resolution = ImmutablePdbRemark2Line.of(Double.NaN);
    modifiedResidues = Collections.emptyList();
    missingResidues = Collections.emptyList();
    title = "";
    modelAtoms = new TreeMap<>();

    final MmCifFile cifFile =
        CifIO.readFromInputStream(IOUtils.toInputStream(structureContent, StandardCharsets.UTF_8))
            .as(StandardSchemata.MMCIF);
    final MmCifBlock data = cifFile.getFirstBlock();

    handleHeader(data);
    handleExperimentalData(data);
    handleResolution(data);
    handleModifiedResidues(data);
    handleMissingResidues(data);
    handleTitle(data);
    handleBasePairs(data);
    handleAtoms(data);

    final List<CifModel> result = new ArrayList<>(modelAtoms.size());

    for (final Map.Entry<Integer, List<PdbAtomLine>> entry : modelAtoms.entrySet()) {
      result.add(
          ImmutableDefaultCifModel.of(
              header,
              experimentalData,
              resolution,
              entry.getKey(),
              entry.getValue(),
              modifiedResidues,
              missingResidues,
              title,
              Collections.emptyList(),
              basePairs));
    }

    return result;
  }

  private void handleBasePairs(final MmCifBlock data) {
    final NdbStructNaBasePair ndbStructNaBasePair = data.getNdbStructNaBasePair();
    final StrColumn iAuthAsymId = ndbStructNaBasePair.getIAuthAsymId();
    final StrColumn iAuthSeqId = ndbStructNaBasePair.getIAuthSeqId();
    final StrColumn ipdbInsCode = ndbStructNaBasePair.getIPDBInsCode();
    final StrColumn iLabelCompId = ndbStructNaBasePair.getILabelCompId();
    final StrColumn jAuthAsymId = ndbStructNaBasePair.getJAuthAsymId();
    final StrColumn jAuthSeqId = ndbStructNaBasePair.getJAuthSeqId();
    final StrColumn jpdbInsCode = ndbStructNaBasePair.getJPDBInsCode();
    final StrColumn jLabelCompId = ndbStructNaBasePair.getJLabelCompId();
    final FloatColumn shearColumn = ndbStructNaBasePair.getShear();
    final FloatColumn stretchColumn = ndbStructNaBasePair.getStretch();
    final FloatColumn staggerColumn = ndbStructNaBasePair.getStagger();
    final FloatColumn buckleColumn = ndbStructNaBasePair.getBuckle();
    final FloatColumn propellerColumn = ndbStructNaBasePair.getPropeller();
    final FloatColumn openingColumn = ndbStructNaBasePair.getOpening();
    final IntColumn hbondType28 = ndbStructNaBasePair.getHbondType28();
    final IntColumn hbondType12 = ndbStructNaBasePair.getHbondType12();
    final List<QuantifiedBasePair> result = new ArrayList<>();

    for (int i = 0; i < ndbStructNaBasePair.getRowCount(); i++) {
      final String iChainIdentifier = iAuthAsymId.get(i);
      final int iResidueNumber = Integer.parseInt(iAuthSeqId.get(i));
      final String iInsertionCode = ipdbInsCode.get(i);
      final String iResidueName = iLabelCompId.get(i);
      final char iOneLetterName =
          ResidueTypeDetector.detectResidueType(iResidueName, Collections.emptySet())
              .oneLetterName();
      final PdbNamedResidueIdentifier left =
          ImmutablePdbNamedResidueIdentifier.of(
              iChainIdentifier,
              iResidueNumber,
              CifParser.isEmpty(iInsertionCode) ? Optional.empty() : Optional.of(iInsertionCode),
              iOneLetterName);

      final String jChainIdentifier = jAuthAsymId.get(i);
      final int jResidueNumber = Integer.parseInt(jAuthSeqId.get(i));
      final String jInsertionCode = jpdbInsCode.get(i);
      final String jResidueName = jLabelCompId.get(i);
      final char jOneLetterName =
          ResidueTypeDetector.detectResidueType(jResidueName, Collections.emptySet())
              .oneLetterName();
      final PdbNamedResidueIdentifier right =
          ImmutablePdbNamedResidueIdentifier.of(
              jChainIdentifier,
              jResidueNumber,
              CifParser.isEmpty(jInsertionCode) ? Optional.empty() : Optional.of(jInsertionCode),
              jOneLetterName);

      final BasePair basePair = ImmutableBasePair.of(left, right);
      final double shear = shearColumn.isDefined() ? shearColumn.get(i) : Double.NaN;
      final double stretch = stretchColumn.isDefined() ? stretchColumn.get(i) : Double.NaN;
      final double stagger = staggerColumn.isDefined() ? staggerColumn.get(i) : Double.NaN;
      final double buckle = buckleColumn.isDefined() ? buckleColumn.get(i) : Double.NaN;
      final double propeller = propellerColumn.isDefined() ? propellerColumn.get(i) : Double.NaN;
      final double opening = openingColumn.isDefined() ? openingColumn.get(i) : Double.NaN;
      final Saenger saenger = Saenger.fromNumber(hbondType28.get(i));
      final LeontisWesthof leontisWesthof = LeontisWesthof.fromNumber(hbondType12.get(i));
      result.add(
          ImmutableQuantifiedBasePair.of(
                  basePair, shear, stretch, stagger, buckle, propeller, opening)
              .withSaenger(saenger)
              .withLeontisWesthof(leontisWesthof));
    }
    basePairs = result;
  }

  private void handleTitle(final MmCifBlock data) {
    title = data.getStruct().getTitle().values().collect(Collectors.joining("\n"));
  }

  private void handleMissingResidues(final MmCifBlock data) {
    final PdbxUnobsOrZeroOccResidues pdbxUnobsOrZeroOccResidues =
        data.getPdbxUnobsOrZeroOccResidues();
    final IntColumn pdbModelNum = pdbxUnobsOrZeroOccResidues.getPDBModelNum();
    final StrColumn authCompId = pdbxUnobsOrZeroOccResidues.getAuthCompId();
    final StrColumn authAsymId = pdbxUnobsOrZeroOccResidues.getAuthAsymId();
    final StrColumn authSeqId = pdbxUnobsOrZeroOccResidues.getAuthSeqId();
    final StrColumn pdbInsCode = pdbxUnobsOrZeroOccResidues.getPDBInsCode();
    final List<PdbRemark465Line> result = new ArrayList<>();

    for (int i = 0; i < pdbxUnobsOrZeroOccResidues.getRowCount(); i++) {
      final int modelNumber = pdbModelNum.get(i);
      final String residueName = authCompId.get(i);
      final String chainIdentifier = authAsymId.get(i);
      final int residueNumber = Integer.parseInt(authSeqId.get(i));
      final String insertionCode = pdbInsCode.get(i);
      result.add(
          ImmutablePdbRemark465Line.of(
              modelNumber,
              residueName,
              chainIdentifier,
              residueNumber,
              CifParser.isEmpty(insertionCode) ? Optional.empty() : Optional.of(insertionCode)));
    }

    missingResidues = result;
  }

  private void handleModifiedResidues(final MmCifBlock data) {
    final PdbxStructModResidue pdbxStructModResidue = data.getPdbxStructModResidue();
    final StrColumn authCompId = pdbxStructModResidue.getAuthCompId();
    final StrColumn authAsymId = pdbxStructModResidue.getAuthAsymId();
    final IntColumn authSeqId = pdbxStructModResidue.getAuthSeqId();
    final StrColumn pdbInsCode = pdbxStructModResidue.getPDBInsCode();
    final StrColumn parentCompId = pdbxStructModResidue.getParentCompId();
    final StrColumn details = pdbxStructModResidue.getDetails();
    final List<PdbModresLine> result = new ArrayList<>();

    for (int i = 0; i < pdbxStructModResidue.getRowCount(); i++) {
      final String residueName = authCompId.get(i);
      final String chainIdentifier = authAsymId.get(i);
      final int residueNumber = authSeqId.get(i);
      final String insertionCode = pdbInsCode.get(i);
      final String standardResidueName = parentCompId.get(i);
      final String comment = details.get(i);
      result.add(
          ImmutablePdbModresLine.of(
              header.idCode(),
              residueName,
              chainIdentifier,
              residueNumber,
              CifParser.isEmpty(insertionCode) ? Optional.empty() : Optional.of(insertionCode),
              standardResidueName,
              comment));
    }

    modifiedResidues = result;
  }

  private void handleResolution(final MmCifBlock data) {
    resolution =
        ImmutablePdbRemark2Line.of(
            data.getRefine().getLsDResHigh().values().min().orElse(Double.NaN));
  }

  private void handleExperimentalData(final MmCifBlock data) {
    final Exptl exptl = data.getExptl();
    final List<ExperimentalTechnique> experimentalTechniques =
        exptl
            .getMethod()
            .values()
            .map(ExperimentalTechnique::fromFullName)
            .collect(Collectors.toList());
    experimentalData = ImmutablePdbExpdtaLine.of(experimentalTechniques);
  }

  private void handleHeader(final MmCifBlock data) {
    Date depositionData;
    try {
      depositionData =
          new SimpleDateFormat("yyyy-MM-dd")
              .parse(
                  data.getPdbxDatabaseStatus()
                      .getRecvdInitialDepositionDate()
                      .values()
                      .collect(Collectors.joining("\n")));
    } catch (final ParseException ignored) {
      depositionData = new Date();
    }
    final String classification =
        data.getStructKeywords().getPdbxKeywords().values().collect(Collectors.joining("\n"));
    final String idCode = data.getEntry().getId().values().collect(Collectors.joining("\n"));
    header = ImmutablePdbHeaderLine.of(classification, depositionData, idCode);
  }

  private void handleAtoms(final MmCifBlock data) {
    final AtomSite atomSite = data.getAtomSite();

    final IntColumn id = atomSite.getId();
    final StrColumn authAtomId = atomSite.getAuthAtomId();
    final StrColumn pdbxAuthAltId = atomSite.getPdbxAuthAltId();
    final StrColumn authCompId = atomSite.getAuthCompId();
    final StrColumn authAsymId = atomSite.getAuthAsymId();
    final IntColumn authSeqId = atomSite.getAuthSeqId();
    final StrColumn pdbxPDBInsCode = atomSite.getPdbxPDBInsCode();
    final FloatColumn cartnX = atomSite.getCartnX();
    final FloatColumn cartnY = atomSite.getCartnY();
    final FloatColumn cartnZ = atomSite.getCartnZ();
    final FloatColumn occupancyColumn = atomSite.getOccupancy();
    final FloatColumn bIsoOrEquiv = atomSite.getBIsoOrEquiv();
    final StrColumn typeSymbol = atomSite.getTypeSymbol();
    final IntColumn pdbxFormalCharge = atomSite.getPdbxFormalCharge();
    final IntColumn pdbxPDBModelNum = atomSite.getPdbxPDBModelNum();

    for (int i = 0; i < atomSite.getRowCount(); i++) {
      final int serialNumber = id.get(i);
      final String atomName = authAtomId.get(i);
      final String alternateLocation = pdbxAuthAltId.isDefined() ? pdbxAuthAltId.get(i) : "?";
      final String residueName = authCompId.get(i);
      final String chainIdentifier = authAsymId.get(i);
      final int residueNumber = authSeqId.get(i);
      final String insertionCode = pdbxPDBInsCode.get(i);
      final double x = cartnX.get(i);
      final double y = cartnY.get(i);
      final double z = cartnZ.get(i);
      final double occupancy = occupancyColumn.get(i);
      final double temperatureFactor = bIsoOrEquiv.get(i);
      final String elementSymbol = typeSymbol.get(i);
      final String charge = Integer.toString(pdbxFormalCharge.get(i));
      final int model = pdbxPDBModelNum.isDefined() ? pdbxPDBModelNum.get(i) : 1;

      final ImmutablePdbAtomLine atomLine =
          ImmutablePdbAtomLine.of(
              serialNumber,
              atomName,
              CifParser.isEmpty(alternateLocation)
                  ? Optional.empty()
                  : Optional.of(alternateLocation),
              residueName,
              chainIdentifier,
              residueNumber,
              CifParser.isEmpty(insertionCode) ? Optional.empty() : Optional.of(insertionCode),
              x,
              y,
              z,
              occupancy,
              temperatureFactor,
              elementSymbol,
              charge);

      if (!modelAtoms.containsKey(model)) {
        modelAtoms.put(model, new ArrayList<>());
      }

      modelAtoms.get(model).add(atomLine);
    }
  }

  private static boolean isEmpty(final String value) {
    return "".equals(value) || ".".equals(value) || "?".equals(value);
  }
}
