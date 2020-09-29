package pl.poznan.put.structure.formats;

import org.immutables.value.Value;
import pl.poznan.put.structure.pseudoknots.PseudoknotFinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class LevelByLevelConverter implements Converter {
  private static final char[] BRACKETS_OPENING = "([{<ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
  private static final char[] BRACKETS_CLOSING = ")]}>abcdefghijklmnopqrstuvwxyz".toCharArray();

  private final PseudoknotFinder pkRemover;
  private final int maxSolutions;

  /**
   * Construct an instance of converter from BPSEQ to dot-bracket.
   *
   * @param pkRemover An instance of algorithm to find which pairs are pseudoknots.
   * @param maxSolutions Maximum number of solutions to be considered in a single step of the
   *     algorithm.
   */
  public LevelByLevelConverter(final PseudoknotFinder pkRemover, final int maxSolutions) {
    super();
    this.pkRemover = pkRemover;
    this.maxSolutions = maxSolutions;
  }

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
          structure[i - 1] = LevelByLevelConverter.BRACKETS_OPENING[current.get().level()];
          structure[j - 1] = LevelByLevelConverter.BRACKETS_CLOSING[current.get().level()];
        }
      }

      current = current.get().parent();
    }

    return new String(structure);
  }

  @Override
  public final DefaultDotBracket convert(final BpSeq bpSeq) {
    List<State> states = new ArrayList<>();
    states.add(ImmutableState.of(Optional.empty(), bpSeq, 0));

    while (LevelByLevelConverter.isProcessingNeeded(states)) {
      states = processStates(states);
    }

    Collections.sort(states);
    final String structure = LevelByLevelConverter.traceback(states.get(0));
    return ImmutableDefaultDotBracket.of(bpSeq.sequence(), structure);
  }

  private List<State> processStates(final Collection<State> states) {
    final List<State> nextStates = new ArrayList<>(states.size());
    for (final State state : states) {
      for (final BpSeq bpSeq : pkRemover.findPseudoknots(state.bpSeq())) {
        final State nextState = ImmutableState.of(Optional.of(state), bpSeq, state.level() + 1);
        nextStates.add(nextState);

        if (nextStates.size() > maxSolutions) {
          return nextStates;
        }
      }
    }
    return nextStates;
  }

  @Value.Immutable
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
    public int compareTo(final State t) {
      if (level() < t.level()) {
        return -1;
      }
      if (level() > t.level()) {
        return 1;
      }
      if (score() < t.score()) {
        return -1;
      }
      if (score() > t.score()) {
        return 1;
      }
      if (level() != 0) {
        if (parent().isPresent() && t.parent().isPresent()) {
          return parent().get().compareTo(t.parent().get());
        }
      }
      return 0;
    }

    private boolean isFinal() {
      return score() == 0;
    }

    private int size() {
      return bpSeq().size();
    }
  }
}
