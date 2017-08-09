package pl.poznan.put.utility;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;

public final class ResourcesHelper {
    public static String loadResource(final String resource)
            throws IOException {
        final ClassLoader loader = ResourcesHelper.class.getClassLoader();
        try (InputStream stream = loader.getResourceAsStream(resource)) {
            if (stream == null) {
                throw new IllegalArgumentException(
                        String.format("Missing resource: %s", resource));
            }
            return IOUtils.toString(stream, Charset.defaultCharset());
        }
    }

    public static URI loadResourceUri(final String resource)
            throws URISyntaxException {
        final ClassLoader loader = ResourcesHelper.class.getClassLoader();
        final URL url = loader.getResource(resource);
        if (url == null) {
            throw new IllegalArgumentException(
                    String.format("Missing resource: %s", resource));
        }
        return url.toURI();
    }

    public static File loadResourceFile(final String resource)
            throws URISyntaxException {
        return new File(ResourcesHelper.loadResourceUri(resource));
    }

    private ResourcesHelper() {
        super();
    }
}
