package pl.poznan.put;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class Helper {
    public static String loadResource(final String resource)
            throws IOException {
        try (InputStream stream = Helper.class.getClassLoader()
                                              .getResourceAsStream(resource)) {
            return IOUtils.toString(stream, Charset.defaultCharset());
        }
    }
}
