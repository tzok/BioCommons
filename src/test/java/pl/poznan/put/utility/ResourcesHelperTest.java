package pl.poznan.put.utility;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

public class ResourcesHelperTest {
  @Test
  public final void loadResource() throws IOException {
    final String resource = ResourcesHelper.loadResource("test.txt");
    assertThat(resource, is("Test\n"));
  }

  @Test(expected = IllegalArgumentException.class)
  public final void loadResourceMissing() throws IOException {
    ResourcesHelper.loadResource("missing");
  }

  @Test
  public final void loadResourceUri() throws URISyntaxException {
    final URI resourceUri = ResourcesHelper.loadResourceUri("test.txt");
    assertThat(resourceUri, notNullValue());
  }

  @Test(expected = IllegalArgumentException.class)
  public final void loadResourceUriMissing() throws URISyntaxException {
    ResourcesHelper.loadResourceUri("missing");
  }

  @Test
  public final void loadResourceFile() throws URISyntaxException {
    final File resourceFile = ResourcesHelper.loadResourceFile("test.txt");
    assertThat(resourceFile, notNullValue());
    assertThat(resourceFile.exists(), is(true));
  }

  @Test(expected = IllegalArgumentException.class)
  public final void loadResourceFileMissing() throws URISyntaxException {
    ResourcesHelper.loadResourceFile("missing");
  }
}
