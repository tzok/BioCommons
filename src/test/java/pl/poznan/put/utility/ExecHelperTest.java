package pl.poznan.put.utility;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class ExecHelperTest {
    @Test
    public final void checkOperatingSystemCompatibility(){
        Assert.assertThat(System.getProperty("os.name").toLowerCase().contains("windows"),is(false));
    }
}