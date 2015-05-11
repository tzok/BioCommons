package pl.poznan.put.protein;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.common.ResidueComponent;
import pl.poznan.put.common.ResidueInformationProvider;
import pl.poznan.put.protein.torsion.Calpha;
import pl.poznan.put.protein.torsion.Omega;
import pl.poznan.put.protein.torsion.Phi;
import pl.poznan.put.protein.torsion.Psi;
import pl.poznan.put.torsion.type.TorsionAngleType;
import pl.poznan.put.types.Quadruplet;

public abstract class ProteinSidechain extends ResidueComponent implements ResidueInformationProvider {
    protected final Map<ProteinChiType, Quadruplet<AtomName>> chiAtoms = new HashMap<ProteinChiType, Quadruplet<AtomName>>();
    protected final List<TorsionAngleType> torsionAngleTypes = new ArrayList<TorsionAngleType>();

    private final String longName;
    private final char oneLetterName;
    private final List<String> pdbNames;

    public ProteinSidechain(List<AtomName> atoms, String longName,
            char oneLetterName, String... pdbNames) {
        super("sidechain", MoleculeType.PROTEIN, atoms);
        this.longName = longName;
        this.oneLetterName = oneLetterName;
        this.pdbNames = Arrays.asList(pdbNames);

        fillChiAtomsMap();

        torsionAngleTypes.add(Phi.getInstance());
        torsionAngleTypes.add(Psi.getInstance());
        torsionAngleTypes.add(Omega.getInstance());
        torsionAngleTypes.add(Calpha.getInstance());
    }

    @Override
    public List<ResidueComponent> getAllMoleculeComponents() {
        return Arrays.asList(new ResidueComponent[] { ProteinBackbone.getInstance(), this });
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
        assert pdbNames.size() > 0;
        return pdbNames.get(0);
    }

    @Override
    public List<String> getPdbNames() {
        return pdbNames;
    }

    protected abstract void fillChiAtomsMap();

    public boolean hasChiDefined(ProteinChiType chiType) {
        return chiAtoms.containsKey(chiType);
    }

    public Quadruplet<AtomName> getChiAtoms(ProteinChiType chiType) {
        if (!hasChiDefined(chiType)) {
            throw new IllegalArgumentException("Invalid " + chiType + " angle for " + getDescription());
        }

        return chiAtoms.get(chiType);
    }

    @Override
    public List<TorsionAngleType> getTorsionAngleTypes() {
        return torsionAngleTypes;
    }
}
