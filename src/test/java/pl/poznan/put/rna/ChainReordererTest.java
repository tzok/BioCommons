package pl.poznan.put.rna;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import pl.poznan.put.pdb.ImmutablePdbAtomLine;
import pl.poznan.put.pdb.ImmutablePdbNamedResidueIdentifier;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.analysis.*;
import pl.poznan.put.structure.CanonicalStructureExtractor;
import pl.poznan.put.structure.ClassifiedBasePair;
import pl.poznan.put.structure.ImmutableAnalyzedBasePair;
import pl.poznan.put.structure.ImmutableBasePair;
import pl.poznan.put.structure.formats.Converter;
import pl.poznan.put.structure.formats.ImmutableDefaultConverter;
import pl.poznan.put.utility.ResourcesHelper;

public class ChainReordererTest {
  private Converter converter;

  @Before
  public final void setUp() {
    converter = ImmutableDefaultConverter.of();
  }

  @Test
  public final void testReorderAtoms() throws IOException {
    final PdbModel originalModel =
        new PdbParser().parse(ResourcesHelper.loadResource("3OK4.pdb")).get(0);
    final List<String> originalChains =
        originalModel.chains().stream().map(PdbChain::identifier).collect(Collectors.toList());
    assertThat(
        originalChains,
        is(
            Arrays.asList(
                "A", "C", "E", "B", "D", "F", "G", "I", "K", "H", "J", "L", "M", "O", "Q", "N", "P",
                "R", "S", "U", "W", "T", "V", "X", "Y", "1", "3", "Z", "2", "4")));
    assertThat(
        converter.convert(CanonicalStructureExtractor.bpSeq(originalModel)).pseudoknotOrder(),
        is(3));

    final PdbModel reorderedModel = ChainReorderer.reorderAtoms(originalModel);
    final List<String> reorderedChains =
        reorderedModel.chains().stream().map(PdbChain::identifier).collect(Collectors.toList());
    assertThat(
        reorderedChains,
        is(
            Arrays.asList(
                "A", "B", "C", "D", "E", "F", "G", "H", "Q", "R", "I", "J", "K", "L", "M", "N", "O",
                "P", "S", "T", "U", "V", "W", "X", "Y", "Z", "1", "2", "3", "4")));

    final Collection<ClassifiedBasePair> originalBasePairs =
        CanonicalStructureExtractor.basePairs(originalModel);
    final Collection<ClassifiedBasePair> reorderedBasePairs =
        CanonicalStructureExtractor.basePairs(reorderedModel);
    assertThat(CollectionUtils.isEqualCollection(originalBasePairs, reorderedBasePairs), is(true));
    assertThat(
        converter.convert(CanonicalStructureExtractor.bpSeq(reorderedModel)).pseudoknotOrder(),
        is(1));
  }

  @Test
  public final void test1A73() throws IOException {
    final PdbModel originalModel =
        new CifParser()
            .parse(ResourcesHelper.loadResource("1a73-assembly-1.cif"))
            .get(0)
            .filteredNewInstance(MoleculeType.RNA);
    final List<String> originalChains =
        originalModel.chains().stream().map(PdbChain::identifier).collect(Collectors.toList());
    assertThat(originalChains, is(Arrays.asList("A", "B", "C", "D")));
    assertThat(
        converter.convert(CanonicalStructureExtractor.bpSeq(originalModel)).pseudoknotOrder(),
        is(0));

    final PdbModel reorderedModel = ChainReorderer.reorderAtoms(originalModel);
    final List<String> reorderedChains =
        reorderedModel.chains().stream().map(PdbChain::identifier).collect(Collectors.toList());
    assertThat(reorderedChains, is(Arrays.asList("A", "B", "C", "D")));
    assertThat(
        converter.convert(CanonicalStructureExtractor.bpSeq(reorderedModel)).pseudoknotOrder(),
        is(0));

    final Collection<ClassifiedBasePair> originalBasePairs =
        CanonicalStructureExtractor.basePairs(originalModel);
    final Collection<ClassifiedBasePair> reorderedBasePairs =
        CanonicalStructureExtractor.basePairs(reorderedModel);
    assertThat(CollectionUtils.isEqualCollection(originalBasePairs, reorderedBasePairs), is(true));
  }

  @Test
  public void testGraphComponentOfSizeOne() throws IOException {
    // create three chains: A, B, C
    final Iterable<PdbAtomLine> atoms =
        Stream.of("A", "B", "C")
            .map(
                chainName ->
                    ImmutablePdbAtomLine.of(
                        1,
                        "P",
                        Optional.empty(),
                        "A",
                        chainName,
                        1,
                        Optional.empty(),
                        0.0,
                        0.0,
                        0.0,
                        1.0,
                        0.0,
                        "P",
                        ""))
            .collect(Collectors.toList());
    final var model = ImmutableDefaultPdbModel.of(atoms);

    // create base pairs only between A and B chains
    Collection<? extends ClassifiedBasePair> basePairs =
        List.of(
            ImmutableAnalyzedBasePair.of(
                ImmutableBasePair.of(
                    ImmutablePdbNamedResidueIdentifier.of("A", 1, Optional.empty(), 'A'),
                    ImmutablePdbNamedResidueIdentifier.of("B", 1, Optional.empty(), 'A'))));

    // the line below used to throw MathIllegalArgumentException
    ChainReorderer.reorderAtoms(model, basePairs);
  }
}
