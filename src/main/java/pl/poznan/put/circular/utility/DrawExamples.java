package pl.poznan.put.circular.utility;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.svg.SVGDocument;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.Axis;
import pl.poznan.put.circular.graphics.AngularHistogram;
import pl.poznan.put.circular.graphics.Drawable;
import pl.poznan.put.circular.graphics.LinearHistogram;
import pl.poznan.put.circular.graphics.RawDataPlot;
import pl.poznan.put.utility.svg.Format;
import pl.poznan.put.utility.svg.SVGHelper;

public final class DrawExamples {
  private DrawExamples() {
    super();
  }

  public static void main(final String[] args) throws IOException {
    final List<Angle> angles = Helper.loadHourMinuteData(Helper.readResource("example/D01"));
    final List<Axis> axes = Helper.loadAxisData(Helper.readResource("example/D02"));

    // D01-angular-histogram
    final Drawable angularHistogramAngles = new AngularHistogram(angles);
    angularHistogramAngles.draw();
    DrawExamples.exportImage(angularHistogramAngles, "D01-angular-histogram");

    // D01-linear-histogram
    final Drawable linearHistogram = new LinearHistogram(angles);
    linearHistogram.draw();
    DrawExamples.exportImage(linearHistogram, "D01-linear-histogram");

    // D01-raw-plot
    final Drawable rawDataPlotAngles = new RawDataPlot(angles);
    rawDataPlotAngles.draw();
    DrawExamples.exportImage(rawDataPlotAngles, "D01-raw-plot");

    // D02-angular-histogram
    final Drawable angularHistogramAxes = new AngularHistogram(axes);
    angularHistogramAxes.draw();
    DrawExamples.exportImage(angularHistogramAxes, "D02-angular-histogram");

    // D02-raw-plot
    final Drawable rawDataPlotAxes = new RawDataPlot(axes);
    rawDataPlotAxes.draw();
    DrawExamples.exportImage(linearHistogram, "D02-raw-plot");
  }

  private static void exportImage(final Drawable drawable, final String filePrefix)
      throws IOException {
    final SVGDocument svgDocument = drawable.finalizeDrawing();
    final File file = File.createTempFile(filePrefix, ".png");
    FileUtils.writeByteArrayToFile(file, SVGHelper.export(svgDocument, Format.PNG));
  }
}
