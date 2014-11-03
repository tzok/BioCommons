package pl.poznan.put.structure;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.apache.commons.collections4.CollectionUtils;
import org.biojava.bio.structure.Group;

import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.interfaces.Exportable;
import pl.poznan.put.interfaces.Tabular;
import pl.poznan.put.torsion.AngleValue;
import pl.poznan.put.torsion.ChiTorsionAngle;
import pl.poznan.put.torsion.ChiTorsionAngleType;
import pl.poznan.put.torsion.TorsionAngle;
import pl.poznan.put.utility.AngleFormat;
import pl.poznan.put.utility.TabularExporter;

public class StructureSelection implements Exportable, Tabular {
    private final String name;
    private final List<Group> residues;

    public StructureSelection(String name, List<Group> residues) {
        super();
        this.name = name;
        this.residues = residues;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return residues.size();
    }

    public CompactFragment[] getCompactFragments() {
        List<CompactFragment> result = new ArrayList<CompactFragment>();

        if (residues.size() == 0) {
            return new CompactFragment[0];
        }

        Group first = residues.get(0);
        CompactFragment current = new CompactFragment(this, MoleculeType.detect(first));
        current.addGroup(first);

        for (int i = 0; i < residues.size() - 1; i++) {
            Group r1 = residues.get(i);
            Group r2 = residues.get(i + 1);
            MoleculeType c1 = MoleculeType.detect(r1);
            MoleculeType c2 = MoleculeType.detect(r2);

            if (c1 == c2 && c1.areConnected(r1, r2)) {
                current.addGroup(r2);
            } else {
                result.add(current);
                current = new CompactFragment(this, c2);
                current.addGroup(r2);
            }
        }

        result.add(current);
        return result.toArray(new CompactFragment[result.size()]);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (name == null ? 0 : name.hashCode());
        result = prime * result + (residues == null ? 0 : residues.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        StructureSelection other = (StructureSelection) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (residues == null) {
            if (other.residues != null) {
                return false;
            }
        } else if (!CollectionUtils.isEqualCollection(residues, other.residues)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        Residue first = Residue.fromGroup(residues.get(0));
        Residue last = Residue.fromGroup(residues.get(residues.size() - 1));
        return first + " - " + last + " (count: " + residues.size() + ")";
    }

    @Override
    public void export(File file) throws IOException {
        TabularExporter.export(asExportableTableModel(), file);
    }

    @Override
    public File suggestName() {
        return new File(name + ".csv");
    }

    @Override
    public TableModel asExportableTableModel() {
        return asTableModel(false);
    }

    @Override
    public TableModel asDisplayableTableModel() {
        return asTableModel(true);
    }

    private TableModel asTableModel(boolean isDisplayable) {
        Set<TorsionAngle> allAngles = new LinkedHashSet<TorsionAngle>();
        Set<String> columns = new LinkedHashSet<String>();
        columns.add("Residue");

        int rowCount = 0;

        for (CompactFragment fragment : getCompactFragments()) {
            FragmentAngles fragmentAngles = fragment.getFragmentAngles();

            for (ResidueAngles angles : fragmentAngles) {
                for (AngleValue angleValue : angles) {
                    TorsionAngle angle = angleValue.getAngle();

                    if (angle instanceof ChiTorsionAngle) {
                        allAngles.add(ChiTorsionAngleType.CHI);
                    } else {
                        allAngles.add(angle);
                    }

                    columns.add(isDisplayable ? angle.getLongDisplayName() : angle.getExportName());
                }
            }

            rowCount += fragmentAngles.getSize();
        }

        String[][] data = new String[rowCount][];
        int i = 0;

        for (CompactFragment fragment : getCompactFragments()) {
            for (ResidueAngles angles : fragment.getFragmentAngles()) {
                List<String> row = new ArrayList<String>();
                row.add(Residue.fromGroup(angles.getGroup()).toString());

                for (TorsionAngle angle : allAngles) {
                    AngleValue angleValue = angles.getAngleValue(angle);
                    double value = angleValue.getValue();
                    row.add(isDisplayable ? AngleFormat.formatDisplayLong(value) : AngleFormat.formatExport(value));
                }

                data[i] = row.toArray(new String[row.size()]);
                i++;
            }
        }

        return new DefaultTableModel(data, columns.toArray(new String[columns.size()]));
    }
}
