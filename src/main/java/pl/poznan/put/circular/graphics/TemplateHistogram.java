package pl.poznan.put.circular.graphics;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.Circular;
import pl.poznan.put.circular.Histogram;
import pl.poznan.put.circular.enums.ValueType;
import pl.poznan.put.circular.utility.Helper;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Draws a histogram using one of predefined templates. */
public class TemplateHistogram {
  private static final MustacheFactory FACTORY = new DefaultMustacheFactory();

  public static void main(final String[] args) throws IOException {
    List<Circular> circulars = new ArrayList<>();
    for (final String line : Helper.readResource("1EHZ-chi").split("\n")) {
      circulars.add(new Angle(Double.parseDouble(line), ValueType.DEGREES));
    }

    Mustache template = Helper.readTemplateResource("pgfplots.mustache");
    TemplateHistogram histogram = new TemplateHistogram(circulars, template);
    File tempFile = File.createTempFile("histogram", ".tex");
    FileUtils.write(tempFile, histogram.generateFromTemplate(), Charset.defaultCharset());
    System.out.println(tempFile);
  }

  private final List<Circular> data;
  private final Mustache template;
  private final double binRadians;

  public TemplateHistogram(
      final List<Circular> data, final Mustache template, final double binRadians) {
    super();
    this.data = new ArrayList<>(data);
    this.template = template;
    this.binRadians = binRadians;
  }

  public TemplateHistogram(final List<Circular> data, final Mustache template) {
    this(data, template, Math.PI / 12);
  }

  public final String generateFromTemplate() throws IOException {
    Map<String, Object> fragments = new HashMap<>();
    fragments.put("points", generatePolarCoordinates());

    Map<String, Object> scopes = new HashMap<>();
    scopes.put("fragments", fragments);

    try (Writer stringWriter = new StringWriter()) {
      try (Writer writer = template.execute(stringWriter, scopes)) {
        return writer.toString();
      }
    }
  }

  private List<Vector2D> generatePolarCoordinates() {
    Histogram histogram = new Histogram(data, binRadians);
    double maxFrequency = histogram.getMaxFrequency();

    List<Vector2D> polarCoordinates = new ArrayList<>();

    for (double d = 0; d < MathUtils.TWO_PI; d += binRadians) {
      polarCoordinates.add(new Vector2D(0, 0));

      double frequency = (double) histogram.getBinSize(d) / data.size();
      double x = Math.toDegrees(d);
      // sqrt allows to see even some smaller clusters
      double y = FastMath.sqrt(frequency);
      polarCoordinates.add(new Vector2D(Math.toDegrees(d), y));
      polarCoordinates.add(new Vector2D(Math.toDegrees(d + binRadians), y));
    }
    polarCoordinates.add(new Vector2D(0, 0));

    return polarCoordinates;
  }
}
