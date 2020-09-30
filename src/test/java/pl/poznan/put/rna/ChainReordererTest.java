package pl.poznan.put.rna;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import pl.poznan.put.pdb.analysis.PdbChain;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.pdb.analysis.PdbParser;
import pl.poznan.put.structure.CanonicalStructureExtractor;
import pl.poznan.put.structure.ClassifiedBasePair;
import pl.poznan.put.structure.formats.Converter;
import pl.poznan.put.structure.formats.ImmutableDefaultConverter;
import pl.poznan.put.structure.pseudoknots.elimination.MinGain;
import pl.poznan.put.utility.ResourcesHelper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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
}
