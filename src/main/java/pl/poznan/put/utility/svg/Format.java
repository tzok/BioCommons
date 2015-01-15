package pl.poznan.put.utility.svg;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.image.TIFFTranscoder;
import org.apache.batik.transcoder.svg2svg.SVGTranscoder;
import org.apache.fop.render.ps.EPSTranscoder;
import org.apache.fop.svg.PDFTranscoder;

public enum Format {
    SVG(new SVGTranscoder(), "svg"), EPS(new EPSTranscoder(), "eps"), PDF(new PDFTranscoder(), "pdf"), PNG(new PNGTranscoder(), "png"), TIFF(new TIFFTranscoder(), "tiff");

    private final Transcoder transcoder;
    private final String extension;

    private Format(Transcoder transcoder, String extension) {
        this.transcoder = transcoder;
        this.extension = extension;
    }

    public Transcoder getTranscoder() {
        return transcoder;
    }

    public String getExtension() {
        return extension;
    }
}
