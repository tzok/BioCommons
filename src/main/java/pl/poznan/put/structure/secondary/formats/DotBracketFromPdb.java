package pl.poznan.put.structure.secondary.formats;

import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.pdb.analysis.PdbChain;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.pdb.analysis.ResidueCollection;
import pl.poznan.put.structure.secondary.DotBracketSymbol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DotBracketFromPdb extends DotBracket {
    private final Map<DotBracketSymbol, PdbResidueIdentifier> symbolToResidue =
            new HashMap<>();
    private final Map<PdbResidueIdentifier, DotBracketSymbol> residueToSymbol =
            new HashMap<>();

    public DotBracketFromPdb(final DotBracket dotBracket, final PdbModel model)
            throws InvalidStructureException {
        this(dotBracket.sequence, dotBracket.structure, model);
    }

    public DotBracketFromPdb(
            final String sequence, final String structure, final PdbModel model)
            throws

            InvalidStructureException {
        super(sequence,
              DotBracketFromPdb.updateMissingIndices(structure, model));

        mapSymbolsAndResidues(model);
        splitStrands(model);
    }

    private static String updateMissingIndices(
            final String structure, final ResidueCollection model) {
        List<PdbResidue> residues = model.getResidues();
        char[] dotBracket = structure.toCharArray();
        assert dotBracket.length == residues.size();

        for (int i = 0; i < dotBracket.length; i++) {
            if (residues.get(i).isMissing()) {
                dotBracket[i] = '-';
            }
        }

        return String.valueOf(dotBracket);
    }

    private void mapSymbolsAndResidues(final ResidueCollection model) {
        List<PdbResidue> residues = model.getResidues();
        assert residues.size() == symbols.size();

        for (int i = 0; i < residues.size(); i++) {
            DotBracketSymbol symbol = symbols.get(i);
            PdbResidue residue = residues.get(i);
            PdbResidueIdentifier residueIdentifier =
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
            strands.add(new Strand(this, String.valueOf(chain.getIdentifier()),
                                   start, end));
            start = end;
        }
    }

    public PdbResidueIdentifier getResidueIdentifier(
            final DotBracketSymbol symbol) {
        return symbolToResidue.get(symbol);
    }

    public DotBracketSymbol getSymbol(
            final PdbResidueIdentifier residueIdentifier) {
        return residueToSymbol.get(residueIdentifier);
    }

    @Override
    protected int getCtOriginalColumn(final DotBracketSymbol symbol) {
        return symbolToResidue.get(symbol).getResidueNumber();
    }
}
