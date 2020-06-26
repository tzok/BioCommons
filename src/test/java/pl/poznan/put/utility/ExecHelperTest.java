package pl.poznan.put.utility;

import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ExecHelper.class)
public class ExecHelperTest {

    static File classTestFile;

    @Mock
    DefaultExecutor mockExecHelper;

    @BeforeClass
    public static void init() throws Exception {
        classTestFile = File.createTempFile("test", null);
        classTestFile.setExecutable(false);
    }

    @AfterClass
    public static void cleanUp(){
        classTestFile.delete();
    }

    @Test
    public final void checkOperatingSystemCompatibility() throws Exception {
        MockitoAnnotations.initMocks(this);
        whenNew(DefaultExecutor.class).withNoArguments().thenReturn(mockExecHelper);
        if(!System.getProperty("os.name").toLowerCase().contains("windows")){
            assertFalse(classTestFile.canExecute());
        }
        ExecHelper.execute(classTestFile.getAbsolutePath());
        assertTrue(classTestFile.canExecute());
    }
}