package pl.poznan.put.pdb.analysis;

import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbExpdtaLine;
import pl.poznan.put.pdb.PdbHeaderLine;
import pl.poznan.put.pdb.PdbModresLine;
import pl.poznan.put.pdb.PdbParsingException;
import pl.poznan.put.pdb.PdbRemark2Line;
import pl.poznan.put.pdb.PdbRemark465Line;
import pl.poznan.put.pdb.PdbResidueIdentifier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class PdbModel implements Serializable, ResidueCollection {
    protected final PdbHeaderLine headerLine;
    protected final PdbExpdtaLine experimentalDataLine;
    protected final PdbRemark2Line resolutionLine;
    protected final int modelNumber;
    protected final List<PdbModresLine> modifiedResidues;
    private final List<PdbChain> chains = new ArrayList<>();
    private final List<PdbResidue> residues = new ArrayList<>();
    private final Set<PdbResidueIdentifier> missingResiduesIdentifiers
            = new HashSet<>();
    private final Map<PdbResidueIdentifier, PdbModresLine>
            identifierToModification = new HashMap<>();
    private final Map<PdbResidueIdentifier, PdbResidue> identifierToResidue
            = new HashMap<>();
    private final Map<PdbResidueIdentifier, PdbChain> identifierToChain
            = new HashMap<>();
    private final List<PdbAtomLine> atoms;
    private final List<PdbRemark465Line> missingResidues;

    public PdbModel(List<PdbAtomLine> atoms) throws PdbParsingException {
        this(PdbHeaderLine.emptyInstance(), PdbExpdtaLine.emptyInstance(),
             PdbRemark2Line.emptyInstance(), 1, atoms,
             Collections.<PdbModresLine>emptyList(),
             Collections.<PdbRemark465Line>emptyList());
    }

    public PdbModel(PdbHeaderLine headerLine,
                    PdbExpdtaLine experimentalDataLine,
                    PdbRemark2Line resolutionLine, int modelNumber,
                    List<PdbAtomLine> atoms,
                    List<PdbModresLine> modifiedResidues,
                    List<PdbRemark465Line> missingResidues)
            throws PdbParsingException {
        super();
        this.headerLine = headerLine;
        this.experimentalDataLine = experimentalDataLine;
        this.resolutionLine = resolutionLine;
        this.modelNumber = modelNumber;
        this.atoms = atoms;
        this.modifiedResidues = modifiedResidues;
        this.missingResidues = missingResidues;

        for (PdbRemark465Line missing : missingResidues) {
            missingResiduesIdentifiers.add(missing.getResidueIdentifier());
        }
        for (PdbModresLine modified : modifiedResidues) {
            identifierToModification
                    .put(modified.getResidueIdentifier(), modified);
        }

        analyzeResidues(missingResidues);
        analyzeChains();

        for (PdbResidue residue : residues) {
            identifierToResidue.put(residue.getResidueIdentifier(), residue);
        }
    }

    private void analyzeResidues(List<PdbRemark465Line> missingResidues)
            throws PdbParsingException {
        assert atoms.size() > 0;

        List<PdbAtomLine> residueAtoms = new ArrayList<>();
        PdbResidueIdentifier lastResidueIdentifier = PdbResidueIdentifier
                .fromChainNumberICode(atoms.get(0));

        for (PdbAtomLine atom : atoms) {
            PdbResidueIdentifier residueIdentifier = PdbResidueIdentifier
                    .fromChainNumberICode(atom);

            if (!residueIdentifier.equals(lastResidueIdentifier)) {
                saveExistingResidueIfValid(residueAtoms, lastResidueIdentifier);
                residueAtoms = new ArrayList<>();
                lastResidueIdentifier = residueIdentifier;
            }

            residueAtoms.add(atom);
        }

        saveExistingResidueIfValid(residueAtoms, lastResidueIdentifier);

        for (PdbRemark465Line missingResidue : missingResidues) {
            List<PdbAtomLine> emptyAtomList = Collections.emptyList();
            PdbResidue residue = new PdbResidue(
                    PdbResidueIdentifier.fromChainNumberICode(missingResidue),
                    missingResidue.getResidueName(), emptyAtomList, true);

            String chain = residue.getChainIdentifier();
            boolean isChainFound = false;
            int i = 0;

            while (i < residues.size()) {
                PdbResidue existing = residues.get(i);
                String existingChain = existing.getChainIdentifier();

                if (!isChainFound && chain.equals(existingChain)) {
                    isChainFound = true;
                }

                if (isChainFound && (existing.compareTo(residue) > 0 || !chain
                        .equals(existingChain))) {
                    residues.add(i, residue);
                    break;
                }

                i += 1;
            }
        }

        if (residues.size() == 0) {
            throw new PdbParsingException(
                    "Invalid PDB file. Failed to analyze any residue");
        }
    }

    private void analyzeChains() {
        assert residues.size() > 0;

        Map<String, List<PdbResidue>> foundChains = new LinkedHashMap<>();

        for (PdbResidue residue : residues) {
            String chainIdentifier = residue.getChainIdentifier();

            if (!foundChains.containsKey(chainIdentifier)) {
                foundChains.put(chainIdentifier, new ArrayList<PdbResidue>());
            }

            List<PdbResidue> chainResidues = foundChains.get(chainIdentifier);
            chainResidues.add(residue);
        }

        for (Entry<String, List<PdbResidue>> entry : foundChains.entrySet()) {
            String chainIdentifier = entry.getKey();
            List<PdbResidue> chainResidues = entry.getValue();
            PdbChain chain = new PdbChain(chainIdentifier, chainResidues);
            chains.add(chain);

            for (PdbResidue residue : chainResidues) {
                identifierToChain.put(residue.getResidueIdentifier(), chain);
            }
        }
    }

    private void saveExistingResidueIfValid(List<PdbAtomLine> residueAtoms,
                                            PdbResidueIdentifier
                                                    residueIdentifier) {
        assert !isMissing(residueIdentifier);
        assert residueAtoms.size() > 0;

        String residueName = residueAtoms.get(0).getResidueName();
        String modifiedResidueName = residueName;
        boolean isModified = isModified(residueIdentifier);

        if (isModified) {
            modifiedResidueName = getModifiedResidueName(residueIdentifier);
        }

        PdbResidue residue = new PdbResidue(residueIdentifier, residueName,
                                            modifiedResidueName, residueAtoms,
                                            isModified, false);

        if (residue.wasSuccessfullyDetected()) {
            residues.add(residue);
        }
    }

    public boolean isMissing(PdbResidueIdentifier residueIdentifier) {
        return missingResiduesIdentifiers.contains(residueIdentifier);
    }

    public boolean isModified(PdbResidueIdentifier residueIdentifier) {
        return identifierToModification.containsKey(residueIdentifier);
    }

    private String getModifiedResidueName(
            PdbResidueIdentifier residueIdentifier) {
        if (!identifierToModification.containsKey(residueIdentifier)) {
            throw new IllegalArgumentException(
                    "Failed to find information about modification of: "
                            + residueIdentifier);
        }

        return identifierToModification.get(residueIdentifier)
                                       .getStandardResidueName();
    }

    public PdbHeaderLine getHeaderLine() {
        return headerLine;
    }

    public PdbExpdtaLine getExperimentalDataLine() {
        return experimentalDataLine;
    }

    public PdbRemark2Line getResolutionLine() {
        return resolutionLine;
    }

    public int getModelNumber() {
        return modelNumber;
    }

    public List<PdbModresLine> getModifiedResidues() {
        return Collections.unmodifiableList(modifiedResidues);
    }

    public List<PdbRemark465Line> getMissingResidues() {
        return Collections.unmodifiableList(missingResidues);
    }

    public List<PdbAtomLine> getAtoms() {
        return Collections.unmodifiableList(atoms);
    }

    @Override
    public List<PdbResidue> getResidues() {
        return Collections.unmodifiableList(residues);
    }

    @Override
    public PdbResidue findResidue(String chainIdentifier, int residueNumber,
                                  String insertionCode) {
        return findResidue(
                new PdbResidueIdentifier(chainIdentifier, residueNumber,
                                         insertionCode));
    }

    @Override
    public PdbResidue findResidue(PdbResidueIdentifier query) {
        if (!identifierToResidue.containsKey(query)) {
            throw new IllegalArgumentException(
                    "Failed to find residue: " + query);
        }

        return identifierToResidue.get(query);
    }

    public List<PdbChain> getChains() {
        return Collections.unmodifiableList(chains);
    }

    public String getIdCode() {
        return headerLine.getIdCode();
    }

    public PdbChain findChainContainingResidue(
            PdbResidueIdentifier residueIdentifier) {
        return identifierToChain.get(residueIdentifier);
    }

    public String getSequence() {
        StringBuilder builder = new StringBuilder();
        for (PdbResidue residue : residues) {
            builder.append(residue.getOneLetterName());
        }
        return builder.toString();
    }

    public String toPdbString() {
        StringBuilder builder = new StringBuilder();
        for (PdbResidue residue : residues) {
            for (PdbAtomLine atom : residue.getAtoms()) {
                builder.append(atom).append('\n');
            }
        }
        return builder.toString();
    }

    public String toCifString() {
        StringBuilder builder = new StringBuilder();
        builder.append("data_").append(getIdCode()).append('\n');
        builder.append(PdbAtomLine.CIF_LOOP).append('\n');

        for (PdbResidue residue : residues) {
            for (PdbAtomLine atom : residue.getAtoms()) {
                builder.append(atom.toCif()).append('\n');
            }
        }

        return builder.toString();
    }

    public boolean containsAny(MoleculeType moleculeType) {
        for (PdbChain chain : chains) {
            if (chain.getMoleculeType() == moleculeType) {
                return true;
            }
        }
        return false;
    }

    public PdbModel filteredNewInstance(MoleculeType moleculeType)
            throws PdbParsingException {
        List<PdbAtomLine> filteredAtoms = filterAtoms(moleculeType);
        List<PdbRemark465Line> filteredMissing = filterMissing(moleculeType);
        return new PdbModel(headerLine, experimentalDataLine, resolutionLine,
                            modelNumber, filteredAtoms, modifiedResidues,
                            filteredMissing);
    }

    protected List<PdbAtomLine> filterAtoms(MoleculeType moleculeType) {
        List<PdbAtomLine> filteredAtoms = new ArrayList<>();

        for (PdbResidue residue : residues) {
            if (residue.getMoleculeType() == moleculeType && !residue
                    .isMissing()) {
                filteredAtoms.addAll(residue.getAtoms());
            }
        }

        return filteredAtoms;
    }

    protected List<PdbRemark465Line> filterMissing(MoleculeType moleculeType) {
        List<PdbRemark465Line> filteredMissing = new ArrayList<>();

        for (PdbResidue residue : residues) {
            if (residue.getMoleculeType() == moleculeType && residue
                    .isMissing()) {
                String residueName = residue.getOriginalResidueName();
                String chainIdentifier = residue.getChainIdentifier();
                int residueNumber = residue.getResidueNumber();
                String insertionCode = residue.getInsertionCode();
                filteredMissing
                        .add(new PdbRemark465Line(modelNumber, residueName,
                                                  chainIdentifier,
                                                  residueNumber,
                                                  insertionCode));
            }
        }

        return filteredMissing;
    }
}
