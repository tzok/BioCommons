package pl.poznan.put.pdb.analysis;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.bidimap.TreeBidiMap;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbExpdtaLine;
import pl.poznan.put.pdb.PdbModresLine;
import pl.poznan.put.pdb.PdbParsingException;
import pl.poznan.put.pdb.PdbRemark2Line;
import pl.poznan.put.pdb.PdbRemark465Line;
import pl.poznan.put.structure.secondary.BasePair;
import pl.poznan.put.structure.secondary.QuantifiedBasePair;

public final class CifConverter {
  private static final Logger LOGGER = LoggerFactory.getLogger(CifConverter.class);

  private static final int MAX_RESIDUE_NUMBER = 9999;
  private static final int MAX_ATOM_SERIAL_NUMBER = 99999;

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

  public static ModelContainer convert(final File cifFile) throws IOException, PdbParsingException {
    final StructureParser cifParser = new CifParser();
    final String cifContents = FileUtils.readFileToString(cifFile, Charset.defaultCharset());
    final Iterable<CifModel> models = (Iterable<CifModel>) cifParser.parse(cifContents);
    return CifConverter.convert(cifFile, models);
  }

  private static ModelContainer convert(final File mmCifFile, final Iterable<CifModel> models)
      throws IOException, PdbParsingException {
    final List<CifModel> rnaModels = new ArrayList<>();

    for (final CifModel model : models) {
      if (model.containsAny(MoleculeType.RNA)) {
        final CifModel rnaModel = model.filteredNewInstance(MoleculeType.RNA);
        rnaModels.add(rnaModel);
      }
    }

    if (rnaModels.isEmpty()) {
      CifConverter.LOGGER.info("Neither model contain any RNA chain");
      return EmptyModelContainer.getInstance();
    }

    for (final PdbModel model : rnaModels) {
      if (!CifConverter.isConversionPossible(model)) {
        return EmptyModelContainer.getInstance();
      }
    }

    final CifModel firstModel = rnaModels.get(0);
    List<Set<String>> chainGroups = CifConverter.groupContactingChains(firstModel);
    chainGroups = CifConverter.packGroups(chainGroups);
    final Map<File, BidiMap<String, String>> fileChainMap = new HashMap<>();

    for (final Set<String> chainGroup : chainGroups) {
      final File pdbFile = File.createTempFile("cif2pdb", ".pdb");
      final BidiMap<String, String> chainMap = new TreeBidiMap<>();

      fileChainMap.put(pdbFile, chainMap);

      final StringBuilder pdbBuilder = new StringBuilder();
      CifConverter.writeHeader(firstModel, chainMap, pdbBuilder);
      for (final PdbModel model : rnaModels) {
        CifConverter.writeModel(model, chainGroup, chainMap, pdbBuilder);
      }

      final String pdbData = pdbBuilder.toString();
      FileUtils.write(pdbFile, pdbData, Charset.defaultCharset());
    }

    return new CifContainer(mmCifFile, fileChainMap);
  }

  private static boolean isConversionPossible(final PdbModel model) {
    for (final PdbChain chain : model.getChains()) {
      for (final PdbResidue residue : chain.getResidues()) {
        if (residue.getResidueNumber() > CifConverter.MAX_RESIDUE_NUMBER) {
          CifConverter.LOGGER.error(
              "Cannot continue. Chain {} has residue" + " of index > 9999", chain.getIdentifier());
          return false;
        }
      }
    }
    return true;
  }

  public static ModelContainer convert(final CifModel model) throws IOException {
    assert model.containsAny(MoleculeType.RNA);

    if (!CifConverter.isConversionPossible(model)) {
      return EmptyModelContainer.getInstance();
    }

    List<Set<String>> chainGroups = CifConverter.groupContactingChains(model);
    chainGroups = CifConverter.packGroups(chainGroups);
    final Map<File, BidiMap<String, String>> fileChainMap = new HashMap<>();

    for (final Set<String> chainGroup : chainGroups) {
      final File pdbFile = File.createTempFile("cif2pdb", ".pdb");
      final BidiMap<String, String> chainMap = new TreeBidiMap<>();

      fileChainMap.put(pdbFile, chainMap);

      final StringBuilder pdbBuilder = new StringBuilder();
      CifConverter.writeHeader(model, chainMap, pdbBuilder);
      CifConverter.writeModel(model, chainGroup, chainMap, pdbBuilder);

      final String pdbData = pdbBuilder.toString();
      FileUtils.write(pdbFile, pdbData, Charset.defaultCharset());
    }

    final File cifFile = File.createTempFile("cif2pdb", ".cif");
    FileUtils.write(cifFile, model.toCifString(), Charset.defaultCharset());
    return new CifContainer(cifFile, fileChainMap);
  }

  /**
   * Solve bin packing problem using first-fit decreasing heuristic. In other words, put as many
   * chains into as few separate files as possible.
   *
   * @param chainGroups List of chain groups. A chain group contains identifiers of chains which are
   *     in contact.
   * @return List of packed chain groups. A packed chain group contains one or more regular chain
   *     groups such that they can be fitted into a single PDB file.
   */
  private static List<Set<String>> packGroups(final List<Set<String>> chainGroups) {
    // sort chain groups in descending size order
    chainGroups.sort((t, t1) -> -Integer.compare(t.size(), t1.size()));

    final List<Set<String>> packed = new ArrayList<>();

    for (final Set<String> group : chainGroups) {
      boolean flag = true;

      for (final Set<String> bin : packed) {
        if ((bin.size() + group.size()) <= CifConverter.PRINTABLE_CHARS.size()) {
          bin.addAll(group);
          flag = false;
          break;
        }
      }

      if (flag) {
        final Set<String> bin = new HashSet<>(group);
        packed.add(bin);
      }
    }

    return packed;
  }

  /**
   * Starting with a one-element set for each chain, merge them basing on the contact information.
   *
   * @param model A parsed mmCIF model.
   * @return A list of sets of chains' identifiers. Each set contains chains which are in contact
   *     with each other (single linkage i.e. each chain has at least one contact in its set).
   */
  private static List<Set<String>> groupContactingChains(final CifModel model) {
    final List<Set<String>> chainGroups = CifConverter.initializeChainGroups(model);
    final Map<String, Set<String>> chainContacts = CifConverter.initializeChainContactMap(model);
    int i = 0;

    while ((chainGroups.size() > 1) && (i < chainGroups.size())) {
      final Set<String> groupL = chainGroups.get(i);
      int toMerge = -1;

      for (int j = i + 1; (toMerge == -1) && (j < chainGroups.size()); j++) {
        final Set<String> groupR = chainGroups.get(j);

        for (final String chainL : groupL) {
          if (chainContacts.containsKey(chainL)) {
            final Set<String> contactsL = chainContacts.get(chainL);
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
        final Set<String> groupToMerge = chainGroups.get(toMerge);
        groupL.addAll(groupToMerge);
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
  private static List<Set<String>> initializeChainGroups(final CifModel model) {
    final List<Set<String>> chainGroups = new ArrayList<>();
    for (final PdbChain chain : model.getChains()) {
      chainGroups.add(new HashSet<>(Collections.singleton(chain.getIdentifier())));
    }
    return chainGroups;
  }

  /**
   * Basing on the information in the mmCIF file itself, analyze which chains are in contact. Return
   * a mapping of chain name to a set of its contacts.
   *
   * @param model A parsed mmCIF model.
   * @return A map, where chain name is a key and set of this chain's contacts is the value.
   */
  private static Map<String, Set<String>> initializeChainContactMap(final CifModel model) {
    final Map<String, Set<String>> chainContacts = new HashMap<>();

    for (final QuantifiedBasePair quantifiedBasePair : model.getBasePairs()) {
      final BasePair basePair = quantifiedBasePair.getBasePair();
      final String left = basePair.getLeft().getChainIdentifier();
      final String right = basePair.getRight().getChainIdentifier();

      if (!chainContacts.containsKey(left)) {
        chainContacts.put(left, new HashSet<>());
      }
      chainContacts.get(left).add(right);

      if (!chainContacts.containsKey(right)) {
        chainContacts.put(right, new HashSet<>());
      }
      chainContacts.get(right).add(left);
    }

    return chainContacts;
  }

  private static void writeModel(
      final PdbModel rnaModel,
      final Collection<String> allowedChains,
      final BidiMap<String, String> chainMap,
      final StringBuilder pdbBuilder) {
    pdbBuilder.append("MODEL ").append(rnaModel.getModelNumber()).append(System.lineSeparator());

    int serialNumber = 1;

    for (final PdbChain chain : rnaModel.getChains()) {
      if (allowedChains.contains(chain.getIdentifier())) {
        for (final PdbResidue residue : chain.getResidues()) {
          for (PdbAtomLine atom : residue.getAtoms()) {
            final String chainIdentifier =
                CifConverter.mapChain(chainMap, atom.getChainIdentifier());
            atom = atom.replaceSerialNumber(serialNumber);
            atom = atom.replaceChainIdentifier(chainIdentifier);
            serialNumber =
                (serialNumber < CifConverter.MAX_ATOM_SERIAL_NUMBER) ? (serialNumber + 1) : 1;
            pdbBuilder.append(atom).append(System.lineSeparator());
          }
        }

        // pdbBuilder.append("TER                        ");
        // pdbBuilder.append(System.lineSeparator());
      }
    }

    pdbBuilder.append("ENDMDL");
    pdbBuilder.append(System.lineSeparator());
  }

  private static void writeHeader(
      final PdbModel firstModel,
      final BidiMap<String, String> chainMap,
      final StringBuilder pdbBuilder) {
    pdbBuilder.append(firstModel.getHeaderLine()).append(System.lineSeparator());

    final PdbExpdtaLine experimentalDataLine = firstModel.getExperimentalDataLine();
    if (experimentalDataLine.isValid()) {
      pdbBuilder.append(experimentalDataLine).append(System.lineSeparator());
    }

    pdbBuilder.append(PdbRemark2Line.PROLOGUE).append(System.lineSeparator());
    pdbBuilder.append(firstModel.getResolutionLine()).append(System.lineSeparator());

    final List<PdbRemark465Line> missingResidues = firstModel.getMissingResidues();
    if (!missingResidues.isEmpty()) {
      pdbBuilder.append(PdbRemark465Line.PROLOGUE).append(System.lineSeparator());

      for (PdbRemark465Line missingResidue : missingResidues) {
        String chainIdentifier = missingResidue.getChainIdentifier();
        final MoleculeType moleculeType = CifConverter.getChainType(firstModel, chainIdentifier);
        if (moleculeType == MoleculeType.RNA) {
          chainIdentifier = CifConverter.mapChain(chainMap, chainIdentifier);
          missingResidue = missingResidue.replaceChainIdentifier(chainIdentifier);
          pdbBuilder.append(missingResidue).append(System.lineSeparator());
        }
      }
    }

    for (final PdbModresLine modifiedResidue : firstModel.getModifiedResidues()) {
      pdbBuilder.append(modifiedResidue).append(System.lineSeparator());
    }
  }

  /**
   * Return type of the named chain.
   *
   * @param firstModel A PDB/mmCIF model.
   * @param chainIdentifier Name of the chain to check.
   * @return A {@link MoleculeType} instance representing chain's type or {@link
   *     MoleculeType#UNKNOWN} if the chain is not present.
   */
  private static MoleculeType getChainType(
      final PdbModel firstModel, final String chainIdentifier) {
    for (final PdbChain chain : firstModel.getChains()) {
      if (Objects.equals(chain.getIdentifier(), chainIdentifier)) {
        return chain.getMoleculeType();
      }
    }
    return MoleculeType.UNKNOWN;
  }

  /**
   * For a chain identifier in mmCIF, return its single-letter mapping from PDB. If the mmCIF
   * identifier is encountered for the first time, add a new mapping.
   *
   * @param chainMap A map where key is the mmCIF name and value is the PDB name.
   * @param chainIdentifier The mmCIF identifier.
   * @return The PDB identifier.
   */
  private static String mapChain(
      final BidiMap<String, String> chainMap, final String chainIdentifier) {
    if (!chainMap.containsKey(chainIdentifier)) {
      chainMap.put(chainIdentifier, CifConverter.PRINTABLE_CHARS.get(chainMap.size()));
    }
    return chainMap.get(chainIdentifier);
  }

  private CifConverter() {
    super();
  }
}
