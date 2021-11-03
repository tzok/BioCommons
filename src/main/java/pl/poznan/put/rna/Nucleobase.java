package pl.poznan.put.rna;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.notation.NucleobaseEdge;
import pl.poznan.put.pdb.ImmutablePdbAtomLine;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.pdb.analysis.ResidueInformationProvider;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** A nucleobase (adenine, cytosine, guanine, uracil or thymine). */
public interface Nucleobase extends NucleicAcidResidueComponent, ResidueInformationProvider {
  String standardReferenceFrameString();

  Pair<AtomName, AtomName> edgeVectorAtoms(final NucleobaseEdge edge);

  default Map<AtomName, PdbAtomLine> standardReferenceFrame() {
    return Arrays.stream(StringUtils.split(standardReferenceFrameString(), '\n'))
        .map(StringUtils::split)
        .map(
            fields ->
                ImmutablePdbAtomLine.of(
                    Integer.parseInt(fields[1]),
                    fields[2],
                    " ",
                    fields[4],
                    "A",
                    1,
                    " ",
                    Double.parseDouble(fields[4]),
                    Double.parseDouble(fields[5]),
                    Double.parseDouble(fields[6]),
                    0.0,
                    0.0,
                    " ",
                    " "))
        .collect(Collectors.toMap(PdbAtomLine::detectAtomName, Function.identity()));
  }

  default Vector3D edgeVector(final PdbResidue residue, final NucleobaseEdge edge) {
    final Pair<AtomName, AtomName> pair = edgeVectorAtoms(edge);
    return residue
        .findAtom(pair.getRight())
        .toVector3D()
        .subtract(residue.findAtom(pair.getLeft()).toVector3D());
  }

  @Override
  default MoleculeType moleculeType() {
    return MoleculeType.RNA;
  }

  @Override
  default NucleotideComponentType nucleotideComponentType() {
    return NucleotideComponentType.BASE;
  }
}
