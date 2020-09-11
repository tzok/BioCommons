package pl.poznan.put.pdb.analysis;

import org.apache.commons.lang3.StringUtils;
import org.biojava.nbio.structure.io.FileParsingParameters;
import org.biojava.nbio.structure.io.mmcif.MMcifConsumer;
import org.biojava.nbio.structure.io.mmcif.model.AtomSite;
import org.biojava.nbio.structure.io.mmcif.model.AtomSites;
import org.biojava.nbio.structure.io.mmcif.model.AuditAuthor;
import org.biojava.nbio.structure.io.mmcif.model.Cell;
import org.biojava.nbio.structure.io.mmcif.model.ChemComp;
import org.biojava.nbio.structure.io.mmcif.model.ChemCompAtom;
import org.biojava.nbio.structure.io.mmcif.model.ChemCompBond;
import org.biojava.nbio.structure.io.mmcif.model.ChemCompDescriptor;
import org.biojava.nbio.structure.io.mmcif.model.DatabasePDBremark;
import org.biojava.nbio.structure.io.mmcif.model.DatabasePDBrev;
import org.biojava.nbio.structure.io.mmcif.model.DatabasePdbrevRecord;
import org.biojava.nbio.structure.io.mmcif.model.Entity;
import org.biojava.nbio.structure.io.mmcif.model.EntityPoly;
import org.biojava.nbio.structure.io.mmcif.model.EntityPolySeq;
import org.biojava.nbio.structure.io.mmcif.model.EntitySrcGen;
import org.biojava.nbio.structure.io.mmcif.model.EntitySrcNat;
import org.biojava.nbio.structure.io.mmcif.model.EntitySrcSyn;
import org.biojava.nbio.structure.io.mmcif.model.Exptl;
import org.biojava.nbio.structure.io.mmcif.model.PdbxAuditRevisionHistory;
import org.biojava.nbio.structure.io.mmcif.model.PdbxChemCompDescriptor;
import org.biojava.nbio.structure.io.mmcif.model.PdbxChemCompIdentifier;
import org.biojava.nbio.structure.io.mmcif.model.PdbxDatabaseStatus;
import org.biojava.nbio.structure.io.mmcif.model.PdbxEntityNonPoly;
import org.biojava.nbio.structure.io.mmcif.model.PdbxNonPolyScheme;
import org.biojava.nbio.structure.io.mmcif.model.PdbxPolySeqScheme;
import org.biojava.nbio.structure.io.mmcif.model.PdbxStructAssembly;
import org.biojava.nbio.structure.io.mmcif.model.PdbxStructAssemblyGen;
import org.biojava.nbio.structure.io.mmcif.model.PdbxStructOperList;
import org.biojava.nbio.structure.io.mmcif.model.Refine;
import org.biojava.nbio.structure.io.mmcif.model.Struct;
import org.biojava.nbio.structure.io.mmcif.model.StructAsym;
import org.biojava.nbio.structure.io.mmcif.model.StructConn;
import org.biojava.nbio.structure.io.mmcif.model.StructKeywords;
import org.biojava.nbio.structure.io.mmcif.model.StructNcsOper;
import org.biojava.nbio.structure.io.mmcif.model.StructRef;
import org.biojava.nbio.structure.io.mmcif.model.StructRefSeq;
import org.biojava.nbio.structure.io.mmcif.model.StructRefSeqDif;
import org.biojava.nbio.structure.io.mmcif.model.StructSite;
import org.biojava.nbio.structure.io.mmcif.model.StructSiteGen;
import org.biojava.nbio.structure.io.mmcif.model.Symmetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.poznan.put.notation.BPh;
import pl.poznan.put.notation.BR;
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
import pl.poznan.put.rna.InteractionType;
import pl.poznan.put.structure.secondary.BasePair;
import pl.poznan.put.structure.secondary.HelixOrigin;
import pl.poznan.put.structure.secondary.ModifiableQuantifiedBasePair;
import pl.poznan.put.structure.secondary.QuantifiedBasePair;

import javax.annotation.Nullable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class CifConsumer implements MMcifConsumer {
  private static final Logger LOGGER = LoggerFactory.getLogger(CifConsumer.class);

  private static final String PDBX_STRUCT_MOD_RESIDUE = "_pdbx_struct_mod_residue"; // NON-NLS
  private static final String PDBX_UNOBS_OR_ZERO_OCC_RESIDUES =
      "_pdbx_unobs_or_zero_occ_residues"; // NON-NLS
  private static final String NDB_STRUCT_NA_BASE_PAIR = "_ndb_struct_na_base_pair"; // NON-NLS
  private static final String SHEAR = "shear"; // NON-NLS
  private static final String STRETCH = "stretch"; // NON-NLS
  private static final String STAGGER = "stagger"; // NON-NLS
  private static final String BUCKLE = "buckle"; // NON-NLS
  private static final String PROPELLER = "propeller"; // NON-NLS
  private static final String OPENING = "opening"; // NON-NLS

  private final Map<Integer, List<PdbAtomLine>> modelAtoms = new TreeMap<>();
  private final Collection<PdbRemark465Line> missingResidues = new ArrayList<>();
  private final Collection<PdbModresLine> modifiedResidues = new ArrayList<>();
  private final List<ExperimentalTechnique> experimentalTechniques = new ArrayList<>();
  private final Collection<QuantifiedBasePair> basePairs = new ArrayList<>();
  private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

  @Nullable private Date depositionDate;
  @Nullable private String classification;
  @Nullable private String idCode;
  @Nullable private String title;
  private double resolution = Double.NaN;

  private FileParsingParameters parameters = new FileParsingParameters();

  private CifConsumer(final FileParsingParameters parameters) {
    super();
    this.parameters = parameters;
  }

  CifConsumer() {
    super();
  }

  private static Map<String, String> convertToMap(
      final List<String> loopFields, final List<String> lineData) {
    return IntStream.range(0, loopFields.size())
        .boxed()
        .collect(Collectors.toMap(loopFields::get, lineData::get, (a, b) -> b));
  }

  private static double getDoubleWithDefaultNaN(final Map<String, String> map, final String key) {
    return map.containsKey(key) ? Double.parseDouble(map.get(key)) : Double.NaN;
  }

  @Override
  public final void documentStart() {
    modelAtoms.clear();
    missingResidues.clear();
    modifiedResidues.clear();
    experimentalTechniques.clear();
    basePairs.clear();

    depositionDate = null;
    classification = null;
    idCode = null;
    resolution = Double.NaN;
  }

  @Override
  public void documentEnd() {
    // do nothing
  }

  @Override
  public final void newAtomSite(final AtomSite atomSite) {
    try {
      final int serialNumber = Integer.parseInt(atomSite.getId());
      final String atomName =
          (atomSite.getAuth_atom_id() != null)
              ? atomSite.getAuth_atom_id()
              : atomSite.getLabel_atom_id();
      String alternateLocation = atomSite.getLabel_alt_id();
      final String residueName =
          (atomSite.getAuth_comp_id() != null)
              ? atomSite.getAuth_comp_id()
              : atomSite.getLabel_comp_id();
      final String chainIdentifier =
          (atomSite.getAuth_asym_id() != null)
              ? atomSite.getAuth_asym_id()
              : atomSite.getLabel_asym_id();
      final int residueNumber =
          Integer.parseInt(
              (atomSite.getAuth_seq_id() != null)
                  ? atomSite.getAuth_seq_id()
                  : atomSite.getLabel_seq_id());
      String insertionCode = atomSite.getPdbx_PDB_ins_code();
      final double x = Double.parseDouble(atomSite.getCartn_x());
      final double y = Double.parseDouble(atomSite.getCartn_y());
      final double z = Double.parseDouble(atomSite.getCartn_z());
      final double occupancy = Double.parseDouble(atomSite.getOccupancy());
      final double temperatureFactor = Double.parseDouble(atomSite.getB_iso_or_equiv());
      final String elementSymbol = atomSite.getType_symbol();
      String charge = atomSite.getPdbx_formal_charge();

      if (Objects.equals("?", insertionCode)) {
        insertionCode = " ";
      }
      if (Objects.equals(".", alternateLocation)) {
        alternateLocation = " ";
      }
      if (Objects.equals("?", charge)) {
        charge = " ";
      }

      final PdbAtomLine atomLine =
          ImmutablePdbAtomLine.of(
              serialNumber,
              atomName,
              alternateLocation,
              residueName,
              chainIdentifier,
              residueNumber,
              insertionCode,
              x,
              y,
              z,
              occupancy,
              temperatureFactor,
              elementSymbol,
              charge);

      final String modelNumberString = atomSite.getPdbx_PDB_model_num();
      int modelNumber = 1;
      if (StringUtils.isNotBlank(modelNumberString)) {
        modelNumber = Integer.parseInt(modelNumberString);
      }

      if (!modelAtoms.containsKey(modelNumber)) {
        modelAtoms.put(modelNumber, new ArrayList<>());
      }

      final List<PdbAtomLine> atomLines = modelAtoms.get(modelNumber);
      atomLines.add(atomLine);
    } catch (final NumberFormatException e) {
      CifConsumer.LOGGER.warn("Failed to parse _atom_site", e);
    }
  }

  @Override
  public void newEntity(final Entity entity) {
    // do nothing
  }

  @Override
  public void newEntityPoly(final EntityPoly entityPoly) {
    // do nothing
  }

  @Override
  public void newEntityPolySeq(final EntityPolySeq entityPolySeq) {
    // do nothing
  }

  @Override
  public void newStructAsym(final StructAsym structAsym) {
    // do nothing
  }

  @Override
  public final void setStruct(final Struct struct) {
    title = StringUtils.upperCase(struct.getTitle());
  }

  @Override
  public final void newDatabasePDBrev(final DatabasePDBrev databasePDBrev) {
    try {
      if (depositionDate == null) {
        depositionDate = dateFormat.parse(databasePDBrev.getDate_original());
      }
    } catch (final ParseException e) {
      CifConsumer.LOGGER.warn(
          "Failed to parse _database_PDB_rev.date_original as yyyy-MM-dd: {}",
          databasePDBrev.getDate_original(),
          e);
    }
  }

  @Override
  public void newDatabasePDBrevRecord(final DatabasePdbrevRecord databasePdbrevRecord) {
    // do nothing
  }

  @Override
  public void newDatabasePDBremark(final DatabasePDBremark databasePDBremark) {
    // do nothing
  }

  @Override
  public final void newExptl(final Exptl exptl) {
    final ExperimentalTechnique technique = ExperimentalTechnique.fromFullName(exptl.getMethod());
    if (technique == ExperimentalTechnique.UNKNOWN) {
      CifConsumer.LOGGER.warn("Failed to parse _exptl.method: {}", exptl.getMethod());
    } else {
      experimentalTechniques.add(technique);
    }
  }

  @Override
  public void newCell(final Cell cell) {
    // do nothing
  }

  @Override
  public void newSymmetry(final Symmetry symmetry) {
    // do nothing
  }

  @Override
  public void newStructNcsOper(final StructNcsOper structNcsOper) {
    // do nothing
  }

  @Override
  public void newAtomSites(final AtomSites atomSites) {
    // do nothing
  }

  @Override
  public void newStructRef(final StructRef structRef) {
    // do nothing
  }

  @Override
  public void newStructRefSeq(final StructRefSeq structRefSeq) {
    // do nothing
  }

  @Override
  public void newStructRefSeqDif(final StructRefSeqDif structRefSeqDif) {
    // do nothing
  }

  @Override
  public void newStructSite(final StructSite structSite) {
    // do nothing
  }

  @Override
  public void newStructSiteGen(final StructSiteGen structSiteGen) {
    // do nothing
  }

  @Override
  public void newPdbxAuditRevisionHistory(final PdbxAuditRevisionHistory pdbxAuditRevisionHistory) {
    // do nothing
  }

  @Override
  public void newPdbxDatabaseStatus(final PdbxDatabaseStatus pdbxDatabaseStatus) {
    // do nothing
  }

  @Override
  public void newPdbxPolySeqScheme(final PdbxPolySeqScheme pdbxPolySeqScheme) {
    // do nothing
  }

  @Override
  public void newPdbxNonPolyScheme(final PdbxNonPolyScheme pdbxNonPolyScheme) {
    // do nothing
  }

  @Override
  public void newPdbxEntityNonPoly(final PdbxEntityNonPoly pdbxEntityNonPoly) {
    // do nothing
  }

  @Override
  public final void newStructKeywords(final StructKeywords structKeywords) {
    idCode = structKeywords.getEntry_id();
    classification = structKeywords.getPdbx_keywords();
  }

  @Override
  public final void newRefine(final Refine refine) {
    try {
      resolution = Double.parseDouble(refine.getLs_d_res_high());
    } catch (final NumberFormatException e) {
      CifConsumer.LOGGER.warn(
          "Failed to parse _refine.ls_d_res_high: {}", refine.getLs_d_res_high(), e);
    }
  }

  @Override
  public void newChemComp(final ChemComp chemComp) {
    // do nothing
  }

  @Override
  public void newChemCompDescriptor(final ChemCompDescriptor chemCompDescriptor) {
    // do nothing
  }

  @Override
  public void newPdbxStructOperList(final PdbxStructOperList pdbxStructOperList) {
    // do nothing
  }

  @Override
  public void newPdbxStrucAssembly(final PdbxStructAssembly pdbxStructAssembly) {
    // do nothing
  }

  @Override
  public void newPdbxStrucAssemblyGen(final PdbxStructAssemblyGen pdbxStructAssemblyGen) {
    // do nothing
  }

  @Override
  public void newChemCompAtom(final ChemCompAtom chemCompAtom) {
    // do nothing
  }

  @Override
  public void newPdbxChemCompIndentifier(final PdbxChemCompIdentifier pdbxChemCompIdentifier) {
    // do nothing
  }

  @Override
  public void newChemCompBond(final ChemCompBond chemCompBond) {
    // do nothing
  }

  @Override
  public void newPdbxChemCompDescriptor(final PdbxChemCompDescriptor pdbxChemCompDescriptor) {
    // do nothing
  }

  @Override
  public void newEntitySrcGen(final EntitySrcGen entitySrcGen) {
    // do nothing
  }

  @Override
  public void newEntitySrcNat(final EntitySrcNat entitySrcNat) {
    // do nothing
  }

  @Override
  public void newEntitySrcSyn(final EntitySrcSyn entitySrcSyn) {
    // do nothing
  }

  @Override
  public void newStructConn(final StructConn structConn) {
    // do nothing
  }

  @Override
  public void newAuditAuthor(final AuditAuthor auditAuthor) {
    // do nothing
  }

  @Override
  public final void newGenericData(
      final String s, final List<String> list, final List<String> list1) {
    if (Objects.equals(CifConsumer.PDBX_STRUCT_MOD_RESIDUE, s)) {
      final Map<String, String> map = CifConsumer.convertToMap(list, list1);

      final String residueName = map.get("auth_comp_id");
      final String chainIdentifier = map.get("auth_asym_id");
      final int residueNumber = Integer.parseInt(map.get("auth_seq_id"));
      String insertionCode = map.get("PDB_ins_code");
      final String standardResidueName = map.get("parent_comp_id");
      final String comment = map.get("details");

      if (Objects.equals("?", insertionCode)) {
        insertionCode = " ";
      }

      final PdbModresLine modresLine =
          ImmutablePdbModresLine.of(
              idCode,
              residueName,
              chainIdentifier,
              residueNumber,
              insertionCode,
              standardResidueName,
              comment);
      modifiedResidues.add(modresLine);
    } else if (Objects.equals(CifConsumer.PDBX_UNOBS_OR_ZERO_OCC_RESIDUES, s)) {
      final Map<String, String> map = CifConsumer.convertToMap(list, list1);

      final int modelNumber = Integer.parseInt(map.get("PDB_model_num"));
      final String residueName = map.get("auth_comp_id");
      final String chainIdentifier = map.get("auth_asym_id");
      final int residueNumber = Integer.parseInt(map.get("auth_seq_id"));
      String insertionCode = map.get("PDB_ins_code");

      if (Objects.equals("?", insertionCode)) {
        insertionCode = " ";
      }

      final PdbRemark465Line remark465Line =
          ImmutablePdbRemark465Line.of(
              modelNumber, residueName, chainIdentifier, residueNumber, insertionCode);
      missingResidues.add(remark465Line);
    } else if (Objects.equals(CifConsumer.NDB_STRUCT_NA_BASE_PAIR, s)) {
      final Map<String, String> map = CifConsumer.convertToMap(list, list1);

      final String chainL = map.get("i_auth_asym_id");
      final int resiL = Integer.parseInt(map.get("i_auth_seq_id"));
      String icodeL = map.get("i_PDB_ins_code");
      if (Objects.equals("?", icodeL)) {
        icodeL = " ";
      }
      final String resnL = map.get("i_label_comp_id");
      final char oneLetterL =
          ResidueTypeDetector.detectResidueType(resnL, Collections.emptySet()).oneLetterName();
      final PdbNamedResidueIdentifier left =
          ImmutablePdbNamedResidueIdentifier.of(chainL, resiL, icodeL, oneLetterL);

      final String chainR = map.get("j_auth_asym_id");
      final int resiR = Integer.parseInt(map.get("j_auth_seq_id"));
      String icodeR = map.get("j_PDB_ins_code");
      if (Objects.equals("?", icodeR)) {
        icodeR = " ";
      }
      final String resnR = map.get("j_label_comp_id");
      final char oneLetterR =
          ResidueTypeDetector.detectResidueType(resnR, Collections.emptySet()).oneLetterName();
      final PdbNamedResidueIdentifier right =
          ImmutablePdbNamedResidueIdentifier.of(chainR, resiR, icodeR, oneLetterR);
      final BasePair basePair = new BasePair(left, right);

      final String saengerString = map.get("hbond_type_28");
      Saenger saenger = Saenger.UNKNOWN;
      if (!Objects.equals("?", saengerString)) {
        saenger = Saenger.fromNumber(Integer.parseInt(saengerString));
      }

      final String leontisWesthofString = map.get("hbond_type_12");
      final LeontisWesthof leontisWesthof =
          Objects.equals("?", leontisWesthofString)
              ? LeontisWesthof.UNKNOWN
              : LeontisWesthof.fromNumber(Integer.parseInt(leontisWesthofString));

      final double shear = CifConsumer.getDoubleWithDefaultNaN(map, CifConsumer.SHEAR);
      final double stretch = CifConsumer.getDoubleWithDefaultNaN(map, CifConsumer.STRETCH);
      final double stagger = CifConsumer.getDoubleWithDefaultNaN(map, CifConsumer.STAGGER);
      final double buckle = CifConsumer.getDoubleWithDefaultNaN(map, CifConsumer.BUCKLE);
      final double propeller = CifConsumer.getDoubleWithDefaultNaN(map, CifConsumer.PROPELLER);
      final double opening = CifConsumer.getDoubleWithDefaultNaN(map, CifConsumer.OPENING);
      final QuantifiedBasePair quantifiedBasePair =
          ModifiableQuantifiedBasePair.create(
              basePair,
              InteractionType.BASE_BASE,
              saenger,
              leontisWesthof,
              BPh.UNKNOWN,
              BR.UNKNOWN,
              HelixOrigin.UNKNOWN,
              false,
              shear,
              stretch,
              stagger,
              buckle,
              propeller,
              opening);
      basePairs.add(quantifiedBasePair);
    }
  }

  @Override
  public final void setFileParsingParameters(final FileParsingParameters fileParsingParameters) {
    parameters = fileParsingParameters;
  }

  @Override
  public final FileParsingParameters getFileParsingParameters() {
    return parameters;
  }

  public final List<CifModel> getModels() {
    final PdbHeaderLine headerLine =
        ImmutablePdbHeaderLine.of(
            classification != null ? classification : "",
            depositionDate != null ? depositionDate : new Date(0L),
            idCode != null ? idCode : "");

    final List<ExperimentalTechnique> techniques =
        experimentalTechniques.isEmpty()
            ? Collections.singletonList(ExperimentalTechnique.UNKNOWN)
            : experimentalTechniques;
    final PdbExpdtaLine experimentalDataLine = ImmutablePdbExpdtaLine.of(techniques);

    final PdbRemark2Line resolutionLine = ImmutablePdbRemark2Line.of(resolution);
    final List<CifModel> result = new ArrayList<>();

    for (final Map.Entry<Integer, List<PdbAtomLine>> entry : modelAtoms.entrySet()) {
      final int modelNumber = entry.getKey();
      final List<PdbAtomLine> atoms = entry.getValue();
      final CifModel pdbModel =
          ImmutableDefaultCifModel.of(
              headerLine,
              experimentalDataLine,
              resolutionLine,
              modelNumber,
              atoms,
              modifiedResidues,
              missingResidues,
              title != null ? title : "",
              Collections.emptyList(),
              basePairs);
      result.add(pdbModel);
    }

    return result;
  }
}
