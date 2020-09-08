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
  private static final Logger LOGGER = LoggerFactory.getLogger(ExecHelper.class);

  private ExecHelper() {
    super();
  }

  public static ExecutionResult execute(
      final File workingDirectory, final String command, final String... arguments)
      throws IOException {
    return ExecHelper.execute(workingDirectory, Collections.emptyMap(), command, arguments);
  }

  public static ExecutionResult execute(
      final File workingDirectory,
      final Map<String, String> environment,
      final String command,
      final String... arguments)
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
    if (workingDirectory != null) {
      executor.setWorkingDirectory(workingDirectory);
    }

    try {
      if (ExecHelper.LOGGER.isInfoEnabled()) {
        if (workingDirectory != null) {
          ExecHelper.LOGGER.info(
              "Running {} with arguments {} in {}",
              command,
              Arrays.toString(arguments),
              workingDirectory);
        } else {
          ExecHelper.LOGGER.info(
              "Running {} with arguments {} in", command, Arrays.toString(arguments));
        }
      }

      // execute the command
      final int exitCode = executor.execute(commandLine, environment);

      if (ExecHelper.LOGGER.isTraceEnabled()) {
        ExecHelper.LOGGER.trace("Standard output: {}", out);
        ExecHelper.LOGGER.trace("Standard error: {}", err);
      }

      return new ExecutionResult(
          exitCode, out.toString(Charset.defaultCharset()), err.toString(Charset.defaultCharset()));
    } catch (final IOException e) {
      ExecHelper.LOGGER.warn("Standard output: {}", out);
      ExecHelper.LOGGER.warn("Standard error: {}", err);
      throw e;
    }
  }

  public static ExecutionResult execute(final String command, final String... arguments)
      throws IOException {
    return ExecHelper.execute(null, Collections.emptyMap(), command, arguments);
  }

  public static ExecutionResult execute(
      final Map<String, String> environment, final String command, final String... arguments)
      throws IOException {
    return ExecHelper.execute(null, environment, command, arguments);
  }

  public static File createRandomDirectory() throws IOException {
    final File tempDirectory = FileUtils.getTempDirectory();
    final String randomComponent = UUID.randomUUID().toString();
    final File randomDirectory = new File(tempDirectory, randomComponent);
    FileUtils.forceMkdir(randomDirectory);
    return randomDirectory;
  }

  private static void makeExecutable(final String path) {
    final File file = new File(path);
    if (!file.canExecute()) {
      file.setExecutable(true);
    }
  }

  public static final class ExecutionResult {
    private final int exitCode;
    private final String standardOutput;
    private final String standardError;

    private ExecutionResult(
        final int exitCode, final String standardOutput, final String standardError) {
      super();
      this.exitCode = exitCode;
      this.standardOutput = standardOutput;
      this.standardError = standardError;
    }

    public int getExitCode() {
      return exitCode;
    }

    public String getStandardOutput() {
      return standardOutput;
    }

    public String getStandardError() {
      return standardError;
    }
  }
}
