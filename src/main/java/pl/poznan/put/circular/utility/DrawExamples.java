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

public class DrawExamples {
  public static void main(String[] args) throws IOException {
    List<Angle> angles = Helper.loadHourMinuteData(Helper.readResource("example/D01"));
    List<Axis> axes = Helper.loadAxisData(Helper.readResource("example/D02"));

    // D01-angular-histogram
    Drawable angularHistogram = new AngularHistogram(angles);
    angularHistogram.draw();
    SVGDocument svgDocument = angularHistogram.finalizeDrawing();
    File file = new File("/tmp/D01-angular-histogram.png");
    FileUtils.writeByteArrayToFile(file, SVGHelper.export(svgDocument, Format.PNG));

    // D01-linear-histogram
    Drawable linearHistogram = new LinearHistogram(angles);
    linearHistogram.draw();
    svgDocument = linearHistogram.finalizeDrawing();
    file = new File("/tmp/D01-linear-histogram.png");
    FileUtils.writeByteArrayToFile(file, SVGHelper.export(svgDocument, Format.PNG));

    // D01-raw-plot
    Drawable rawDataPlot = new RawDataPlot(angles);
    rawDataPlot.draw();
    svgDocument = rawDataPlot.finalizeDrawing();
    file = new File("/tmp/D01-raw-plot.png");
    FileUtils.writeByteArrayToFile(file, SVGHelper.export(svgDocument, Format.PNG));

    // D02-angular-histogram
    angularHistogram = new AngularHistogram(axes);
    angularHistogram.draw();
    svgDocument = angularHistogram.finalizeDrawing();
    file = new File("/tmp/D02-angular-histogram.png");
    FileUtils.writeByteArrayToFile(file, SVGHelper.export(svgDocument, Format.PNG));

    // D02-raw-plot
    rawDataPlot = new RawDataPlot(axes);
    rawDataPlot.draw();
    svgDocument = rawDataPlot.finalizeDrawing();
    file = new File("/tmp/D02-raw-plot.png");
    FileUtils.writeByteArrayToFile(file, SVGHelper.export(svgDocument, Format.PNG));
  }
}
