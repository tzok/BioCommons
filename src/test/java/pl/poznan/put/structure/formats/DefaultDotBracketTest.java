package pl.poznan.put.structure.formats;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import pl.poznan.put.utility.ResourcesHelper;

public class DefaultDotBracketTest {
  public static final String FROM_2Z74 =
      ">strand_A\n"
          + "aGCGCCuGGACUUAAAGCCAUUGCACU\n"
          + "..[[[[.[(((((((((((..------\n"
          + ">strand_B\n"
          + "CCGGCUUUAAGUUGACGAGGGCAGGGUUuAUCGAGACAUCGGCGGGUGCCCUGCGGUCUUCCUGCGACCGUUAGAGGACUGGuAAAACCACAGGCGACUGUGGCAUAGAGCAGUCCGGGCAGGAA\n"
          + "--)))))))))))..[[[...((((((...]]]......]]]]]...))))))[[[[[.((((((]]]]].....((((((......((((((....)))))).......))))))..)))))).";
  // @formatter:off
  private static final String WITH_WINDOWS_NEWLINE = ">strand_A\r\n" + "ACAAGU\r\n" + "((..))";
  private static final String WITH_ISOLATED_BASE_PAIR_AT_ENDS =
      ">strand_A\nGUGGAAGUUCCUC\n(.(((...))).)";
  private static final String WITH_ISOLATED_BASE_PAIR_IN_THE_MIDDLE =
      ">strand_A\nGGCUGAGACCGCC\n(((...(.).)))";
  private static final String WITH_ISOLATED_BASE_PAIR_PSEUDOKNOT =
      ">strand_A\nGUCCCUGACGAG\n(((.[.))).].";
  private static final String WITH_ISOLATED_BASE_PAIR_MULTI_STRAND =
      ">strand_A\nGGUGUAGCC\n(((.[.)))\n>strand_B\nCCCGACGGG\n(((.].)))";

  private static final String BPSEQ =
      "1 C 11\n"
          + "2 C 9\n"
          + "3 C 0\n"
          + "4 C 13\n"
          + "5 C 10\n"
          + "6 C 0\n"
          + "7 C 12\n"
          + "8 C 0\n"
          + "9 C 2\n"
          + "10 C 5\n"
          + "11 C 1\n"
          + "12 C 7\n"
          + "13 C 4";
  private static final String DOTBRACKET = ">strand_1\n" + "CCCCCCCCCCCCC\n" + "((.[[.{.)])}]";
  // @formatter:on

  @Test
  public final void from2Z74() {
    final DefaultDotBracket dotBracket =
        DefaultDotBracket.fromString(DefaultDotBracketTest.FROM_2Z74);
    assertThat(dotBracket.strands().size(), is(2));
  }

  @Test
  public final void fromBpSeq() {
    final Converter converter = ImmutableDefaultConverter.of();
    final BpSeq bpSeq = BpSeq.fromString(DefaultDotBracketTest.BPSEQ);
    final DotBracket dotBracketFromBpSeq = converter.convert(bpSeq);
    final DefaultDotBracket dotBracketFromString =
        DefaultDotBracket.fromString(DefaultDotBracketTest.DOTBRACKET);
    assertThat(dotBracketFromBpSeq, is(dotBracketFromString));
  }

  @Test
  public final void fromBpSeq1EHZ() throws Exception {
    final String bpseq1EHZ = ResourcesHelper.loadResource("1EHZ-2D-bpseq.txt");
    final String dotBracket1EHZ = ResourcesHelper.loadResource("1EHZ-2D-dotbracket.txt");

    final Converter converter = ImmutableDefaultConverter.of();
    final BpSeq bpSeq = BpSeq.fromString(bpseq1EHZ);
    final DotBracket dotBracketFromBpSeq = converter.convert(bpSeq);
    final DefaultDotBracket dotBracketFromString = DefaultDotBracket.fromString(dotBracket1EHZ);
    assertThat(dotBracketFromBpSeq, is(dotBracketFromString));
  }

  @Test
  public final void testWithWindowsNewline() {
    final DefaultDotBracket dotBracket =
        DefaultDotBracket.fromString(DefaultDotBracketTest.WITH_WINDOWS_NEWLINE);
    assertThat(dotBracket.sequence(), is("ACAAGU"));
    assertThat(dotBracket.structure(), is("((..))"));
  }

  @Test
  public final void testWithoutIsolatedBasePaisAtEnds() {
    final DefaultDotBracket dotBracket =
        DefaultDotBracket.fromString(DefaultDotBracketTest.WITH_ISOLATED_BASE_PAIR_AT_ENDS);
    assertThat(
        DefaultDotBracket.copyWithoutIsolatedBasePairs(dotBracket).structure(),
        is("..(((...))).."));
  }

  @Test
  public final void testWithoutIsolatedBasePaisInTheMiddle() {
    final DefaultDotBracket dotBracket =
        DefaultDotBracket.fromString(DefaultDotBracketTest.WITH_ISOLATED_BASE_PAIR_IN_THE_MIDDLE);
    assertThat(
        DefaultDotBracket.copyWithoutIsolatedBasePairs(dotBracket).structure(),
        is("(((.......)))"));
  }

  @Test
  public final void testWithoutIsolatedBasePaisPseudoknot() {
    final DefaultDotBracket dotBracket =
        DefaultDotBracket.fromString(DefaultDotBracketTest.WITH_ISOLATED_BASE_PAIR_PSEUDOKNOT);
    assertThat(
        DefaultDotBracket.copyWithoutIsolatedBasePairs(dotBracket).structure(), is("(((...)))..."));
  }

  @Test
  public final void testWithoutIsolatedBasePaisMultiStrand() {
    final DefaultDotBracket dotBracket =
        DefaultDotBracket.fromString(DefaultDotBracketTest.WITH_ISOLATED_BASE_PAIR_MULTI_STRAND);
    final DotBracket dotBracketAltered = DefaultDotBracket.copyWithoutIsolatedBasePairs(dotBracket);
    assertThat(dotBracketAltered.strands().size(), is(2));
    assertThat(dotBracketAltered.strands().get(0).structure(), is("(((...)))"));
    assertThat(dotBracketAltered.strands().get(1).structure(), is("(((...)))"));
  }
}
