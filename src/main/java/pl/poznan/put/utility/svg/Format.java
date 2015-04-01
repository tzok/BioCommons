package pl.poznan.put.utility.svg;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.image.TIFFTranscoder;
import org.apache.batik.transcoder.svg2svg.SVGTranscoder;
import org.apache.fop.render.ps.EPSTranscoder;
import org.apache.fop.svg.PDFTranscoder;

public enum Format {
    SVG("svg"), EPS("eps"), PDF("pdf"), PNG("png"), TIFF("tiff");

    private final String extension;

    private Format(String extension) {
        this.extension = extension;
    }

    public Transcoder getTranscoder() {
        switch (this) {
        case EPS:
            return new EPSTranscoder();
        case PDF:
            return new PDFTranscoder();
        case PNG:
            return new PNGTranscoder();
        case TIFF:
            return new TIFFTranscoder();
        default:
        case SVG:
            return new SVGTranscoder();
        }
    }

    public String getExtension() {
        return extension;
    }
}
