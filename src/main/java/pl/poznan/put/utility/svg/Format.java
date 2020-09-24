package pl.poznan.put.utility.svg;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.svg2svg.SVGTranscoder;
import org.apache.fop.render.ps.EPSTranscoder;
import org.apache.fop.svg.PDFTranscoder;

/** An image format which can be used to export SVG images to. */
public enum Format {
  SVG("svg", "--export-plain-svg"),
  EPS("eps", "--export-eps"),
  PDF("pdf", "--export-pdf"),
  PNG("png", "--export-png");

  private final String extension;
  private final String inkscapeArgument;

  Format(final String extension, final String inkscapeArgument) {
    this.extension = extension;
    this.inkscapeArgument = inkscapeArgument;
  }

  /** @return An instance of batik transcoder for this output image format. */
  public Transcoder getTranscoder() {
    switch (this) {
      case EPS:
        return new EPSTranscoder();
      case PDF:
        return new PDFTranscoder();
      case PNG:
        return new PNGTranscoder();
      case SVG:
      default:
        return new SVGTranscoder();
    }
  }

  /** @return The default extension for this image format. */
  public String getExtension() {
    return extension;
  }

  /** @return The switch used by Inkscape to export to this image format. */
  public String getInkscapeArgument() {
    return inkscapeArgument;
  }
}
