package pl.poznan.put;

import org.junit.Test;
import pl.poznan.put.structure.secondary.formats.BpSeq;
import pl.poznan.put.structure.secondary.formats.Converter;
import pl.poznan.put.structure.secondary.formats.DotBracket;
import pl.poznan.put.structure.secondary.formats.LevelByLevelConverter;
import pl.poznan.put.structure.secondary.pseudoknots.elimination.MinGain;
import pl.poznan.put.utility.ResourcesHelper;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DotBracketTest {
  public static final String FROM_2Z74 =
      ">strand_A\n"
          + "aGCGCCuGGACUUAAAGCCAUUGCACU\n"
          + "..[[[[.[(((((((((((..------\n"
          + ">strand_B\n"
          + "CCGGCUUUAAGUUGACGAGGGCAGGGUUuAUCGAGACAUCGGCGGGUGCCCUGCGGUCUUCCUGCGACCGUUAGAGGACUGGuAAAACCACAGGCGACUGUGGCAUAGAGCAGUCCGGGCAGGAA\n"
          + "--)))))))))))..[[[...((((((...]]]......]]]]]...))))))[[[[[.((((((]]]]].....((((((......((((((....)))))).......))))))..)))))).";
  // @formatter:off
  private static final String WITH_WINDOWS_NEWLINE = ">strand_A\r\n" + "ACAAGU\r\n" + "((..))";
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
    final DotBracket dotBracket = DotBracket.fromString(DotBracketTest.FROM_2Z74);
    assertThat(dotBracket.getStrands().size(), is(2));
  }

  @Test
  public final void fromBpSeq() {
    final Converter converter = new LevelByLevelConverter(new MinGain(), 1);
    final BpSeq bpSeq = BpSeq.fromString(DotBracketTest.BPSEQ);
    final DotBracket dotBracketFromBpSeq = converter.convert(bpSeq);
    final DotBracket dotBracketFromString = DotBracket.fromString(DotBracketTest.DOTBRACKET);
    assertThat(dotBracketFromBpSeq, is(dotBracketFromString));
  }

  @Test
  public final void fromBpSeq1EHZ() throws Exception {
    final String bpseq1EHZ = ResourcesHelper.loadResource("1EHZ-2D-bpseq.txt");
    final String dotBracket1EHZ = ResourcesHelper.loadResource("1EHZ-2D-dotbracket.txt");

    final Converter converter = new LevelByLevelConverter(new MinGain(), 1);
    final BpSeq bpSeq = BpSeq.fromString(bpseq1EHZ);
    final DotBracket dotBracketFromBpSeq = converter.convert(bpSeq);
    final DotBracket dotBracketFromString = DotBracket.fromString(dotBracket1EHZ);
    assertThat(dotBracketFromBpSeq, is(dotBracketFromString));
  }

  @Test
  public final void testWithWindowsNewline() {
    final DotBracket dotBracket = DotBracket.fromString(DotBracketTest.WITH_WINDOWS_NEWLINE);
    assertThat(dotBracket.getSequence(), is("ACAAGU"));
    assertThat(dotBracket.getStructure(), is("((..))"));
  }
}
