package pl.poznan.put.pdb.analysis;

import org.biojava.nbio.structure.io.FileParsingParameters;
import org.biojava.nbio.structure.io.mmcif.MMcifConsumer;
import org.biojava.nbio.structure.io.mmcif.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.poznan.put.notation.LeontisWesthof;
import pl.poznan.put.notation.Saenger;
import pl.poznan.put.pdb.*;
import pl.poznan.put.pdb.PdbExpdtaLine.ExperimentalTechnique;
import pl.poznan.put.structure.secondary.BasePair;
import pl.poznan.put.structure.secondary.QuantifiedBasePair;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MmCifConsumer implements MMcifConsumer {
    private final static Logger LOGGER = LoggerFactory.getLogger(MMcifConsumer.class);
    private final static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private final Map<Integer, List<PdbAtomLine>> modelAtoms = new TreeMap<>();
    private final List<PdbRemark465Line> missingResidues = new ArrayList<>();
    private final List<PdbModresLine> modifiedResidues = new ArrayList<>();
    private final List<ExperimentalTechnique> experimentalTechniques = new ArrayList<>();
    private final List<QuantifiedBasePair> basePairs = new ArrayList<>();

    private Date depositionDate;
    private String classification;
    private String idCode;
    private double resolution;

    private FileParsingParameters parameters;

    public MmCifConsumer(FileParsingParameters parameters) {
        this.parameters = parameters;
    }

    public MmCifConsumer() {
    }

    @Override
    public void documentStart() {
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
    public void newAtomSite(AtomSite atomSite) {
        try {
            int serialNumber = Integer.parseInt(atomSite.getId());
            String atomName = atomSite.getAuth_atom_id();
            String alternateLocation = atomSite.getLabel_alt_id();
            String residueName = atomSite.getAuth_comp_id();
            String chainIdentifier = atomSite.getAuth_asym_id();
            int residueNumber = Integer.parseInt(atomSite.getAuth_seq_id());
            String insertionCode = atomSite.getPdbx_PDB_ins_code();
            double x = Double.parseDouble(atomSite.getCartn_x());
            double y = Double.parseDouble(atomSite.getCartn_y());
            double z = Double.parseDouble(atomSite.getCartn_z());
            double occupancy = Double.parseDouble(atomSite.getOccupancy());
            double temperatureFactor = Double.parseDouble(atomSite.getB_iso_or_equiv());
            String elementSymbol = atomSite.getType_symbol();
            String charge = atomSite.getPdbx_formal_charge();

            if ("?".equals(insertionCode)) {
                insertionCode = " ";
            }
            if (".".equals(alternateLocation)) {
                alternateLocation = " ";
            }
            if ("?".equals(charge)) {
                charge = " ";
            }

            PdbAtomLine atomLine = new PdbAtomLine(serialNumber, atomName, alternateLocation, residueName, chainIdentifier, residueNumber, insertionCode, x, y, z, occupancy, temperatureFactor, elementSymbol, charge);
            int modelNumber = Integer.parseInt(atomSite.getPdbx_PDB_model_num());

            if (!modelAtoms.containsKey(modelNumber)) {
                modelAtoms.put(modelNumber, new ArrayList<PdbAtomLine>());
            }

            List<PdbAtomLine> atomLines = modelAtoms.get(modelNumber);
            atomLines.add(atomLine);
        } catch (NumberFormatException e) {
            MmCifConsumer.LOGGER.warn("Failed to parse _atom_site", e);
        }
    }

    @Override
    public void newEntity(Entity entity) {
        // do nothing
    }

    @Override
    public void newEntityPolySeq(EntityPolySeq entityPolySeq) {
        // do nothing
    }

    @Override
    public void newStructAsym(StructAsym structAsym) {
        // do nothing
    }

    @Override
    public void setStruct(Struct struct) {
        // do nothing
    }

    @Override
    public void newDatabasePDBrev(DatabasePDBrev databasePDBrev) {
        try {
            if (depositionDate == null) {
                depositionDate = MmCifConsumer.DATE_FORMAT.parse(databasePDBrev.getDate_original());
            }
        } catch (ParseException e) {
            MmCifConsumer.LOGGER.warn("Failed to parse _database_PDB_rev.date_original as yyyy-MM-dd: " + databasePDBrev.getDate_original(), e);
        }
    }

    @Override
    public void newDatabasePDBrevRecord(DatabasePdbrevRecord databasePdbrevRecord) {
        // do nothing
    }

    @Override
    public void newDatabasePDBremark(DatabasePDBremark databasePDBremark) {
        // do nothing
    }

    @Override
    public void newExptl(Exptl exptl) {
        ExperimentalTechnique technique = ExperimentalTechnique.fromFullName(exptl.getMethod());
        if (technique == ExperimentalTechnique.UNKNOWN) {
            LOGGER.warn("Failed to parse _exptl.method: " + exptl.getMethod());
        } else {
            experimentalTechniques.add(technique);
        }
    }

    @Override
    public void newCell(Cell cell) {
        // do nothing
    }

    @Override
    public void newSymmetry(Symmetry symmetry) {
        // do nothing
    }

    @Override
    public void newStructNcsOper(StructNcsOper structNcsOper) {
        // do nothing
    }

    @Override
    public void newStructRef(StructRef structRef) {
        // do nothing
    }

    @Override
    public void newStructRefSeq(StructRefSeq structRefSeq) {
        // do nothing
    }

    @Override
    public void newStructRefSeqDif(StructRefSeqDif structRefSeqDif) {
        // do nothing
    }

    @Override
    public void newStructSite(StructSite structSite) {
        // do nothing
    }

    @Override
    public void newStructSiteGen(StructSiteGen structSiteGen) {
        // do nothing
    }

    @Override
    public void newPdbxPolySeqScheme(PdbxPolySeqScheme pdbxPolySeqScheme) {
        // do nothing
    }

    @Override
    public void newPdbxNonPolyScheme(PdbxNonPolyScheme pdbxNonPolyScheme) {
        // do nothing
    }

    @Override
    public void newPdbxEntityNonPoly(PdbxEntityNonPoly pdbxEntityNonPoly) {
        // do nothing
    }

    @Override
    public void newStructKeywords(StructKeywords structKeywords) {
        idCode = structKeywords.getEntry_id();
        classification = structKeywords.getPdbx_keywords();
    }

    @Override
    public void newRefine(Refine refine) {
        try {
            resolution = Double.parseDouble(refine.getLs_d_res_high());
        } catch (NumberFormatException e) {
            LOGGER.warn("Failed to parse _refine.ls_d_res_high: " + refine.getLs_d_res_high());
        }
    }

    @Override
    public void newChemComp(ChemComp chemComp) {
        // do nothing
    }

    @Override
    public void newChemCompDescriptor(ChemCompDescriptor chemCompDescriptor) {
        // do nothing
    }

    @Override
    public void newPdbxStructOperList(PdbxStructOperList pdbxStructOperList) {
        // do nothing
    }

    @Override
    public void newPdbxStrucAssembly(PdbxStructAssembly pdbxStructAssembly) {
        // do nothing
    }

    @Override
    public void newPdbxStrucAssemblyGen(PdbxStructAssemblyGen pdbxStructAssemblyGen) {
        // do nothing
    }

    @Override
    public void newChemCompAtom(ChemCompAtom chemCompAtom) {
        // do nothing
    }

    @Override
    public void newPdbxChemCompIndentifier(PdbxChemCompIdentifier pdbxChemCompIdentifier) {
        // do nothing
    }

    @Override
    public void newChemCompBond(ChemCompBond chemCompBond) {
        // do nothing
    }

    @Override
    public void newPdbxChemCompDescriptor(PdbxChemCompDescriptor pdbxChemCompDescriptor) {
        // do nothing
    }

    @Override
    public void newEntitySrcGen(EntitySrcGen entitySrcGen) {
        // do nothing
    }

    @Override
    public void newEntitySrcNat(EntitySrcNat entitySrcNat) {
        // do nothing
    }

    @Override
    public void newEntitySrcSyn(EntitySrcSyn entitySrcSyn) {
        // do nothing
    }

    @Override
    public void newStructConn(StructConn structConn) {
        // do nothing
    }

    @Override
    public void newAuditAuthor(AuditAuthor auditAuthor) {
        // do nothing
    }

    @Override
    public void newGenericData(String category, List<String> loopFields, List<String> lineData) {
        if ("_pdbx_struct_mod_residue".equals(category)) {
            Map<String, String> map = convertToMap(loopFields, lineData);

            String residueName = map.get("auth_comp_id");
            String chainIdentifier = map.get("auth_asym_id");
            int residueNumber = Integer.parseInt(map.get("auth_seq_id"));
            String insertionCode = map.get("PDB_ins_code");
            String standardResidueName = map.get("parent_comp_id");
            String comment = map.get("details");

            if ("?".equals(insertionCode)) {
                insertionCode = " ";
            }

            PdbModresLine modresLine = new PdbModresLine(idCode, residueName, chainIdentifier, residueNumber, insertionCode, standardResidueName, comment);
            modifiedResidues.add(modresLine);
        } else if ("_pdbx_unobs_or_zero_occ_residues".equals(category)) {
            Map<String, String> map = convertToMap(loopFields, lineData);

            int modelNumber = Integer.parseInt(map.get("PDB_model_num"));
            String residueName = map.get("auth_comp_id");
            String chainIdentifier = map.get("auth_asym_id");
            int residueNumber = Integer.parseInt(map.get("auth_seq_id"));
            String insertionCode = map.get("PDB_ins_code");

            if ("?".equals(insertionCode)) {
                insertionCode = " ";
            }

            PdbRemark465Line remark465Line = new PdbRemark465Line(modelNumber, residueName, chainIdentifier, residueNumber, insertionCode);
            missingResidues.add(remark465Line);
        } else if ("_ndb_struct_na_base_pair".equals(category)) {
            Map<String, String> map = convertToMap(loopFields, lineData);

            String chainL = map.get("i_auth_asym_id");
            int resiL = Integer.parseInt(map.get("i_auth_seq_id"));
            String icodeL = map.get("i_PDB_ins_code");
            if ("?".equals(icodeL)) {
                icodeL = " ";
            }
            PdbResidueIdentifier left = new PdbResidueIdentifier(chainL, resiL, icodeL);

            String chainR = map.get("j_auth_asym_id");
            int resiR = Integer.parseInt(map.get("j_auth_seq_id"));
            String icodeR = map.get("j_PDB_ins_code");
            if ("?".equals(icodeR)) {
                icodeR = " ";
            }
            PdbResidueIdentifier right = new PdbResidueIdentifier(chainR, resiR, icodeR);
            BasePair basePair = new BasePair(left, right);

            String saengerString = map.get("hbond_type_28");
            Saenger saenger = "?".equals(saengerString) ? Saenger.UNKNOWN : Saenger.fromOrdinal(Integer.parseInt(saengerString));
            String leontisWesthofString = map.get("hbond_type_12");
            LeontisWesthof leontisWesthof = "?".equals(leontisWesthofString) ? LeontisWesthof.UNKNOWN : LeontisWesthof.fromOrdinal(Integer.parseInt(leontisWesthofString));

            double shear = MmCifConsumer.getDoubleWithDefaultNaN(map, "shear");
            double stretch = MmCifConsumer.getDoubleWithDefaultNaN(map, "stretch");
            double stagger = MmCifConsumer.getDoubleWithDefaultNaN(map, "stagger");
            double buckle = MmCifConsumer.getDoubleWithDefaultNaN(map, "buckle");
            double propeller = MmCifConsumer.getDoubleWithDefaultNaN(map, "propeller");
            double opening = MmCifConsumer.getDoubleWithDefaultNaN(map, "opening");
            QuantifiedBasePair quantifiedBasePair = new QuantifiedBasePair(basePair, saenger, leontisWesthof, shear, stretch, stagger, buckle, propeller, opening);
            basePairs.add(quantifiedBasePair);
        }
    }

    private static double getDoubleWithDefaultNaN(Map<String, String> map, String key) {
        return map.containsKey(key) ? Double.parseDouble(map.get(key)) : Double.NaN;
    }

    private Map<String, String> convertToMap(List<String> loopFields, List<String> lineData) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < loopFields.size(); i++) {
            map.put(loopFields.get(i), lineData.get(i));
        }
        return map;
    }

    @Override
    public void setFileParsingParameters(FileParsingParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public FileParsingParameters getFileParsingParameters() {
        return parameters;
    }

    public List<CifModel> getModels() throws PdbParsingException {
        PdbHeaderLine headerLine = new PdbHeaderLine(classification, depositionDate == null ? new Date(0) : depositionDate, idCode);
        PdbExpdtaLine experimentalDataLine = new PdbExpdtaLine(experimentalTechniques.isEmpty() ? Collections.singletonList(ExperimentalTechnique.UNKNOWN) : experimentalTechniques);
        PdbRemark2Line resolutionLine = new PdbRemark2Line(resolution);
        List<CifModel> result = new ArrayList<>();

        for (Map.Entry<Integer, List<PdbAtomLine>> entry : modelAtoms.entrySet()) {
            int modelNumber = entry.getKey();
            List<PdbAtomLine> atoms = entry.getValue();
            CifModel pdbModel = new CifModel(headerLine, experimentalDataLine, resolutionLine, modelNumber, atoms, modifiedResidues, missingResidues, basePairs);
            result.add(pdbModel);
        }

        return result;
    }
}
