package pl.poznan.put.pdb.analysis;

import org.biojava.nbio.structure.io.FileParsingParameters;
import org.biojava.nbio.structure.io.mmcif.MMcifConsumer;
import org.biojava.nbio.structure.io.mmcif.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.poznan.put.pdb.*;

import java.util.*;

/**
 * Created by tzok on 24.05.16.
 */
public class MmCifConsumer implements MMcifConsumer {
    private final static Logger LOGGER = LoggerFactory.getLogger(MMcifConsumer.class);

    private final Map<Integer, List<PdbAtomLine>> modelAtoms = new TreeMap<>();
    private final List<PdbRemark465Line> missingResidues = new ArrayList<>();
    private final List<PdbModresLine> modifiedResidues = new ArrayList<>();

    private String classification;
    private String depositionDate;
    private String idCode;

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

        classification = null;
        depositionDate = null;
        idCode = null;
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

            PdbAtomLine atomLine = new PdbAtomLine(serialNumber, atomName, alternateLocation, residueName, chainIdentifier, residueNumber, insertionCode, x, y, z, occupancy, temperatureFactor, elementSymbol, charge);
            int modelNumber = Integer.parseInt(atomSite.getPdbx_PDB_model_num());

            if (!modelAtoms.containsKey(modelNumber)) {
                modelAtoms.put(modelNumber, new ArrayList<PdbAtomLine>());
            }

            List<PdbAtomLine> atomLines = modelAtoms.get(modelNumber);
            atomLines.add(atomLine);
        } catch (NumberFormatException e) {
            MmCifConsumer.LOGGER.warn("Failed to parse AtomSite line", e);
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
        // FIXME
        if (depositionDate == null) {
            depositionDate = databasePDBrev.getDate_original();
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
        // do nothing
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
        // do nothing
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
        }
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

    public List<PdbModel> getModels() throws PdbParsingException {
        PdbHeaderLine headerLine = new PdbHeaderLine(classification, depositionDate, idCode);
        List<PdbModel> result = new ArrayList<>();

        for (Map.Entry<Integer, List<PdbAtomLine>> entry : modelAtoms.entrySet()) {
            int modelNumber = entry.getKey();
            List<PdbAtomLine> atoms = entry.getValue();
            PdbModel pdbModel = new PdbModel(headerLine, modelNumber, atoms, modifiedResidues, missingResidues);
            result.add(pdbModel);
        }

        return result;
    }
}
