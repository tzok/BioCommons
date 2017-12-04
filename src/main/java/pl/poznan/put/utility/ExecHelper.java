package pl.poznan.put.utility;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public final class ExecHelper {
    public static final Logger LOGGER =
            LoggerFactory.getLogger(ExecHelper.class);

    private ExecHelper() {
        super();
    }

    public static String execute(final File workingDirectory,
                                 final File command, final String... arguments)
            throws IOException {
        return ExecHelper
                .execute(workingDirectory, Collections.emptyMap(), command,
                         arguments);
    }

    public static String execute(final File workingDirectory,
                                 final Map<String, String> environment,
                                 final File command, final String... arguments)
            throws IOException {
        // run `chmod a+x` on the command
        ExecHelper.makeExecutable(command);

        // prepare commandline
        final CommandLine commandLine = new CommandLine(command);
        for (final String argument : arguments) {
            commandLine.addArgument(argument);
        }

        // prepare handler for output and error streams
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ByteArrayOutputStream err = new ByteArrayOutputStream();
        final ExecuteStreamHandler handler = new PumpStreamHandler(out, err);

        // prepare the execution of command
        final Executor executor = new DefaultExecutor();
        executor.setStreamHandler(handler);
        executor.setWorkingDirectory(workingDirectory);

        try {
            // execute the command
            ExecHelper.LOGGER
                    .info("Running {} with arguments {} in {}", command,
                          Arrays.toString(arguments), workingDirectory);
            executor.execute(commandLine, environment);

            if (ExecHelper.LOGGER.isTraceEnabled()) {
                ExecHelper.LOGGER.trace("Standard output: {}", out);
                ExecHelper.LOGGER.trace("Standard error: {}", err);
            }

            return out.toString(Charset.defaultCharset());
        } catch (final IOException e) {
            ExecHelper.LOGGER.warn("Standard output: {}", out);
            ExecHelper.LOGGER.warn("Standard error: {}", err);
            throw e;
        }
    }

    public static File createRandomDirectory() throws IOException {
        final File tempDirectory = FileUtils.getTempDirectory();
        final String randomComponent = UUID.randomUUID().toString();
        final File randomDirectory = new File(tempDirectory, randomComponent);
        FileUtils.forceMkdir(randomDirectory);
        return randomDirectory;
    }

    private static void makeExecutable(final File file) {
        try {
            // prepare commandline
            final CommandLine chmod = new CommandLine("chmod");
            chmod.addArgument("a+x");
            chmod.addArgument(file.getAbsolutePath());

            // prepare handler for output and error streams
            final ExecuteStreamHandler handler =
                    new PumpStreamHandler(null, null);

            // execute the command
            final Executor executor = new DefaultExecutor();
            executor.setStreamHandler(handler);
            executor.execute(chmod);
        } catch (final IOException ignored) {
            // do nothing
        }
    }
}
