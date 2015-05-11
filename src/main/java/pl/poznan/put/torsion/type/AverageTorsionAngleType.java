package pl.poznan.put.torsion.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.exception.InvalidCircularValueException;
import pl.poznan.put.circular.samples.AngleSample;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.torsion.TorsionAngleValue;

public class AverageTorsionAngleType extends TorsionAngleType implements MasterTorsionAngleType {
    private final String displayName;
    private final String exportName;
    private final List<MasterTorsionAngleType> consideredAngles;

    public AverageTorsionAngleType(MoleculeType moleculeType,
            MasterTorsionAngleType... masterTypes) {
        super(moleculeType);
        this.consideredAngles = Arrays.asList(masterTypes);
        this.displayName = AverageTorsionAngleType.toDisplayName(consideredAngles);
        this.exportName = AverageTorsionAngleType.toExportName(consideredAngles);
    }

    public AverageTorsionAngleType(MoleculeType moleculeType,
            List<MasterTorsionAngleType> consideredAngles) {
        super(moleculeType);
        this.consideredAngles = consideredAngles;
        this.displayName = AverageTorsionAngleType.toDisplayName(consideredAngles);
        this.exportName = AverageTorsionAngleType.toExportName(consideredAngles);
    }

    private static String toDisplayName(
            List<MasterTorsionAngleType> consideredAngles) {
        Set<String> angleNames = new LinkedHashSet<String>();
        for (MasterTorsionAngleType angleType : consideredAngles) {
            angleNames.add(angleType.getShortDisplayName());
        }

        StringBuilder builder = new StringBuilder("MCQ(");
        Iterator<String> iterator = angleNames.iterator();
        builder.append(iterator.next());

        while (iterator.hasNext()) {
            builder.append(", ");
            builder.append(iterator.next());
        }

        builder.append(')');
        return builder.toString();
    }

    private static String toExportName(
            List<MasterTorsionAngleType> consideredAngles) {
        Set<String> angleNames = new LinkedHashSet<String>();
        for (MasterTorsionAngleType angleType : consideredAngles) {
            angleNames.add(angleType.getExportName());
        }

        StringBuilder builder = new StringBuilder("MCQ_");
        Iterator<String> iterator = angleNames.iterator();
        builder.append(iterator.next());

        while (iterator.hasNext()) {
            builder.append("_");
            builder.append(iterator.next());
        }

        return builder.toString();
    }

    private AverageTorsionAngleType(MoleculeType moleculeType,
            List<MasterTorsionAngleType> consideredAngles, String displayName,
            String exportName) {
        super(moleculeType);
        this.consideredAngles = consideredAngles;
        this.displayName = displayName;
        this.exportName = exportName;
    }

    @Override
    public String getLongDisplayName() {
        return displayName;
    }

    @Override
    public String getShortDisplayName() {
        return displayName;
    }

    @Override
    public String getExportName() {
        return exportName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public List<MasterTorsionAngleType> getConsideredAngles() {
        return Collections.unmodifiableList(consideredAngles);
    }

    @Override
    public TorsionAngleValue calculate(List<PdbResidue> residues,
            int currentIndex) throws InvalidCircularValueException {
        PdbResidue residue = residues.get(currentIndex);
        List<Angle> angles = new ArrayList<Angle>();

        for (TorsionAngleType type : residue.getTorsionAngleTypes()) {
            for (MasterTorsionAngleType masterType : consideredAngles) {
                if (masterType.getAngleTypes().contains(type)) {
                    TorsionAngleValue angleValue = type.calculate(residues, currentIndex);
                    angles.add(angleValue.getValue());
                }
            }
        }

        AngleSample angleSample = new AngleSample(angles);
        return new TorsionAngleValue(this, angleSample.getMeanDirection());
    }

    public TorsionAngleValue calculate(Collection<TorsionAngleValue> values) {
        List<Angle> angles = new ArrayList<Angle>();

        for (MasterTorsionAngleType masterType : consideredAngles) {
            for (TorsionAngleValue angleValue : values) {
                if (masterType.getAngleTypes().contains(angleValue.getAngleType())) {
                    if (angleValue.isValid()) {
                        angles.add(angleValue.getValue());
                    }
                    break;
                }
            }
        }

        AngleSample angleSample = new AngleSample(angles);
        return new TorsionAngleValue(this, angleSample.getMeanDirection());
    }

    @Override
    public Collection<? extends TorsionAngleType> getAngleTypes() {
        return Collections.singleton(this);
    }
}