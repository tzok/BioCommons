package pl.poznan.put.structure.formats;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.TreeBidiMap;
import pl.poznan.put.structure.DotBracketSymbol;
import pl.poznan.put.structure.ModifiableDotBracketSymbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

abstract class AbstractDotBracket implements DotBracket {
  @Override
  public List<DotBracketSymbol> symbols() {
    final List<DotBracketSymbol> symbols = new ArrayList<>();
    final char[] seq = sequence().toCharArray();
    final char[] str = structure().toCharArray();

    ModifiableDotBracketSymbol current = ModifiableDotBracketSymbol.create(seq[0], str[0], 0);
    for (int i = 1; i < seq.length; i++) {
      final ModifiableDotBracketSymbol next = ModifiableDotBracketSymbol.create(seq[i], str[i], i);
      current.setNext(next);
      next.setPrevious(current);
      symbols.add(current);
      current = next;
    }
    symbols.add(current);

    analyzePairing(symbols);

    return symbols;
  }

  protected final List<List<Strand>> candidatesToCombine() {
    final List<List<Strand>> result = new ArrayList<>();
    final List<Strand> toCombine = new ArrayList<>();
    int level = 0;

    for (final Strand strand : strands()) {
      toCombine.add(strand);

      for (final DotBracketSymbol symbol : strand.symbols()) {
        level += symbol.isOpening() ? 1 : 0;
        level -= symbol.isClosing() ? 1 : 0;
      }

      if (level == 0) {
        result.add(new ArrayList<>(toCombine));
        toCombine.clear();
      }
    }

    return result;
  }

  private void analyzePairing(final Iterable<DotBracketSymbol> symbols) {
    final BidiMap<Character, Character> parentheses = new TreeBidiMap<>();
    parentheses.put('(', ')');
    parentheses.put('[', ']');
    parentheses.put('{', '}');
    parentheses.put('<', '>');

    for (final char c : "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()) {
      parentheses.put(c, Character.toLowerCase(c));
    }

    final Map<Character, Stack<DotBracketSymbol>> parenthesesStacks = new HashMap<>();
    for (final char c : parentheses.keySet()) {
      parenthesesStacks.put(c, new Stack<>());
    }

    for (final DotBracketSymbol symbol : symbols) {
      assert symbol instanceof ModifiableDotBracketSymbol;

      final char str = symbol.structure();

      // catch dot '.'
      if ((str == '.') || (str == '-')) {
        ((ModifiableDotBracketSymbol) symbol).setPair(Optional.empty());
        continue;
      }

      // catch opening '(', '[', etc.
      if (parentheses.containsKey(str)) {
        final Stack<DotBracketSymbol> stack = parenthesesStacks.get(str);
        stack.push(symbol);
        continue;
      }

      // catch closing ')', ']', etc.
      if (parentheses.containsValue(str)) {
        final char opening = parentheses.getKey(str);
        final Stack<DotBracketSymbol> stack = parenthesesStacks.get(opening);

        if (stack.empty()) {
          throw new IllegalArgumentException(
              "Invalid dot-bracket input:\n" + sequence() + '\n' + structure());
        }

        final DotBracketSymbol pair = stack.pop();
        assert pair instanceof ModifiableDotBracketSymbol;

        ((ModifiableDotBracketSymbol) symbol).setPair(pair);
        ((ModifiableDotBracketSymbol) pair).setPair(symbol);
      }
    }
  }
}
