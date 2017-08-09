package pl.poznan.put.structure.secondary.formats;

import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.pdb.analysis.PdbChain;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.pdb.analysis.ResidueCollection;
import pl.poznan.put.structure.secondary.BasePair;
import pl.poznan.put.structure.secondary.ClassifiedBasePair;
import pl.poznan.put.structure.secondary.DotBracketSymbol;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class DotBracketFromPdb extends DotBracket {
    private static final long serialVersionUID = -4415694977869681897L;

    private final Map<DotBracketSymbol, PdbResidueIdentifier> symbolToResidue =
            new HashMap<>();
    private final Map<PdbResidueIdentifier, DotBracketSymbol> residueToSymbol =
            new HashMap<>();

    public DotBracketFromPdb(final DotBracket dotBracket, final PdbModel model,
                             final Iterable<ClassifiedBasePair> nonCanonical)
            throws InvalidStructureException {
        this(dotBracket.getSequence(), dotBracket.getStructure(), model);
        markRepresentedNonCanonicals(nonCanonical);
    }

    private void markRepresentedNonCanonicals(
            final Iterable<ClassifiedBasePair> nonCanonical) {
        final Collection<BasePair> representedSet = new HashSet<>();

        for (final DotBracketSymbol symbol : symbols) {
            if (symbol.isPairing()) {
                final PdbResidueIdentifier left = getResidueIdentifier(symbol);
                final PdbResidueIdentifier right =
                        getResidueIdentifier(symbol.getPair());
                representedSet.add(new BasePair(left, right));
            }
        }

        for (final ClassifiedBasePair cbp : nonCanonical) {
            final BasePair basePair = cbp.getBasePair();
            if (representedSet.contains(basePair)) {
                cbp.setRepresented(true);

                if (!cbp.isCanonical()) {
                    final DotBracketSymbol left = getSymbol(basePair.getLeft());
                    final DotBracketSymbol right =
                            getSymbol(basePair.getRight());
                    left.setNonCanonical(true);
                    right.setNonCanonical(true);
                }
            }
        }
    }

    public DotBracketFromPdb(final String sequence, final String structure,
                             final PdbModel model)
            throws InvalidStructureException {
        super(sequence,
              DotBracketFromPdb.updateMissingIndices(structure, model));

        mapSymbolsAndResidues(model);
        splitStrands(model);
    }

    private static String updateMissingIndices(final String structure,
                                               final ResidueCollection model) {
        final List<PdbResidue> residues = model.getResidues();
        final char[] dotBracket = structure.toCharArray();
        assert dotBracket.length == residues.size();

        for (int i = 0; i < dotBracket.length; i++) {
            if (residues.get(i).isMissing()) {
                dotBracket[i] = '-';
            }
        }

        return String.valueOf(dotBracket);
    }

    private void mapSymbolsAndResidues(final ResidueCollection model) {
        final List<PdbResidue> residues = model.getResidues();
        assert residues.size() == symbols.size();

        for (int i = 0; i < residues.size(); i++) {
            final DotBracketSymbol symbol = symbols.get(i);
            final PdbResidue residue = residues.get(i);
            final PdbResidueIdentifier residueIdentifier =
                    residue.getResidueIdentifier();
            symbolToResidue.put(symbol, residueIdentifier);
            residueToSymbol.put(residueIdentifier, symbol);
        }
    }

    private void splitStrands(final PdbModel model) {
        strands.clear();
        int start = 0;
        int end = 0;

        for (final PdbChain chain : model.getChains()) {
            end += chain.getResidues().size();
            strands.add(new Strand(this, String.format("strand_%s",
                                                       chain.getIdentifier()),
                                   start, end));
            start = end;
        }
    }

    public final PdbResidueIdentifier getResidueIdentifier(
            final DotBracketSymbol symbol) {
        return symbolToResidue.get(symbol);
    }

    public final DotBracketSymbol getSymbol(
            final PdbResidueIdentifier residueIdentifier) {
        return residueToSymbol.get(residueIdentifier);
    }

    @Override
    protected final int getCtOriginalColumn(final DotBracketSymbol symbol) {
        return symbolToResidue.get(symbol).getResidueNumber();
    }
}
