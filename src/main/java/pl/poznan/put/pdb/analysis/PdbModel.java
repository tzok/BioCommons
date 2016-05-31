package pl.poznan.put.pdb.analysis;

import pl.poznan.put.pdb.*;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

public class PdbModel implements Serializable, ResidueCollection {
    private final List<PdbChain> chains = new ArrayList<>();
    private final List<PdbResidue> residues = new ArrayList<>();

    private final Set<PdbResidueIdentifier> missingResiduesIdentifiers = new HashSet<>();
    private final Map<PdbResidueIdentifier, PdbModresLine> identifierToModification = new HashMap<>();
    private final Map<PdbResidueIdentifier, PdbResidue> identifierToResidue = new HashMap<>();
    private final Map<PdbResidueIdentifier, PdbChain> identifierToChain = new HashMap<>();

    private final PdbHeaderLine headerLine;
    private final PdbExpdtaLine experimentalDataLine;
    private final PdbRemark2Line resolutionLine;
    private final int modelNumber;
    private final List<PdbAtomLine> atoms;
    private final List<PdbModresLine> modifiedResidues;

    public PdbModel(List<PdbAtomLine> atoms) throws PdbParsingException {
        this(PdbHeaderLine.emptyInstance(), PdbExpdtaLine.emptyInstance(), PdbRemark2Line.emptyInstance(), 1, atoms, Collections.<PdbModresLine>emptyList(), Collections.<PdbRemark465Line>emptyList());
    }

    public PdbModel(PdbHeaderLine headerLine, PdbExpdtaLine experimentalDataLine, PdbRemark2Line resolutionLine, int modelNumber, List<PdbAtomLine> atoms, List<PdbModresLine> modifiedResidues, List<PdbRemark465Line> missingResidues) throws PdbParsingException {
        super();
        this.headerLine = headerLine;
        this.experimentalDataLine = experimentalDataLine;
        this.resolutionLine = resolutionLine;
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

        List<PdbAtomLine> residueAtoms = new ArrayList<>();
        PdbResidueIdentifier lastResidueIdentifier = PdbResidueIdentifier.fromChainNumberICode(atoms.get(0));

        for (PdbAtomLine atom : atoms) {
            PdbResidueIdentifier residueIdentifier = PdbResidueIdentifier.fromChainNumberICode(atom);

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
            PdbResidue residue = new PdbResidue(PdbResidueIdentifier.fromChainNumberICode(missingResidue), missingResidue.getResidueName(), emptyAtomList, true);

            String chain = residue.getChainIdentifier();
            boolean isChainFound = false;
            int i = 0;

            while (i < residues.size()) {
                PdbResidue existing = residues.get(i);
                String existingChain = existing.getChainIdentifier();

                if (!isChainFound && chain.equals(existingChain)) {
                    isChainFound = true;
                }

                if (isChainFound && (existing.compareTo(residue) > 0 || !chain.equals(existingChain))) {
                    residues.add(i, residue);
                    break;
                }

                i += 1;
            }
        }

        if (residues.size() == 0) {
            throw new PdbParsingException("Invalid PDB file. Failed to analyze any residue");
        }
    }

    private void saveExistingResidueIfValid(List<PdbAtomLine> residueAtoms, PdbResidueIdentifier residueIdentifier) {
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

    public List<PdbAtomLine> getAtoms() {
        return Collections.unmodifiableList(atoms);
    }

    @Override
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

    @Override
    public PdbResidue findResidue(String chainIdentifier, int residueNumber, String insertionCode) {
        return findResidue(new PdbResidueIdentifier(chainIdentifier, residueNumber, insertionCode));
    }

    @Override
    public PdbResidue findResidue(PdbResidueIdentifier query) {
        if (!identifierToResidue.containsKey(query)) {
            throw new IllegalArgumentException("Failed to find residue: " + query);
        }

        return identifierToResidue.get(query);
    }

    public PdbChain findChainContainingResidue(PdbResidueIdentifier residueIdentifier) {
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
        List<PdbAtomLine> filteredAtoms = new ArrayList<>();
        List<PdbRemark465Line> filteredMissing = new ArrayList<>();

        for (PdbResidue residue : residues) {
            if (residue.getMoleculeType() == moleculeType) {
                if (!residue.isMissing()) {
                    filteredAtoms.addAll(residue.getAtoms());
                } else {
                    String residueName = residue.getOriginalResidueName();
                    String chainIdentifier = residue.getChainIdentifier();
                    int residueNumber = residue.getResidueNumber();
                    String insertionCode = residue.getInsertionCode();
                    filteredMissing.add(new PdbRemark465Line(modelNumber, residueName, chainIdentifier, residueNumber, insertionCode));
                }
            }
        }

        return new PdbModel(headerLine, experimentalDataLine, resolutionLine, modelNumber, filteredAtoms, modifiedResidues, filteredMissing);
    }
}
