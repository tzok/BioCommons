package pl.poznan.put.pdb.analysis;

import org.apache.commons.lang3.StringUtils;
import org.biojava.nbio.structure.io.FileParsingParameters;
import org.biojava.nbio.structure.io.mmcif.MMcifConsumer;
import org.biojava.nbio.structure.io.mmcif.model.AtomSite;
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
import org.biojava.nbio.structure.io.mmcif.model.PdbxChemCompDescriptor;
import org.biojava.nbio.structure.io.mmcif.model.PdbxChemCompIdentifier;
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
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbExpdtaLine;
import pl.poznan.put.pdb.PdbHeaderLine;
import pl.poznan.put.pdb.PdbModresLine;
import pl.poznan.put.pdb.PdbParsingException;
import pl.poznan.put.pdb.PdbRemark2Line;
import pl.poznan.put.pdb.PdbRemark465Line;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.structure.secondary.BasePair;
import pl.poznan.put.structure.secondary.QuantifiedBasePair;

import javax.annotation.Nullable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class CifConsumer implements MMcifConsumer {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(CifConsumer.class);

    private static final DateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static final String PDBX_STRUCT_MOD_RESIDUE =
            "_pdbx_struct_mod_residue"; //NON-NLS
    private static final String PDBX_UNOBS_OR_ZERO_OCC_RESIDUES =
            "_pdbx_unobs_or_zero_occ_residues"; //NON-NLS
    private static final String NDB_STRUCT_NA_BASE_PAIR =
            "_ndb_struct_na_base_pair"; //NON-NLS
    private static final String SHEAR = "shear"; //NON-NLS
    private static final String STRETCH = "stretch"; //NON-NLS
    private static final String STAGGER = "stagger"; //NON-NLS
    private static final String BUCKLE = "buckle"; //NON-NLS
    private static final String PROPELLER = "propeller"; //NON-NLS
    private static final String OPENING = "opening"; //NON-NLS

    private final Map<Integer, List<PdbAtomLine>> modelAtoms = new TreeMap<>();
    private final List<PdbRemark465Line> missingResidues = new ArrayList<>();
    private final List<PdbModresLine> modifiedResidues = new ArrayList<>();
    private final List<ExperimentalTechnique> experimentalTechniques =
            new ArrayList<>();
    private final List<QuantifiedBasePair> basePairs = new ArrayList<>();

    @Nullable
    private Date depositionDate;
    @Nullable
    private String classification;
    @Nullable
    private String idCode;
    private double resolution;

    private FileParsingParameters parameters;

    public CifConsumer(final FileParsingParameters parameters) {
        super();
        this.parameters = parameters;
    }

    public CifConsumer() {
        super();
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
    public final void newAtomSite(final AtomSite atom) {
        try {
            int serialNumber = Integer.parseInt(atom.getId());
            String atomName = atom.getAuth_atom_id();
            String alternateLocation = atom.getLabel_alt_id();
            String residueName = atom.getAuth_comp_id();
            String chainIdentifier = atom.getAuth_asym_id();
            int residueNumber = Integer.parseInt(atom.getAuth_seq_id());
            String insertionCode = atom.getPdbx_PDB_ins_code();
            double x = Double.parseDouble(atom.getCartn_x());
            double y = Double.parseDouble(atom.getCartn_y());
            double z = Double.parseDouble(atom.getCartn_z());
            double occupancy = Double.parseDouble(atom.getOccupancy());
            double temperatureFactor =
                    Double.parseDouble(atom.getB_iso_or_equiv());
            String elementSymbol = atom.getType_symbol();
            String charge = atom.getPdbx_formal_charge();

            if (Objects.equals("?", insertionCode)) {
                insertionCode = " ";
            }
            if (Objects.equals(".", alternateLocation)) {
                alternateLocation = " ";
            }
            if (Objects.equals("?", charge)) {
                charge = " ";
            }

            PdbAtomLine atomLine =
                    new PdbAtomLine(serialNumber, atomName, alternateLocation,
                                    residueName, chainIdentifier, residueNumber,
                                    insertionCode, x, y, z, occupancy,
                                    temperatureFactor, elementSymbol, charge);

            String modelNumberString = atom.getPdbx_PDB_model_num();
            int modelNumber = 1;
            if (StringUtils.isNotBlank(modelNumberString)) {
                modelNumber = Integer.parseInt(modelNumberString);
            }

            if (!modelAtoms.containsKey(modelNumber)) {
                modelAtoms.put(modelNumber, new ArrayList<>());
            }

            List<PdbAtomLine> atomLines = modelAtoms.get(modelNumber);
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
    public void newEntityPolySeq(final EntityPolySeq epolseq) {
        // do nothing
    }

    @Override
    public void newStructAsym(final StructAsym sasym) {
        // do nothing
    }

    @Override
    public void setStruct(final Struct struct) {
        // do nothing
    }

    @Override
    public final void newDatabasePDBrev(final DatabasePDBrev dbrev) {
        try {
            if (depositionDate == null) {
                depositionDate =
                        CifConsumer.DATE_FORMAT.parse(dbrev.getDate_original());
            }
        } catch (final ParseException e) {
            CifConsumer.LOGGER
                    .warn("Failed to parse _database_PDB_rev.date_original as"
                          + " yyyy-MM-dd: {}", dbrev.getDate_original(), e);
        }
    }

    @Override
    public void newDatabasePDBrevRecord(
            final DatabasePdbrevRecord dbrev) {
        // do nothing
    }

    @Override
    public void newDatabasePDBremark(
            final DatabasePDBremark remark) {
        // do nothing
    }

    @Override
    public final void newExptl(final Exptl exptl) {
        ExperimentalTechnique technique =
                ExperimentalTechnique.fromFullName(exptl.getMethod());
        if (technique == ExperimentalTechnique.UNKNOWN) {
            CifConsumer.LOGGER.warn("Failed to parse _exptl.method: {}",
                                    exptl.getMethod());
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
    public void newStructNcsOper(final StructNcsOper sNcsOper) {
        // do nothing
    }

    @Override
    public void newStructRef(final StructRef sref) {
        // do nothing
    }

    @Override
    public void newStructRefSeq(final StructRefSeq sref) {
        // do nothing
    }

    @Override
    public void newStructRefSeqDif(final StructRefSeqDif sref) {
        // do nothing
    }

    @Override
    public void newStructSite(final StructSite sref) {
        // do nothing
    }

    @Override
    public void newStructSiteGen(final StructSiteGen sref) {
        // do nothing
    }

    @Override
    public void newPdbxPolySeqScheme(
            final PdbxPolySeqScheme ppss) {
        // do nothing
    }

    @Override
    public void newPdbxNonPolyScheme(
            final PdbxNonPolyScheme ppss) {
        // do nothing
    }

    @Override
    public void newPdbxEntityNonPoly(
            final PdbxEntityNonPoly pen) {
        // do nothing
    }

    @Override
    public final void newStructKeywords(final StructKeywords kw) {
        idCode = kw.getEntry_id();
        classification = kw.getPdbx_keywords();
    }

    @Override
    public final void newRefine(final Refine r) {
        try {
            resolution = Double.parseDouble(r.getLs_d_res_high());
        } catch (final NumberFormatException e) {
            CifConsumer.LOGGER.warn("Failed to parse _refine.ls_d_res_high: {}",
                                    r.getLs_d_res_high(), e);
        }
    }

    @Override
    public void newChemComp(final ChemComp c) {
        // do nothing
    }

    @Override
    public void newChemCompDescriptor(
            final ChemCompDescriptor ccd) {
        // do nothing
    }

    @Override
    public void newPdbxStructOperList(
            final PdbxStructOperList structOper) {
        // do nothing
    }

    @Override
    public void newPdbxStrucAssembly(
            final PdbxStructAssembly strucAssembly) {
        // do nothing
    }

    @Override
    public void newPdbxStrucAssemblyGen(
            final PdbxStructAssemblyGen strucAssembly) {
        // do nothing
    }

    @Override
    public void newChemCompAtom(final ChemCompAtom atom) {
        // do nothing
    }

    @Override
    public void newPdbxChemCompIndentifier(
            final PdbxChemCompIdentifier id) {
        // do nothing
    }

    @Override
    public void newChemCompBond(final ChemCompBond bond) {
        // do nothing
    }

    @Override
    public void newPdbxChemCompDescriptor(
            final PdbxChemCompDescriptor desc) {
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
    public void newAuditAuthor(final AuditAuthor aa) {
        // do nothing
    }

    @Override
    public final void newGenericData(
            final String category, final List<String> loopFields,
            final List<String> lineData) {
        if (Objects.equals(CifConsumer.PDBX_STRUCT_MOD_RESIDUE, category)) {
            Map<String, String> map =
                    CifConsumer.convertToMap(loopFields, lineData);

            String residueName = map.get("auth_comp_id");
            String chainIdentifier = map.get("auth_asym_id");
            int residueNumber = Integer.parseInt(map.get("auth_seq_id"));
            String insertionCode = map.get("PDB_ins_code");
            String standardResidueName = map.get("parent_comp_id");
            String comment = map.get("details");

            if (Objects.equals("?", insertionCode)) {
                insertionCode = " ";
            }

            PdbModresLine modresLine =
                    new PdbModresLine(idCode, residueName, chainIdentifier,
                                      residueNumber, insertionCode,
                                      standardResidueName, comment);
            modifiedResidues.add(modresLine);
        } else if (Objects.equals(CifConsumer.PDBX_UNOBS_OR_ZERO_OCC_RESIDUES,
                                  category)) {
            Map<String, String> map =
                    CifConsumer.convertToMap(loopFields, lineData);

            int modelNumber = Integer.parseInt(map.get("PDB_model_num"));
            String residueName = map.get("auth_comp_id");
            String chainIdentifier = map.get("auth_asym_id");
            int residueNumber = Integer.parseInt(map.get("auth_seq_id"));
            String insertionCode = map.get("PDB_ins_code");

            if (Objects.equals("?", insertionCode)) {
                insertionCode = " ";
            }

            PdbRemark465Line remark465Line =
                    new PdbRemark465Line(modelNumber, residueName,
                                         chainIdentifier, residueNumber,
                                         insertionCode);
            missingResidues.add(remark465Line);
        } else if (Objects
                .equals(CifConsumer.NDB_STRUCT_NA_BASE_PAIR, category)) {
            Map<String, String> map =
                    CifConsumer.convertToMap(loopFields, lineData);

            String chainL = map.get("i_auth_asym_id");
            int resiL = Integer.parseInt(map.get("i_auth_seq_id"));
            String icodeL = map.get("i_PDB_ins_code");
            if (Objects.equals("?", icodeL)) {
                icodeL = " ";
            }
            PdbResidueIdentifier left =
                    new PdbResidueIdentifier(chainL, resiL, icodeL);

            String chainR = map.get("j_auth_asym_id");
            int resiR = Integer.parseInt(map.get("j_auth_seq_id"));
            String icodeR = map.get("j_PDB_ins_code");
            if (Objects.equals("?", icodeR)) {
                icodeR = " ";
            }
            PdbResidueIdentifier right =
                    new PdbResidueIdentifier(chainR, resiR, icodeR);
            BasePair basePair = new BasePair(left, right);

            String saengerString = map.get("hbond_type_28");
            Saenger saenger = Saenger.UNKNOWN;
            if (!Objects.equals("?", saengerString)) {
                saenger = Saenger.fromOrdinal(Integer.parseInt(saengerString));
            }

            String leontisWesthofString = map.get("hbond_type_12");
            LeontisWesthof leontisWesthof =
                    Objects.equals("?", leontisWesthofString)
                    ? LeontisWesthof.UNKNOWN : LeontisWesthof.fromOrdinal(
                            Integer.parseInt(leontisWesthofString));

            double shear =
                    CifConsumer.getDoubleWithDefaultNaN(map, CifConsumer.SHEAR);
            double stretch = CifConsumer
                    .getDoubleWithDefaultNaN(map, CifConsumer.STRETCH);
            double stagger = CifConsumer
                    .getDoubleWithDefaultNaN(map, CifConsumer.STAGGER);
            double buckle = CifConsumer
                    .getDoubleWithDefaultNaN(map, CifConsumer.BUCKLE);
            double propeller = CifConsumer
                    .getDoubleWithDefaultNaN(map, CifConsumer.PROPELLER);
            double opening = CifConsumer
                    .getDoubleWithDefaultNaN(map, CifConsumer.OPENING);
            QuantifiedBasePair quantifiedBasePair =
                    new QuantifiedBasePair(basePair, saenger, leontisWesthof,
                                           BPh.UNKNOWN, BR.UNKNOWN, shear,
                                           stretch, stagger, buckle, propeller,
                                           opening);
            basePairs.add(quantifiedBasePair);
        }
    }

    private static Map<String, String> convertToMap(
            final List<String> loopFields, final List<String> lineData) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < loopFields.size(); i++) {
            map.put(loopFields.get(i), lineData.get(i));
        }
        return map;
    }

    private static double getDoubleWithDefaultNaN(
            final Map<String, String> map, final String key) {
        return map.containsKey(key) ? Double.parseDouble(map.get(key))
                                    : Double.NaN;
    }

    public final List<CifModel> getModels() throws PdbParsingException {
        Date date = (depositionDate == null) ? new Date(0) : depositionDate;
        PdbHeaderLine headerLine =
                new PdbHeaderLine(classification, date, idCode);

        List<ExperimentalTechnique> techniques =
                experimentalTechniques.isEmpty() ? Collections
                        .singletonList(ExperimentalTechnique.UNKNOWN)
                                                 : experimentalTechniques;
        PdbExpdtaLine experimentalDataLine = new PdbExpdtaLine(techniques);

        PdbRemark2Line resolutionLine = new PdbRemark2Line(resolution);
        List<CifModel> result = new ArrayList<>();

        for (final Map.Entry<Integer, List<PdbAtomLine>> entry : modelAtoms
                .entrySet()) {
            int modelNumber = entry.getKey();
            List<PdbAtomLine> atoms = entry.getValue();
            CifModel pdbModel = new CifModel(headerLine, experimentalDataLine,
                                             resolutionLine, modelNumber, atoms,
                                             modifiedResidues, missingResidues,
                                             basePairs);
            result.add(pdbModel);
        }

        return result;
    }

    @Override
    public final void setFileParsingParameters(
            final FileParsingParameters params) {
        parameters = params;
    }

    @Override
    public final FileParsingParameters getFileParsingParameters() {
        return parameters;
    }


}
