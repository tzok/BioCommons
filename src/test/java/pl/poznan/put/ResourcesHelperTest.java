package pl.poznan.put;

import org.junit.Test;
import pl.poznan.put.utility.ResourcesHelper;

import java.io.IOException;

public class ResourcesHelperTest {
  @Test(expected = IllegalArgumentException.class)
  public final void testMissing() throws IOException {
    ResourcesHelper.loadResource("missing-resource");
  }
}
