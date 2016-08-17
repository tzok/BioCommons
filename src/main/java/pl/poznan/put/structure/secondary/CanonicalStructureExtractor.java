package pl.poznan.put.structure.secondary;

import java.util.ArrayList;
import java.util.List;

import pl.poznan.put.notation.LeontisWesthof;
import pl.poznan.put.notation.Saenger;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.pdb.analysis.ResidueCollection;
import pl.poznan.put.rna.RNAInteractionType;
import pl.poznan.put.structure.secondary.formats.BpSeq;
import pl.poznan.put.structure.secondary.formats.InvalidStructureException;

public class CanonicalStructureExtractor {
    /*
     * This is just a simple implementation. For a robust solution, see RNApdbee
     * http://rnapdbee.cs.put.poznan.pl
     */
    public static BpSeq getCanonicalSecondaryStructure(ResidueCollection residueCollection) throws

                                                                                            InvalidStructureException {
        List<PdbResidue> residues = residueCollection.getResidues();
        List<ClassifiedBasePair> basePairs = new ArrayList<>();

        for (int i = 0; i < residues.size(); i++) {
            PdbResidue left = residues.get(i);
            char leftName = Character.toUpperCase(left.getOneLetterName());

            for (int j = 0; j < residues.size(); j++) {
                if (Math.abs(i - j) <= 1) {
                    continue;
                }

                PdbResidue right = residues.get(j);
                char rightName = Character.toUpperCase(right.getOneLetterName());
                Saenger saenger;

                if (leftName == 'C' && rightName == 'G' && BasePair.isCanonicalCG(left, right)) {
                    saenger = Saenger.XIX;
                } else if (leftName == 'A' && rightName == 'U' && BasePair.isCanonicalAU(left, right)) {
                    saenger = Saenger.XX;
                } else if (leftName == 'G' && rightName == 'U' && BasePair.isCanonicalGU(left, right)) {
                    saenger = Saenger.XXVIII;
                } else {
                    continue;
                }

                BasePair basePair = new BasePair(left.getResidueIdentifier(), right.getResidueIdentifier());
                ClassifiedBasePair classifiedBasePair = new ClassifiedBasePair(basePair, RNAInteractionType.BASE_BASE, saenger, LeontisWesthof.CWW, HelixOrigin.UNKNOWN);

                if (CanonicalStructureExtractor.areBothBasesUnpaired(basePairs, left.getResidueIdentifier(), right.getResidueIdentifier())) {
                    basePairs.add(classifiedBasePair);
                }
            }
        }

        return BpSeq.fromResidueCollection(residueCollection, basePairs);
    }

    private static boolean areBothBasesUnpaired(List<ClassifiedBasePair> basePairs, PdbResidueIdentifier left, PdbResidueIdentifier right) {
        for (ClassifiedBasePair classifiedBasePair : basePairs) {
            BasePair basePair = classifiedBasePair.getBasePair();
            PdbResidueIdentifier bpLeft = basePair.getLeft();
            PdbResidueIdentifier bpRight = basePair.getRight();

            if (bpLeft.equals(left) || bpLeft.equals(right) || bpRight.equals(left) || bpRight.equals(right)) {
                return false;
            }
        }

        return true;
    }

    private CanonicalStructureExtractor() {
        // empty constructor
    }
}
