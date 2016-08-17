package pl.poznan.put.rna;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.analysis.ResidueComponent;
import pl.poznan.put.pdb.analysis.ResidueInformationProvider;
import pl.poznan.put.rna.torsion.Alpha;
import pl.poznan.put.rna.torsion.Beta;
import pl.poznan.put.rna.torsion.Delta;
import pl.poznan.put.rna.torsion.Epsilon;
import pl.poznan.put.rna.torsion.Eta;
import pl.poznan.put.rna.torsion.EtaPrim;
import pl.poznan.put.rna.torsion.Gamma;
import pl.poznan.put.rna.torsion.Nu0;
import pl.poznan.put.rna.torsion.Nu1;
import pl.poznan.put.rna.torsion.Nu2;
import pl.poznan.put.rna.torsion.Nu3;
import pl.poznan.put.rna.torsion.Nu4;
import pl.poznan.put.rna.torsion.PseudophasePuckerType;
import pl.poznan.put.rna.torsion.Theta;
import pl.poznan.put.rna.torsion.ThetaPrim;
import pl.poznan.put.rna.torsion.Zeta;
import pl.poznan.put.torsion.TorsionAngleType;
import pl.poznan.put.types.Quadruplet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class Base extends NucleicAcidResidueComponent
        implements ResidueInformationProvider {
    private static final Base INVALID =
            new Base(Collections.<AtomName>emptyList(), "UNK", 'X', "UNK") {
                private final Quadruplet<AtomName> chiAtoms =
                        new Quadruplet<>(AtomName.UNKNOWN, AtomName.UNKNOWN,
                                         AtomName.UNKNOWN, AtomName.UNKNOWN);

                @Override
                public List<TorsionAngleType> getTorsionAngleTypes() {
                    return Collections.emptyList();
                }

                @Override
                public Quadruplet<AtomName> getChiAtoms() {
                    return chiAtoms;
                }

                @Override
                public Sugar getDefaultSugarInstance() {
                    return Sugar.invalidInstance();
                }
            };
    protected final List<TorsionAngleType> torsionAngleTypes =
            new ArrayList<>();
    private final String longName;
    private final char oneLetterName;
    private final List<String> pdbNames;
    protected Base(List<AtomName> atoms, String longName, char oneLetterName,
                   String... pdbNames) {
        super(RNAResidueComponentType.BASE, atoms);
        this.longName = longName;
        this.oneLetterName = oneLetterName;
        this.pdbNames = Arrays.asList(pdbNames);

        torsionAngleTypes
                .addAll(Arrays.asList(Alpha.getInstance(), Beta.getInstance(),
                                      Gamma.getInstance(), Delta.getInstance(),
                                      Epsilon.getInstance(), Zeta.getInstance(),
                                      Nu0.getInstance(), Nu1.getInstance(),
                                      Nu2.getInstance(), Nu3.getInstance(),
                                      Nu4.getInstance(), Eta.getInstance(),
                                      Theta.getInstance(),
                                      EtaPrim.getInstance(),
                                      ThetaPrim.getInstance(),
                                      PseudophasePuckerType.getInstance()));
    }

    public static Base invalidInstance() {
        return Base.INVALID;
    }

    @Override
    public List<ResidueComponent> getAllMoleculeComponents() {
        return Arrays.asList(new ResidueComponent[]{Phosphate.getInstance(),
                                                    getDefaultSugarInstance(),
                                                    this});
    }

    @Override
    public String getDescription() {
        return longName;
    }

    @Override
    public char getOneLetterName() {
        return oneLetterName;
    }

    @Override
    public String getDefaultPdbName() {
        assert !pdbNames.isEmpty();
        return pdbNames.get(0);
    }

    @Override
    public List<String> getPdbNames() {
        return pdbNames;
    }

    @Override
    public List<TorsionAngleType> getTorsionAngleTypes() {
        return torsionAngleTypes;
    }

    public abstract Sugar getDefaultSugarInstance();

    public abstract Quadruplet<AtomName> getChiAtoms();
}
