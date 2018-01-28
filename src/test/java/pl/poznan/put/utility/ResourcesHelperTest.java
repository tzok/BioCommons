package pl.poznan.put.utility;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.junit.Test;

public class ResourcesHelperTest {
  @Test
  public void loadResource() throws IOException {
    final String resource = ResourcesHelper.loadResource("test.txt");
    assertEquals("Test\n", resource);
  }

  @Test(expected = IllegalArgumentException.class)
  public void loadResourceMissing() throws IOException {
    ResourcesHelper.loadResource("missing");
  }

  @Test
  public void loadResourceUri() throws URISyntaxException {
    final URI resourceUri = ResourcesHelper.loadResourceUri("test.txt");
    assertNotNull(resourceUri);
  }

  @Test(expected = IllegalArgumentException.class)
  public void loadResourceUriMissing() throws URISyntaxException {
    ResourcesHelper.loadResourceUri("missing");
  }

  @Test
  public void loadResourceFile() throws URISyntaxException {
    final File resourceFile = ResourcesHelper.loadResourceFile("test.txt");
    assertNotNull(resourceFile);
    assertTrue(resourceFile.exists());
  }

  @Test(expected = IllegalArgumentException.class)
  public void loadResourceFileMissing() throws URISyntaxException {
    ResourcesHelper.loadResourceFile("missing");
  }
}
