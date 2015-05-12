package pl.poznan.put.pdb.analysis;

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

import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbHeaderLine;
import pl.poznan.put.pdb.PdbModresLine;
import pl.poznan.put.pdb.PdbParsingException;
import pl.poznan.put.pdb.PdbRemark465Line;
import pl.poznan.put.pdb.PdbResidueIdentifier;

public class PdbModel implements Serializable {
    private final List<PdbChain> chains = new ArrayList<PdbChain>();
    private final List<PdbResidue> residues = new ArrayList<PdbResidue>();

    private final Set<PdbResidueIdentifier> missingResiduesIdentifiers = new HashSet<PdbResidueIdentifier>();
    private final Map<PdbResidueIdentifier, PdbModresLine> identifierToModification = new HashMap<PdbResidueIdentifier, PdbModresLine>();
    private final Map<PdbResidueIdentifier, PdbResidue> identifierToResidue = new HashMap<PdbResidueIdentifier, PdbResidue>();
    private final Map<PdbResidueIdentifier, PdbChain> identifierToChain = new HashMap<PdbResidueIdentifier, PdbChain>();

    private final PdbHeaderLine headerLine;
    private final int modelNumber;
    private final List<PdbAtomLine> atoms;
    private final List<PdbModresLine> modifiedResidues;

    public PdbModel(List<PdbAtomLine> atoms) throws PdbParsingException {
        this(PdbHeaderLine.emptyInstance(), 1, atoms, Collections.<PdbModresLine> emptyList(), Collections.<PdbRemark465Line> emptyList());
    }

    public PdbModel(PdbHeaderLine headerLine, int modelNumber,
            List<PdbAtomLine> atoms, List<PdbModresLine> modifiedResidues,
            List<PdbRemark465Line> missingResidues) throws PdbParsingException {
        super();
        this.headerLine = headerLine;
        this.modelNumber = modelNumber;
        this.atoms = atoms;
        this.modifiedResidues = modifiedResidues;

        for (PdbRemark465Line missing : missingResidues) {
            missingResiduesIdentifiers.add(missing.getResidueIdentifier());
        }
        for (PdbModresLine modified : modifiedResidues) {
            identifierToModification.put(modified.getResidueIdentifier(), modified);
        }

        analyzeResidues(missingResidues);
        analyzeChains();

        for (PdbResidue residue : residues) {
            identifierToResidue.put(residue.getResidueIdentifier(), residue);
        }
    }

    private void analyzeResidues(List<PdbRemark465Line> missingResidues) throws PdbParsingException {
        assert atoms.size() > 0;

        List<PdbAtomLine> residueAtoms = new ArrayList<PdbAtomLine>();
        PdbResidueIdentifier lastResidueIdentifier = PdbResidueIdentifier.fromChainNumberICode(atoms.get(0));

        for (PdbAtomLine atom : atoms) {
            PdbResidueIdentifier residueIdentifier = PdbResidueIdentifier.fromChainNumberICode(atom);

            if (!residueIdentifier.equals(lastResidueIdentifier)) {
                saveExistingResidueIfValid(residueAtoms, lastResidueIdentifier);
                residueAtoms = new ArrayList<PdbAtomLine>();
                lastResidueIdentifier = residueIdentifier;
            }

            residueAtoms.add(atom);
        }

        saveExistingResidueIfValid(residueAtoms, lastResidueIdentifier);

        for (PdbRemark465Line missingResidue : missingResidues) {
            List<PdbAtomLine> emptyAtomList = Collections.emptyList();
            PdbResidue residue = new PdbResidue(PdbResidueIdentifier.fromChainNumberICode(missingResidue), missingResidue.getResidueName(), emptyAtomList, true);

            for (int i = 0; i < residues.size(); i++) {
                if (residues.get(i).compareTo(residue) > 0) {
                    residues.add(i, residue);
                    break;
                }
            }
        }

        if (residues.size() == 0) {
            throw new PdbParsingException("Invalid PDB file. Failed to analyze any residue");
        }
    }

    private void saveExistingResidueIfValid(List<PdbAtomLine> residueAtoms,
            PdbResidueIdentifier residueIdentifier) {
        assert !isMissing(residueIdentifier);
        assert residueAtoms.size() > 0;

        String residueName = residueAtoms.get(0).getResidueName();
        String modifiedResidueName = residueName;
        boolean isModified = isModified(residueIdentifier);

        if (isModified) {
            modifiedResidueName = getModifiedResidueName(residueIdentifier);
        }

        PdbResidue residue = new PdbResidue(residueIdentifier, residueName, modifiedResidueName, residueAtoms, isModified, false);

        if (residue.wasSuccessfullyDetected()) {
            residues.add(residue);
        }
    }

    private String getModifiedResidueName(PdbResidueIdentifier residueIdentifier) {
        if (!identifierToModification.containsKey(residueIdentifier)) {
            throw new IllegalArgumentException("Failed to find information about modification of: " + residueIdentifier);
        }

        return identifierToModification.get(residueIdentifier).getStandardResidueName();
    }

    private void analyzeChains() {
        assert residues.size() > 0;

        Map<Character, List<PdbResidue>> foundChains = new LinkedHashMap<Character, List<PdbResidue>>();

        for (PdbResidue residue : residues) {
            char chainIdentifier = residue.getChainIdentifier();

            if (!foundChains.containsKey(chainIdentifier)) {
                foundChains.put(chainIdentifier, new ArrayList<PdbResidue>());
            }

            List<PdbResidue> chainResidues = foundChains.get(chainIdentifier);
            chainResidues.add(residue);
        }

        for (Entry<Character, List<PdbResidue>> entry : foundChains.entrySet()) {
            char chainIdentifier = entry.getKey();
            List<PdbResidue> chainResidues = entry.getValue();
            PdbChain chain = new PdbChain(chainIdentifier, chainResidues);
            chains.add(chain);

            for (PdbResidue residue : chainResidues) {
                identifierToChain.put(residue.getResidueIdentifier(), chain);
            }
        }
    }

    public int getModelNumber() {
        return modelNumber;
    }

    public List<PdbAtomLine> getAtoms() {
        return Collections.unmodifiableList(atoms);
    }

    public List<PdbResidue> getResidues() {
        return Collections.unmodifiableList(residues);
    }

    public List<PdbChain> getChains() {
        return Collections.unmodifiableList(chains);
    }

    public String getIdCode() {
        return headerLine.getIdCode();
    }

    public boolean isModified(PdbResidueIdentifier residueIdentifier) {
        return identifierToModification.containsKey(residueIdentifier);
    }

    public boolean isMissing(PdbResidueIdentifier residueIdentifier) {
        return missingResiduesIdentifiers.contains(residueIdentifier);
    }

    public PdbResidue findResidue(char chainIdentifier, int residueNumber,
            char insertionCode) {
        return findResidue(new PdbResidueIdentifier(chainIdentifier, residueNumber, insertionCode));
    }

    public PdbResidue findResidue(PdbResidueIdentifier query) {
        if (!identifierToResidue.containsKey(query)) {
            throw new IllegalArgumentException("Failed to find residue: " + query);
        }

        return identifierToResidue.get(query);
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
                builder.append(atom);
                builder.append('\n');
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

    public PdbModel filteredNewInstance(MoleculeType moleculeType) throws PdbParsingException {
        List<PdbAtomLine> filteredAtoms = new ArrayList<PdbAtomLine>();
        List<PdbRemark465Line> filteredMissing = new ArrayList<PdbRemark465Line>();

        for (PdbResidue residue : residues) {
            if (residue.getMoleculeType() == moleculeType) {
                if (!residue.isMissing()) {
                    filteredAtoms.addAll(residue.getAtoms());
                } else {
                    String residueName = residue.getOriginalResidueName();
                    char chainIdentifier = residue.getChainIdentifier();
                    int residueNumber = residue.getResidueNumber();
                    char insertionCode = residue.getInsertionCode();
                    filteredMissing.add(new PdbRemark465Line(modelNumber, residueName, chainIdentifier, residueNumber, insertionCode));
                }
            }
        }

        return new PdbModel(headerLine, modelNumber, filteredAtoms, modifiedResidues, filteredMissing);
    }
}
