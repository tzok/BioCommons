package pl.poznan.put.structure.formats;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.immutables.value.Value;
import pl.poznan.put.structure.pseudoknots.PseudoknotFinder;
import pl.poznan.put.structure.pseudoknots.elimination.ImmutableMinGain;

/**
 * A default converter from BPSEQ to dot-bracket which iteratively (1) finds non-pseudoknots and
 * assigns the current lowest level, then (2) increases level and (3) treats pseudoknots as the next
 * input to (1) until there are base pairs without level assigned.
 */
@Value.Immutable(singleton = true)
@JsonSerialize(as = ImmutableDefaultConverter.class)
@JsonDeserialize(as = ImmutableDefaultConverter.class)
public abstract class DefaultConverter implements Converter {
  private static final char[] BRACKETS_OPENING = "([{<ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
  private static final char[] BRACKETS_CLOSING = ")]}>abcdefghijklmnopqrstuvwxyz".toCharArray();

  private static boolean isProcessingNeeded(final Iterable<State> states) {
    for (final State state : states) {
      if (!state.isFinal()) {
        return true;
      }
    }
    return false;
  }

  private static String traceback(final State state) {
    final char[] structure = new char[state.size()];
    Arrays.fill(structure, '.');
    Optional<State> current = state.parent();

    while (current.isPresent()) {
      for (final BpSeq.Entry pairs : current.get().bpSeq().paired()) {
        final int i = pairs.index();
        final int j = pairs.pair();

        if (structure[i - 1] == '.') {
          structure[i - 1] = DefaultConverter.BRACKETS_OPENING[current.get().level()];
          structure[j - 1] = DefaultConverter.BRACKETS_CLOSING[current.get().level()];
        }
      }

      current = current.get().parent();
    }

    return new String(structure);
  }

  /**
   * @return The finder of pseudoknots ({@link
   *     pl.poznan.put.structure.pseudoknots.elimination.MinGain} by default).
   */
  @Value.Default
  public PseudoknotFinder pseudoknotFinder() {
    return ImmutableMinGain.of();
  }

  /**
   * @return The number of solutions to return (1 by default).
   */
  @Value.Default
  public int maxSolutions() {
    return 1;
  }

  /**
   * Converts the secondary structure in BPSEQ format to dot-bracket. Works level-by-level, see
   * class description.
   *
   * @param bpSeq The data in BPSEQ format.
   * @return The converted dot-bracket.
   */
  @Override
  public final DotBracket convert(final BpSeq bpSeq) {
    List<State> states = new ArrayList<>();
    states.add(ImmutableState.of(Optional.empty(), bpSeq, 0));

    while (DefaultConverter.isProcessingNeeded(states)) {
      states = processStates(states);
    }

    Collections.sort(states);
    final String structure = DefaultConverter.traceback(states.get(0));
    return ImmutableDefaultDotBracket.of(bpSeq.sequence(), structure);
  }

  private List<State> processStates(final Collection<State> states) {
    final List<State> nextStates = new ArrayList<>(states.size());
    for (final State state : states) {
      for (final BpSeq bpSeq : pseudoknotFinder().findPseudoknots(state.bpSeq())) {
        final State nextState = ImmutableState.of(Optional.of(state), bpSeq, state.level() + 1);
        nextStates.add(nextState);

        if (nextStates.size() > maxSolutions()) {
          return nextStates;
        }
      }
    }
    return nextStates;
  }

  @Value.Immutable
  @JsonSerialize(as = ImmutableState.class)
  @JsonDeserialize(as = ImmutableState.class)
  abstract static class State implements Comparable<State> {
    @Value.Parameter(order = 1)
    public abstract Optional<State> parent();

    @Value.Parameter(order = 2)
    public abstract BpSeq bpSeq();

    @Value.Parameter(order = 3)
    public abstract int level();

    @Value.Lazy
    public int score() {
      return bpSeq().paired().size();
    }

    @Override
    public final int compareTo(final State t) {
      return new CompareToBuilder().append(level(), t.level()).append(score(), t.score()).build();
    }

    private boolean isFinal() {
      return score() == 0;
    }

    private int size() {
      return bpSeq().size();
    }
  }
}
