package pl.poznan.put.utility;

import org.apache.commons.exec.DefaultExecutor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ExecHelper.class)
public class ExecHelperTest {
  private File testFile;

  @Mock private DefaultExecutor mockExecHelper;

  @Before
  public final void init() throws Exception {
    testFile = File.createTempFile("ExecHelperTest", ".exe");
    testFile.setExecutable(false);
  }

  @After
  public final void cleanUp() {
    testFile.delete();
  }

  @Test
  public final void testFilePermissionsChange() throws Exception {
    // on Windows, File.canExecute() always returns true
    // even if @Before method explicitly calls File.setExecutable(false);
    if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
      MockitoAnnotations.initMocks(this);
      whenNew(DefaultExecutor.class).withNoArguments().thenReturn(mockExecHelper);

      assertFalse(testFile.canExecute());
      ExecHelper.execute(testFile.getAbsolutePath());
      assertTrue(testFile.canExecute());
    }
  }
}
