package pl.poznan.put.pdb.analysis;

import org.apache.commons.lang3.tuple.Pair;
import pl.poznan.put.atom.AtomName;
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PdbModel implements Serializable, ResidueCollection {
    private static final long serialVersionUID = -2769758924241993375L;

    protected final PdbHeaderLine headerLine;
    protected final PdbExpdtaLine experimentalDataLine;
    protected final PdbRemark2Line resolutionLine;
    protected final int modelNumber;
    protected final List<PdbModresLine> modifiedResidues;
    private final List<PdbChain> chains = new ArrayList<>();
    private final List<PdbResidue> residues = new ArrayList<>();
    private final Collection<PdbResidueIdentifier> missingResiduesIdentifiers =
            new HashSet<>();
    private final Map<PdbResidueIdentifier, PdbModresLine>
            identifierToModification = new HashMap<>();
    private final Map<PdbResidueIdentifier, PdbResidue> identifierToResidue =
            new HashMap<>();
    private final Map<PdbResidueIdentifier, PdbChain> identifierToChain =
            new HashMap<>();
    private final List<PdbAtomLine> atoms;
    private final List<PdbRemark465Line> missingResidues;

    public PdbModel(final List<PdbAtomLine> atoms) throws PdbParsingException {
        this(PdbHeaderLine.emptyInstance(), PdbExpdtaLine.emptyInstance(),
             PdbRemark2Line.emptyInstance(), 1, atoms,
             Collections.<PdbModresLine>emptyList(),
             Collections.<PdbRemark465Line>emptyList());
    }

    public PdbModel(
            final PdbHeaderLine headerLine,
            final PdbExpdtaLine experimentalDataLine,
            final PdbRemark2Line resolutionLine, final int modelNumber,
            final List<PdbAtomLine> atoms,
            final List<PdbModresLine> modifiedResidues,
            final List<PdbRemark465Line> missingResidues)
            throws PdbParsingException {
        super();
        this.headerLine = headerLine;
        this.experimentalDataLine = experimentalDataLine;
        this.resolutionLine = resolutionLine;
        this.modelNumber = modelNumber;
        this.atoms = new ArrayList<>(atoms);
        this.modifiedResidues = new ArrayList<>(modifiedResidues);
        this.missingResidues = new ArrayList<>(missingResidues);

        for (final PdbRemark465Line missing : missingResidues) {
            missingResiduesIdentifiers.add(missing.getResidueIdentifier());
        }
        for (final PdbModresLine modified : modifiedResidues) {
            identifierToModification
                    .put(modified.getResidueIdentifier(), modified);
        }

        analyzeResidues();
        analyzeChains();

        for (final PdbResidue residue : residues) {
            identifierToResidue.put(residue.getResidueIdentifier(), residue);
        }
    }

    private void analyzeResidues() throws PdbParsingException {
        assert !atoms.isEmpty();

        List<PdbAtomLine> residueAtoms = new ArrayList<>();
        PdbResidueIdentifier lastResidueIdentifier =
                PdbResidueIdentifier.fromChainNumberICode(atoms.get(0));

        for (final PdbAtomLine atom : atoms) {
            PdbResidueIdentifier residueIdentifier =
                    PdbResidueIdentifier.fromChainNumberICode(atom);

            if (!Objects.equals(residueIdentifier, lastResidueIdentifier)) {
                saveExistingResidueIfValid(residueAtoms, lastResidueIdentifier);
                residueAtoms = new ArrayList<>();
                lastResidueIdentifier = residueIdentifier;
            }

            residueAtoms.add(atom);
        }

        saveExistingResidueIfValid(residueAtoms, lastResidueIdentifier);

        for (final PdbRemark465Line missingResidue : missingResidues) {
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

                if (!isChainFound && Objects.equals(chain, existingChain)) {
                    isChainFound = true;
                }

                if (isChainFound && ((existing.compareTo(residue) > 0)
                                     || !Objects
                        .equals(chain, existingChain))) {
                    residues.add(i, residue);
                    break;
                }

                i += 1;
            }
        }

        if (residues.isEmpty()) {
            throw new PdbParsingException(
                    "Invalid PDB file. Failed to analyze any residue");
        }
    }

    private void analyzeChains() {
        assert !residues.isEmpty();

        Map<String, List<PdbResidue>> foundChains = new LinkedHashMap<>();

        for (final PdbResidue residue : residues) {
            String chainIdentifier = residue.getChainIdentifier();

            if (!foundChains.containsKey(chainIdentifier)) {
                foundChains.put(chainIdentifier, new ArrayList<PdbResidue>());
            }

            List<PdbResidue> chainResidues = foundChains.get(chainIdentifier);
            chainResidues.add(residue);
        }

        for (final Map.Entry<String, List<PdbResidue>> entry : foundChains
                .entrySet()) {
            String chainIdentifier = entry.getKey();
            List<PdbResidue> chainResidues = entry.getValue();
            PdbChain chain = new PdbChain(chainIdentifier, chainResidues);
            chains.add(chain);

            for (final PdbResidue residue : chainResidues) {
                identifierToChain.put(residue.getResidueIdentifier(), chain);
            }
        }
    }

    private void saveExistingResidueIfValid(
            final List<PdbAtomLine> residueAtoms,
            final PdbResidueIdentifier residueIdentifier) {
        assert !isMissing(residueIdentifier);
        assert !residueAtoms.isEmpty();

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

    public final boolean isMissing(
            final PdbResidueIdentifier residueIdentifier) {
        return missingResiduesIdentifiers.contains(residueIdentifier);
    }

    public final boolean isModified(
            final PdbResidueIdentifier residueIdentifier) {
        return identifierToModification.containsKey(residueIdentifier);
    }

    private String getModifiedResidueName(
            final PdbResidueIdentifier residueIdentifier) {
        if (!identifierToModification.containsKey(residueIdentifier)) {
            throw new IllegalArgumentException(
                    "Failed to find information about modification of: "
                    + residueIdentifier);
        }

        return identifierToModification.get(residueIdentifier)
                                       .getStandardResidueName();
    }

    public final PdbHeaderLine getHeaderLine() {
        return headerLine;
    }

    public final PdbExpdtaLine getExperimentalDataLine() {
        return experimentalDataLine;
    }

    public final PdbRemark2Line getResolutionLine() {
        return resolutionLine;
    }

    public final int getModelNumber() {
        return modelNumber;
    }

    public final Iterable<PdbModresLine> getModifiedResidues() {
        return Collections.unmodifiableList(modifiedResidues);
    }

    public final List<PdbRemark465Line> getMissingResidues() {
        return Collections.unmodifiableList(missingResidues);
    }

    public final List<PdbAtomLine> getAtoms() {
        return Collections.unmodifiableList(atoms);
    }

    @Override
    public final List<PdbResidue> getResidues() {
        return Collections.unmodifiableList(residues);
    }

    @Override
    public final PdbResidue findResidue(
            final String chainIdentifier, final int residueNumber,
            final String insertionCode) {
        return findResidue(
                new PdbResidueIdentifier(chainIdentifier, residueNumber,
                                         insertionCode));
    }

    @Override
    public final PdbResidue findResidue(final PdbResidueIdentifier query) {
        if (!identifierToResidue.containsKey(query)) {
            throw new IllegalArgumentException(
                    "Failed to find residue: " + query);
        }

        return identifierToResidue.get(query);
    }

    public final List<PdbChain> getChains() {
        return Collections.unmodifiableList(chains);
    }

    public final String getIdCode() {
        return headerLine.getIdCode();
    }

    public final PdbChain findChainContainingResidue(
            final PdbResidueIdentifier residueIdentifier) {
        return identifierToChain.get(residueIdentifier);
    }

    public final String getSequence() {
        StringBuilder builder = new StringBuilder();
        for (final PdbResidue residue : residues) {
            builder.append(residue.getOneLetterName());
        }
        return builder.toString();
    }

    @SuppressWarnings("HardcodedLineSeparator")
    public final String toPdbString() {
        Collection<Pair<PdbResidueIdentifier, AtomName>> resolved =
                new HashSet<>();
        StringBuilder builder = new StringBuilder();

        for (final PdbResidue residue : residues) {
            for (final PdbAtomLine atom : residue.getAtoms()) {
                Pair<PdbResidueIdentifier, AtomName> pair =
                        Pair.of(residue.getResidueIdentifier(),
                                atom.detectAtomName());

                if (!resolved.contains(pair)) {
                    builder.append(atom.replaceAlternateLocation(" "))
                           .append('\n');
                    resolved.add(pair);
                }
            }
        }

        return builder.toString();
    }

    @SuppressWarnings("HardcodedLineSeparator")
    public final String toCifString() {
        StringBuilder builder = new StringBuilder();
        builder.append("data_").append(getIdCode()).append('\n');
        builder.append(PdbAtomLine.CIF_LOOP).append('\n');

        Collection<Pair<PdbResidueIdentifier, AtomName>> resolved =
                new HashSet<>();
        for (final PdbResidue residue : residues) {
            for (final PdbAtomLine atom : residue.getAtoms()) {
                Pair<PdbResidueIdentifier, AtomName> pair =
                        Pair.of(residue.getResidueIdentifier(),
                                atom.detectAtomName());

                if (!resolved.contains(pair)) {
                    builder.append(atom.replaceAlternateLocation(" ").toCif())
                           .append('\n');
                    resolved.add(pair);
                }
            }
        }

        return builder.toString();
    }

    public final boolean containsAny(final MoleculeType moleculeType) {
        for (final PdbChain chain : chains) {
            if (chain.getMoleculeType() == moleculeType) {
                return true;
            }
        }
        return false;
    }

    public PdbModel filteredNewInstance(final MoleculeType moleculeType)
            throws PdbParsingException {
        List<PdbAtomLine> filteredAtoms = filterAtoms(moleculeType);
        List<PdbRemark465Line> filteredMissing = filterMissing(moleculeType);
        return new PdbModel(headerLine, experimentalDataLine, resolutionLine,
                            modelNumber, filteredAtoms, modifiedResidues,
                            filteredMissing);
    }

    protected final List<PdbAtomLine> filterAtoms(
            final MoleculeType moleculeType) {
        List<PdbAtomLine> filteredAtoms = new ArrayList<>();

        for (final PdbResidue residue : residues) {
            if ((residue.getMoleculeType() == moleculeType) && !residue
                    .isMissing()) {
                filteredAtoms.addAll(residue.getAtoms());
            }
        }

        return filteredAtoms;
    }

    protected final List<PdbRemark465Line> filterMissing(
            final MoleculeType moleculeType) {
        List<PdbRemark465Line> filteredMissing = new ArrayList<>();

        for (final PdbResidue residue : residues) {
            if ((residue.getMoleculeType() == moleculeType) && residue
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
