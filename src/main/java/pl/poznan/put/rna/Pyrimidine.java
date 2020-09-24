package pl.poznan.put.rna;

import pl.poznan.put.torsion.TorsionAngleType;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** A pyrimidine (cytosine, uracil or thymine). */
public interface Pyrimidine extends Nucleobase {
  @Override
  default List<TorsionAngleType> torsionAngleTypes() {
    return Stream.of(
            NucleotideTorsionAngle.ALPHA.angleTypes().get(0),
            NucleotideTorsionAngle.BETA.angleTypes().get(0),
            NucleotideTorsionAngle.GAMMA.angleTypes().get(0),
            NucleotideTorsionAngle.DELTA.angleTypes().get(0),
            NucleotideTorsionAngle.EPSILON.angleTypes().get(0),
            NucleotideTorsionAngle.ZETA.angleTypes().get(0),
            NucleotideTorsionAngle.NU0.angleTypes().get(0),
            NucleotideTorsionAngle.NU1.angleTypes().get(0),
            NucleotideTorsionAngle.NU2.angleTypes().get(0),
            NucleotideTorsionAngle.NU3.angleTypes().get(0),
            NucleotideTorsionAngle.NU4.angleTypes().get(0),
            NucleotideTorsionAngle.ETA.angleTypes().get(0),
            NucleotideTorsionAngle.THETA.angleTypes().get(0),
            NucleotideTorsionAngle.ETA_PRIM.angleTypes().get(0),
            NucleotideTorsionAngle.THETA_PRIM.angleTypes().get(0),
            NucleotideTorsionAngle.PSEUDOPHASE_PUCKER.angleTypes().get(0),
            Chi.PYRIMIDINE.angleType())
        .collect(Collectors.toList());
  }
}
