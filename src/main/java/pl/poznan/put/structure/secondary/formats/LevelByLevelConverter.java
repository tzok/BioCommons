package pl.poznan.put.structure.secondary.formats;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.poznan.put.structure.secondary.pseudoknots.PseudoknotFinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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

  @Override
  public final DotBracket convert(final BpSeq bpSeq) {
    List<State> states = new ArrayList<>();
    states.add(new State(null, bpSeq, 0));

    while (LevelByLevelConverter.isProcessingNeeded(states)) {
      states = processStates(states);
    }

    Collections.sort(states);
    final String structure = LevelByLevelConverter.traceback(states.get(0));
    return new DotBracket(bpSeq.getSequence(), structure);
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
    State current = state.getParent();

    while (current != null) {
      for (final BpSeq.Entry pairs : current.getBpSeq().getPaired()) {
        final int i = pairs.getIndex();
        final int j = pairs.getPair();

        if (structure[i - 1] == '.') {
          structure[i - 1] = LevelByLevelConverter.BRACKETS_OPENING[current.getLevel()];
          structure[j - 1] = LevelByLevelConverter.BRACKETS_CLOSING[current.getLevel()];
        }
      }

      current = current.getParent();
    }

    return new String(structure);
  }

  private List<State> processStates(final Collection<State> states) {
    final List<State> nextStates = new ArrayList<>(states.size());
    for (final State state : states) {
      for (final BpSeq bpSeq : pkRemover.findPseudoknots(state.getBpSeq())) {
        final State nextState = new State(state, bpSeq, state.getLevel() + 1);
        nextStates.add(nextState);

        if (nextStates.size() > maxSolutions) {
          return nextStates;
        }
      }
    }
    return nextStates;
  }

  @EqualsAndHashCode
  @Getter
  private static final class State implements Comparable<State> {
    private final State parent;
    private final BpSeq bpSeq;
    private final int level;
    private final int score;

    private State(final State parent, final BpSeq bpSeq, final int level) {
      super();
      this.parent = parent;
      this.bpSeq = bpSeq;
      this.level = level;
      score = bpSeq.getPaired().size();
    }

    private boolean isFinal() {
      return score == 0;
    }

    private int size() {
      return bpSeq.size();
    }

    @Override
    public int compareTo(final State t) {
      if (level < t.level) {
        return -1;
      }
      if (level > t.level) {
        return 1;
      }
      if (score < t.score) {
        return -1;
      }
      if (score > t.score) {
        return 1;
      }
      if (level != 0) {
        return parent.compareTo(t.parent);
      }
      return 0;
    }

    @Override
    public String toString() {
      return String.format("State{level=%d, score=%d}", level, score);
    }
  }
}
