package pl.poznan.put.pdb.analysis;

import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbExpdtaLine;
import pl.poznan.put.pdb.PdbHeaderLine;
import pl.poznan.put.pdb.PdbModresLine;
import pl.poznan.put.pdb.PdbParsingException;
import pl.poznan.put.pdb.PdbRemark2Line;
import pl.poznan.put.pdb.PdbRemark465Line;
import pl.poznan.put.structure.secondary.QuantifiedBasePair;

import java.util.Collections;
import java.util.List;

public class CifModel extends PdbModel {
    private final List<QuantifiedBasePair> basePairs;

    public CifModel(PdbHeaderLine headerLine,
                    PdbExpdtaLine experimentalDataLine,
                    PdbRemark2Line resolutionLine, int modelNumber,
                    List<PdbAtomLine> atoms,
                    List<PdbModresLine> modifiedResidues,
                    List<PdbRemark465Line> missingResidues,
                    List<QuantifiedBasePair> basePairs)
            throws PdbParsingException {
        super(headerLine, experimentalDataLine, resolutionLine, modelNumber,
              atoms, modifiedResidues, missingResidues);
        this.basePairs = basePairs;
    }

    public List<QuantifiedBasePair> getBasePairs() {
        return Collections.unmodifiableList(basePairs);
    }

    @Override
    public CifModel filteredNewInstance(MoleculeType moleculeType)
            throws PdbParsingException {
        List<PdbAtomLine> filteredAtoms = filterAtoms(moleculeType);
        List<PdbRemark465Line> filteredMissing = filterMissing(moleculeType);
        return new CifModel(headerLine, experimentalDataLine, resolutionLine,
                            modelNumber, filteredAtoms, modifiedResidues,
                            filteredMissing, basePairs);
    }
}
