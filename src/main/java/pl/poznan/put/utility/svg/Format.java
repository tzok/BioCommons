package pl.poznan.put.utility.svg;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.image.TIFFTranscoder;
import org.apache.batik.transcoder.svg2svg.SVGTranscoder;
import org.apache.fop.render.ps.EPSTranscoder;
import org.apache.fop.svg.PDFTranscoder;

public enum Format {
    SVG(new SVGTranscoder()), EPS(new EPSTranscoder()), PDF(new PDFTranscoder()), PNG(new PNGTranscoder()), TIFF(new TIFFTranscoder());

    private final Transcoder transcoder;

    private Format(Transcoder transcoder) {
        this.transcoder = transcoder;
    }

    public Transcoder getTranscoder() {
        return transcoder;
    }
}
