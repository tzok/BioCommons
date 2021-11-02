package integration;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import pl.poznan.put.pdb.analysis.PdbChain;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.pdb.analysis.PdbParser;
import pl.poznan.put.structure.CanonicalStructureExtractor;
import pl.poznan.put.structure.formats.BpSeq;
import pl.poznan.put.structure.formats.Converter;
import pl.poznan.put.structure.formats.Ct;
import pl.poznan.put.structure.formats.DotBracket;
import pl.poznan.put.structure.formats.ImmutableDefaultConverter;
import pl.poznan.put.structure.pseudoknots.Region;
import pl.poznan.put.utility.ResourcesHelper;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class WikiExamples {
  @Test
  public void testParse() throws Exception {
    // will be used later to format deposition date
    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    // read PDB or mmCIF data from file
    final File file = ResourcesHelper.loadResourceFile("1EHZ.pdb");
    final String structureContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

    // parse the data
    final PdbParser parser = new PdbParser();
    final List<PdbModel> models = parser.parse(structureContent);

    // focus on the first model only
    final PdbModel firstModel = models.get(0);

    // print header information
    System.out.println("PDB id: " + firstModel.idCode());
    System.out.println("Title: " + firstModel.title());
    System.out.println("Classification: " + firstModel.header().classification());
    System.out.println(
        "Deposition date: " + dateFormat.format(firstModel.header().depositionDate()));
    System.out.println(
        "Solved with: "
            + firstModel.experimentalData().experimentalTechniques().get(0).getPdbName());
    System.out.println("Resolution: " + firstModel.resolution().resolution());

    // print information about modified residues
    System.out.println("Modified residues:");
    firstModel
        .modifiedResidues()
        .forEach(
            modification ->
                System.out.printf(
                    "  %s.%d %s ~ %s -> %s%n",
                    modification.chainIdentifier(),
                    modification.residueNumber(),
                    modification.residueName(),
                    modification.standardResidueName(),
                    modification.comment()));
  }

  @Test
  public void testSecondary() throws Exception {
    final File file = ResourcesHelper.loadResourceFile("1DDY.pdb");
    final String structureContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    final PdbParser parser = new PdbParser();
    final List<PdbModel> models = parser.parse(structureContent);
    final PdbModel model = models.get(0);

    // get chain A only
    final PdbChain chain =
        model.chains().stream()
            .filter(pdbChain -> "A".equals(pdbChain.identifier()))
            .findFirst()
            .orElseThrow(RuntimeException::new);

    // extract base pairs
    final BpSeq bpSeq = CanonicalStructureExtractor.bpSeq(chain);
    System.out.println("Pairs: " + bpSeq.paired().size());

    // remove isolated base pairs
    final BpSeq withoutIsolatedPairs = bpSeq.withoutIsolatedPairs();
    System.out.println("Non-isolated pairs: " + withoutIsolatedPairs.paired().size());

    // find stems
    final List<Region> regions = Region.createRegions(withoutIsolatedPairs);
    System.out.println("Stems: " + regions.size());
    regions.forEach(
        region ->
            System.out.printf(
                "  %d-%d [length=%d]%n", region.begin(), region.end(), region.length()));

    // convert to CT format
    final Ct ct = Ct.fromBpSeq(bpSeq);
    System.out.println("Strands: " + ct.strandCount());

    // convert to dot-bracket
    final Converter converter = ImmutableDefaultConverter.of();
    final DotBracket dotBracket = converter.convert(bpSeq);
    System.out.println("Pseudoknot order: " + dotBracket.pseudoknotOrder());
    System.out.println("Dot-bracket:\n\n" + dotBracket);
  }
}
