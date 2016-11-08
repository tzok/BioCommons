package pl.poznan.put.pdb.analysis;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.bidimap.TreeBidiMap;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbModresLine;
import pl.poznan.put.pdb.PdbParsingException;
import pl.poznan.put.pdb.PdbRemark2Line;
import pl.poznan.put.pdb.PdbRemark465Line;
import pl.poznan.put.structure.secondary.BasePair;
import pl.poznan.put.structure.secondary.QuantifiedBasePair;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class CifConverter {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(CifConverter.class);

    // PRINTABLE_CHARS is a set of chain names that we allow
    private static final List<String> PRINTABLE_CHARS = new ArrayList<>();

    static {
        for (char c = 'A'; c <= 'Z'; c++) {
            CifConverter.PRINTABLE_CHARS.add(Character.toString(c));
        }
        for (char c = 'a'; c <= 'z'; c++) {
            CifConverter.PRINTABLE_CHARS.add(Character.toString(c));
        }
        for (char c = '0'; c <= '9'; c++) {
            CifConverter.PRINTABLE_CHARS.add(Character.toString(c));
        }
    }

    private static final ModelContainer EMPTY_MODEL_CONTAINER
            = new ModelContainer() {
        @Override
        public boolean isCif() {
            return false;
        }

        public File getCifFile() {
            throw new UnsupportedOperationException(
                    "Container does not represent mmCIF file");
        }

        @Override
        public Set<File> getPdbFiles() {
            return Collections.emptySet();
        }

        @Override
        public String getCifChain(File pdbFile, String pdbChain) {
            return pdbChain;
        }

        @Override
        public String getPdbChain(File pdbFile, String cifChain) {
            return cifChain;
        }
    };

    public static ModelContainer convert(final File cifFile)
            throws IOException, PdbParsingException {
        CifParser cifParser = new CifParser();
        String cifContents = FileUtils
                .readFileToString(cifFile, Charset.defaultCharset());
        List<CifModel> models = cifParser.parse(cifContents);
        return CifConverter.convert(cifFile, models);
    }

    private static ModelContainer convert(final File mmCifFile,
                                          final List<CifModel> models)
            throws IOException, PdbParsingException {
        List<CifModel> rnaModels = new ArrayList<>();

        for (CifModel model : models) {
            if (model.containsAny(MoleculeType.RNA)) {
                rnaModels.add(model.filteredNewInstance(MoleculeType.RNA));
            }
        }

        if (rnaModels.isEmpty()) {
            CifConverter.LOGGER.info("Neither model contain any RNA chain");
            return CifConverter.EMPTY_MODEL_CONTAINER;
        }

        for (CifModel model : rnaModels) {
            for (PdbChain chain : model.getChains()) {
                for (PdbResidue residue : chain.getResidues()) {
                    if (residue.getResidueNumber() > 9999) {
                        CifConverter.LOGGER
                                .error("Cannot continue. Chain {} has residue"
                                               + " of index > 9999",
                                       chain.getIdentifier());
                        return CifConverter.EMPTY_MODEL_CONTAINER;
                    }
                }
            }
        }

        CifModel firstModel = rnaModels.get(0);
        List<Set<String>> chainGroups = CifConverter
                .groupContactingChains(firstModel);
        chainGroups = CifConverter.packGroups(chainGroups);
        Map<File, BidiMap<String, String>> fileChainMap = new HashMap<>();

        for (Set<String> chainGroup : chainGroups) {
            StringBuilder pdbBuilder = new StringBuilder();
            File pdbFile = File.createTempFile("cif2pdb", ".pdb");
            BidiMap<String, String> chainMap = new TreeBidiMap<>();

            fileChainMap.put(pdbFile, chainMap);

            CifConverter.writeHeader(firstModel, chainMap, pdbBuilder);
            for (CifModel model : rnaModels) {
                CifConverter
                        .writeModel(model, chainGroup, chainMap, pdbBuilder);
            }

            FileUtils.write(pdbFile, pdbBuilder.toString(),
                            Charset.defaultCharset());
        }

        return new CifContainer(mmCifFile, fileChainMap);
    }

    /**
     * Solve bin packing problem using first-fit decreasing heuristic. In other
     * words, put as many chains into as few separate files as possible.
     *
     * @param chainGroups List of chain groups. A chain group contains
     *                    identifiers of chains which are in contact.
     * @return List of packed chain groups. A packed chain group contains one or
     * more regular chain groups such that they can be fitted into a single PDB
     * file.
     */
    private static List<Set<String>> packGroups(
            final List<Set<String>> chainGroups) {
        // sort chain groups in descending size order
        Collections.sort(chainGroups, new Comparator<Set<String>>() {
            @Override
            public int compare(final Set<String> t, final Set<String> t1) {
                return -Integer.compare(t.size(), t1.size());
            }
        });

        List<Set<String>> packed = new ArrayList<>();

        for (Set<String> group : chainGroups) {
            boolean flag = false;

            for (Set<String> bin : packed) {
                if ((bin.size() + group.size()) <= CifConverter.PRINTABLE_CHARS
                        .size()) {
                    bin.addAll(group);
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                Set<String> bin = new HashSet<>(group);
                packed.add(bin);
            }
        }

        return packed;
    }

    /**
     * Starting with a one-element set for each chain, merge them basing on
     * the contact information.
     *
     * @param model A parsed mmCIF model.
     * @return A list of sets of chains' identifiers. Each set contains
     * chains which are in contact with each other (single linkage i.e. each
     * chain has at least one contact in its set).
     */
    private static List<Set<String>> groupContactingChains(
            final CifModel model) {
        List<Set<String>> chainGroups = CifConverter
                .initializeChainGroups(model);
        Map<String, Set<String>> chainContacts = CifConverter
                .initializeChainContactMap(model);
        int i = 0;

        while ((chainGroups.size() > 1) && (i < chainGroups.size())) {
            Set<String> groupL = chainGroups.get(i);
            int toMerge = -1;

            for (int j = i + 1; (toMerge == -1) && (j < chainGroups.size());
                 j++) {
                Set<String> groupR = chainGroups.get(j);

                for (String chainL : groupL) {
                    if (chainContacts.containsKey(chainL)) {
                        Set<String> contactsL = chainContacts.get(chainL);
                        if (CollectionUtils.containsAny(contactsL, groupR)) {
                            toMerge = j;
                            break;
                        }
                    }
                }
            }

            if (toMerge == -1) {
                i += 1;
            } else {
                groupL.addAll(chainGroups.get(toMerge));
                chainGroups.remove(toMerge);
                i = 0;
            }
        }

        return chainGroups;
    }

    /**
     * For each chain, create a set which contains only this chain.
     *
     * @param model A parsed coordinates of a mmCIF file.
     * @return A list of sets, each containing one chain.
     */
    private static List<Set<String>> initializeChainGroups(
            final CifModel model) {
        List<Set<String>> chainGroups = new ArrayList<>();
        for (PdbChain chain : model.getChains()) {
            chainGroups.add(new HashSet<>(
                    Collections.singleton(chain.getIdentifier())));
        }
        return chainGroups;
    }

    /**
     * Basing on the information in the mmCIF file itself, analyze which
     * chains are in contact. Return a mapping of chain name to a set of its
     * contacts.
     *
     * @param model A parsed mmCIF model.
     * @return A map, where chain name is a key and set of this chain's
     * contacts is the value.
     */
    private static Map<String, Set<String>> initializeChainContactMap(
            final CifModel model) {
        Map<String, Set<String>> chainContacts = new HashMap<>();

        for (QuantifiedBasePair quantifiedBasePair : model.getBasePairs()) {
            BasePair basePair = quantifiedBasePair.getBasePair();
            String left = basePair.getLeft().getChainIdentifier();
            String right = basePair.getRight().getChainIdentifier();

            if (!chainContacts.containsKey(left)) {
                chainContacts.put(left, new HashSet<String>());
            }
            chainContacts.get(left).add(right);

            if (!chainContacts.containsKey(right)) {
                chainContacts.put(right, new HashSet<String>());
            }
            chainContacts.get(right).add(left);
        }

        return chainContacts;
    }

    private static void writeModel(final PdbModel rnaModel,
                                   final Collection<String> allowedChains,
                                   final BidiMap<String, String> chainMap,
                                   final StringBuilder pdbBuilder) {
        pdbBuilder.append("MODEL ").append(rnaModel.getModelNumber())
                  .append('\n');

        int serialNumber = 1;

        for (PdbChain chain : rnaModel.getChains()) {
            if (allowedChains.contains(chain.getIdentifier())) {
                for (PdbResidue residue : chain.getResidues()) {
                    for (PdbAtomLine atom : residue.getAtoms()) {
                        String chainIdentifier = CifConverter
                                .mapChain(chainMap, atom.getChainIdentifier());
                        atom = atom.replaceSerialNumber(serialNumber);
                        atom = atom.replaceChainIdentifier(chainIdentifier);
                        serialNumber = (serialNumber < 99999) ? (serialNumber
                                + 1) : 1;
                        pdbBuilder.append(atom).append('\n');
                    }
                }

                pdbBuilder.append("TER                        \n");
            }
        }

        pdbBuilder.append("ENDMDL\n");
    }

    private static void writeHeader(final PdbModel firstModel,
                                    final BidiMap<String, String> chainMap,
                                    final StringBuilder pdbBuilder) {
        pdbBuilder.append(firstModel.getHeaderLine()).append('\n');

        pdbBuilder.append(firstModel.getExperimentalDataLine()).append('\n');
        pdbBuilder.append(PdbRemark2Line.PROLOGUE).append('\n');
        pdbBuilder.append(firstModel.getResolutionLine()).append('\n');

        List<PdbRemark465Line> missingResidues = firstModel
                .getMissingResidues();
        if (!missingResidues.isEmpty()) {
            pdbBuilder.append(PdbRemark465Line.PROLOGUE).append('\n');

            for (PdbRemark465Line missingResidue : missingResidues) {
                String chainIdentifier = missingResidue.getChainIdentifier();
                MoleculeType moleculeType = CifConverter
                        .getChainType(firstModel, chainIdentifier);
                if (moleculeType == MoleculeType.RNA) {
                    chainIdentifier = CifConverter
                            .mapChain(chainMap, chainIdentifier);
                    missingResidue = missingResidue
                            .replaceChainIdentifier(chainIdentifier);
                    pdbBuilder.append(missingResidue).append('\n');
                }
            }
        }

        for (PdbModresLine modifiedResidue : firstModel.getModifiedResidues()) {
            pdbBuilder.append(modifiedResidue).append('\n');
        }
    }

    /**
     * Return type of the named chain.
     *
     * @param firstModel      A PDB/mmCIF model.
     * @param chainIdentifier Name of the chain to check.
     * @return A {@link MoleculeType} instance representing chain's type or
     * {@link MoleculeType#UNKNOWN} if the chain is not present.
     */
    private static MoleculeType getChainType(final PdbModel firstModel,
                                             final String chainIdentifier) {
        for (PdbChain chain : firstModel.getChains()) {
            if (chain.getIdentifier().equals(chainIdentifier)) {
                return chain.getMoleculeType();
            }
        }
        return MoleculeType.UNKNOWN;
    }

    /**
     * For a chain identifier in mmCIF, return its single-letter mapping from
     * PDB. If the mmCIF identifier is encountered for the first time, add a
     * new mapping.
     *
     * @param chainMap        A map where key is the mmCIF name and value is
     *                        the PDB name.
     * @param chainIdentifier The mmCIF identifier.
     * @return The PDB identifier.
     */
    private static String mapChain(final BidiMap<String, String> chainMap,
                                   final String chainIdentifier) {
        if (!chainMap.containsKey(chainIdentifier)) {
            chainMap.put(chainIdentifier,
                         CifConverter.PRINTABLE_CHARS.get(chainMap.size()));
        }
        return chainMap.get(chainIdentifier);
    }

    private CifConverter() {
        super();
    }
}
