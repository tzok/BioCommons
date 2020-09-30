package pl.poznan.put.pdb.analysis;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.bidimap.TreeBidiMap;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.poznan.put.pdb.ImmutablePdbAtomLine;
import pl.poznan.put.pdb.ImmutablePdbRemark465Line;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbModresLine;
import pl.poznan.put.pdb.PdbRemark2Line;
import pl.poznan.put.pdb.PdbRemark465Line;
import pl.poznan.put.structure.BasePair;
import pl.poznan.put.structure.QuantifiedBasePair;

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
import java.util.stream.Collectors;

/**
 * A converter from mmCIF to one or more PDB files. It takes care of formats' mismatches (e.g.
 * multi-character chain names in mmCIF vs single-character in PDB).
 */
public final class CifConverter {
  private static final Logger LOGGER = LoggerFactory.getLogger(CifConverter.class);

  private static final int MAX_RESIDUE_NUMBER = 9999;
  private static final int MAX_ATOM_SERIAL_NUMBER = 99999;

  // PRINTABLE_CHARS is a set of chain names that we allow
  private static final List<String> PRINTABLE_CHARS =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
          .chars()
          .mapToObj(i -> (char) i)
          .map(String::valueOf)
          .collect(Collectors.toList());

  private CifConverter() {
    super();
  }

  /**
   * Parses a file in mmCIF format and convert it into a container of multiple PDB files.
   *
   * @param cifFile Path to mmCIF file.
   * @return A container of (possibly) multiple PDB files with mapped chain names.
   * @throws IOException When reading of mmCIF file or writing to output files fails.
   */
  public static ModelContainer convert(final File cifFile) throws IOException {
    final String cifContents = FileUtils.readFileToString(cifFile, Charset.defaultCharset());
    final List<CifModel> models = CifParser.parse(cifContents);
    return CifConverter.convert(cifFile, models);
  }

  /**
   * Converts a parsed mmCIF model into a set of PDB files with mapped chain names.
   *
   * @param model A parse mmCIF model.
   * @return A container of (possibly) multiple PDB files with mapped chain names.
   * @throws IOException When writing to output files fails.
   */
  public static ModelContainer convert(final DefaultCifModel model) throws IOException {
    final File cifFile = File.createTempFile("cif2pdb", ".cif");
    FileUtils.write(cifFile, model.toCif(), Charset.defaultCharset());

    if (!CifConverter.isConversionPossible(model)) {
      return CifContainer.emptyInstance(cifFile);
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

    return ImmutableCifContainer.of(cifFile, fileChainMap);
  }

  private static ModelContainer convert(final File cifFile, final List<CifModel> models)
      throws IOException {
    final List<CifModel> rnaModels = new ArrayList<>();

    for (final CifModel model : models) {
      if (model.containsAny(MoleculeType.RNA)) {
        final CifModel rnaModel = (CifModel) model.filteredNewInstance(MoleculeType.RNA);
        rnaModels.add(rnaModel);
      }
    }

    if (rnaModels.isEmpty()) {
      CifConverter.LOGGER.info("Neither model contain any RNA chain");
      return CifContainer.emptyInstance(cifFile);
    }

    for (final PdbModel model : rnaModels) {
      if (!CifConverter.isConversionPossible(model)) {
        return CifContainer.emptyInstance(cifFile);
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

    return ImmutableCifContainer.of(cifFile, fileChainMap);
  }

  private static boolean isConversionPossible(final PdbModel model) {
    for (final PdbChain chain : model.chains()) {
      for (final PdbResidue residue : chain.residues()) {
        if (residue.residueNumber() > CifConverter.MAX_RESIDUE_NUMBER) {
          CifConverter.LOGGER.error(
              "Cannot continue. Chain {} has residue of index > 9999", chain.identifier());
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Merges chains which are in contact to form groups.
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

        if (groupL.stream()
            .filter(chainContacts::containsKey)
            .map(chainContacts::get)
            .anyMatch(contactsL -> CollectionUtils.containsAny(contactsL, groupR))) {
          toMerge = j;
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
  private static List<Set<String>> initializeChainGroups(final PdbModel model) {
    return model.chains().stream()
        .map(chain -> new HashSet<>(Collections.singleton(chain.identifier())))
        .collect(Collectors.toList());
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

    for (final QuantifiedBasePair quantifiedBasePair : model.basePairs()) {
      final BasePair basePair = quantifiedBasePair.basePair();
      final String left = basePair.left().chainIdentifier();
      final String right = basePair.right().chainIdentifier();

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

  private static void writeHeader(
      final PdbModel firstModel,
      final BidiMap<String, String> chainMap,
      final StringBuilder pdbBuilder) {
    pdbBuilder.append(firstModel.header()).append(System.lineSeparator());
    if (!firstModel.experimentalData().experimentalTechniques().isEmpty()) {
      pdbBuilder.append(firstModel.experimentalData()).append(System.lineSeparator());
    }
    pdbBuilder.append(PdbRemark2Line.PROLOGUE).append(System.lineSeparator());
    pdbBuilder.append(firstModel.resolution()).append(System.lineSeparator());

    final List<PdbRemark465Line> missingResidues = firstModel.missingResidues();
    if (!missingResidues.isEmpty()) {
      pdbBuilder.append(PdbRemark465Line.PROLOGUE).append(System.lineSeparator());

      for (PdbRemark465Line missingResidue : missingResidues) {
        String chainIdentifier = missingResidue.chainIdentifier();
        final MoleculeType moleculeType = CifConverter.getChainType(firstModel, chainIdentifier);
        if (moleculeType == MoleculeType.RNA) {
          chainIdentifier = CifConverter.mapChain(chainMap, chainIdentifier);
          missingResidue =
              ImmutablePdbRemark465Line.copyOf(missingResidue).withChainIdentifier(chainIdentifier);
          pdbBuilder.append(missingResidue).append(System.lineSeparator());
        }
      }
    }

    for (final PdbModresLine modifiedResidue : firstModel.modifiedResidues()) {
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
    return firstModel.chains().stream()
        .filter(chain -> Objects.equals(chain.identifier(), chainIdentifier))
        .findFirst()
        .map(SingleTypedResidueCollection::moleculeType)
        .orElse(MoleculeType.UNKNOWN);
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

  private static void writeModel(
      final PdbModel rnaModel,
      final Collection<String> allowedChains,
      final BidiMap<String, String> chainMap,
      final StringBuilder pdbBuilder) {
    pdbBuilder.append("MODEL ").append(rnaModel.modelNumber()).append(System.lineSeparator());

    int serialNumber = 1;

    for (final PdbChain chain : rnaModel.chains()) {
      if (allowedChains.contains(chain.identifier())) {
        for (final PdbResidue residue : chain.residues()) {
          for (final PdbAtomLine atom : residue.atoms()) {
            final String chainIdentifier = CifConverter.mapChain(chainMap, atom.chainIdentifier());
            final ImmutablePdbAtomLine atomLine =
                ((ImmutablePdbAtomLine) atom)
                    .withSerialNumber(serialNumber)
                    .withChainIdentifier(chainIdentifier);
            serialNumber =
                (serialNumber < CifConverter.MAX_ATOM_SERIAL_NUMBER) ? (serialNumber + 1) : 1;
            pdbBuilder.append(atomLine).append(System.lineSeparator());
          }
        }
      }
    }

    pdbBuilder.append("ENDMDL");
    pdbBuilder.append(System.lineSeparator());
  }
}
