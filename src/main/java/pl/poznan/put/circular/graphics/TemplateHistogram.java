package pl.poznan.put.circular.graphics;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.Circular;
import pl.poznan.put.circular.Histogram;
import pl.poznan.put.circular.enums.ValueType;
import pl.poznan.put.circular.utility.Helper;

/** Draws a histogram using one of predefined templates. */
public final class TemplateHistogram {
  private static final MustacheFactory FACTORY = new DefaultMustacheFactory();

  public static void main(final String[] args) throws IOException {
    final List<Circular> circulars = new ArrayList<>();
    for (final String line : Helper.readResource("1EHZ-chi").split("\n")) {
      circulars.add(new Angle(Double.parseDouble(line), ValueType.DEGREES));
    }

    final Mustache template = Helper.readTemplateResource("pgfplots.mustache");
    final TemplateHistogram histogram = new TemplateHistogram(circulars, template);
    final File tempFile = File.createTempFile("histogram", ".tex");
    FileUtils.write(tempFile, histogram.generateFromTemplate(), Charset.defaultCharset());
    System.out.println(tempFile);
  }

  private final List<Circular> data;
  private final Mustache template;
  private final double binRadians;

  private TemplateHistogram(
      final List<Circular> data, final Mustache template, final double binRadians) {
    super();
    this.data = new ArrayList<>(data);
    this.template = template;
    this.binRadians = binRadians;
  }

  private TemplateHistogram(final List<Circular> data, final Mustache template) {
    this(data, template, FastMath.PI / 12);
  }

  private String generateFromTemplate() throws IOException {
    final Map<String, Object> fragments = new HashMap<>();
    fragments.put("points", generatePolarCoordinates());

    final Map<String, Object> scopes = new HashMap<>();
    scopes.put("fragments", fragments);

    try (final Writer stringWriter = new StringWriter()) {
      try (final Writer writer = template.execute(stringWriter, scopes)) {
        return writer.toString();
      }
    }
  }

  private List<Vector2D> generatePolarCoordinates() {
    final Histogram histogram = new Histogram(data, binRadians);
    final double maxFrequency = histogram.getMaxFrequency();

    final List<Vector2D> polarCoordinates = new ArrayList<>();

    for (double d = 0; d < MathUtils.TWO_PI; d += binRadians) {
      polarCoordinates.add(new Vector2D(0, 0));

      final double frequency = (double) histogram.getBinSize(d) / data.size();
      final double x = FastMath.toDegrees(d);
      // sqrt allows to see even some smaller clusters
      final double y = FastMath.sqrt(frequency);
      polarCoordinates.add(new Vector2D(Math.toDegrees(d), y));
      polarCoordinates.add(new Vector2D(Math.toDegrees(d + binRadians), y));
    }
    polarCoordinates.add(new Vector2D(0, 0));

    return polarCoordinates;
  }
}
