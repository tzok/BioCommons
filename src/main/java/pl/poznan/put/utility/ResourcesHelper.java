package pl.poznan.put.utility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;

public final class ResourcesHelper {
  /**
   * Load contents of a resource file available in the JAR of running process.
   *
   * @param resource Name of the resource.
   * @return Contents of the file addressed by the resource name.
   * @throws IOException If the file cannot be converted to {@link String}
   */
  public static String loadResource(final String resource) throws IOException {
    final ClassLoader loader = ResourcesHelper.class.getClassLoader();
    try (InputStream stream = loader.getResourceAsStream(resource)) {
      if (stream == null) {
        throw new IllegalArgumentException(String.format("Missing resource: %s", resource));
      }
      return IOUtils.toString(stream, Charset.defaultCharset());
    }
  }

  /**
   * Translate resource address to a URI.
   *
   * @param resource Name of the resource.
   * @return URI of the resource.
   * @throws URISyntaxException If the resource URI could not be created.
   */
  public static URI loadResourceUri(final String resource) throws URISyntaxException {
    final ClassLoader loader = ResourcesHelper.class.getClassLoader();
    final URL url = loader.getResource(resource);
    if (url == null) {
      throw new IllegalArgumentException(String.format("Missing resource: %s", resource));
    }
    return url.toURI();
  }

  public static File loadResourceFile(final String resource) throws URISyntaxException {
    return new File(ResourcesHelper.loadResourceUri(resource));
  }

  private ResourcesHelper() {
    super();
  }
}
