package pl.poznan.put.utility;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** An executor of external processes. */
@Value.Immutable
public abstract class ExecHelper {
  private static final Logger LOGGER = LoggerFactory.getLogger(ExecHelper.class);

  /**
   * Generates a random name and creates a directory named like that in system temporary directory.
   *
   * @return A path to the created directory.
   * @throws IOException When it was impossible to create the random directory.
   */
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

  /**
   * @return The working directory where to run the command in.
   */
  public abstract Optional<File> workingDirectory();

  /**
   * @return The command to run.
   */
  public abstract String command();

  /**
   * Executes the command in a working directory (if provided) with environment setting (if
   * provided).
   *
   * @return An object containing exit code with standard output and error streams.
   * @throws IOException When it was impossible to run the external command.
   */
  public ExecutionResult execute() throws IOException {
    // run `chmod a+x` on the command
    ExecHelper.makeExecutable(command());

    // prepare commandline
    final CommandLine commandLine = new CommandLine(command());
    for (final String argument : arguments()) {
      commandLine.addArgument(argument);
    }

    // prepare handler for output and error streams
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final ByteArrayOutputStream err = new ByteArrayOutputStream();
    final ExecuteStreamHandler handler = new PumpStreamHandler(out, err);

    // prepare the execution of command
    final Executor executor = new DefaultExecutor();
    executor.setStreamHandler(handler);
    if (workingDirectory().isPresent()) {
      executor.setWorkingDirectory(workingDirectory().get());
    }

    try {
      if (ExecHelper.LOGGER.isInfoEnabled()) {
        if (workingDirectory().isPresent()) {
          ExecHelper.LOGGER.info(
              "Running {} with arguments {} in {}", command(), arguments(), workingDirectory());
        } else {
          ExecHelper.LOGGER.info("Running {} with arguments {} in", command(), arguments());
        }
      }

      // execute the command
      final int exitCode = executor.execute(commandLine, environment());

      if (ExecHelper.LOGGER.isTraceEnabled()) {
        ExecHelper.LOGGER.trace("Standard output: {}", out);
        ExecHelper.LOGGER.trace("Standard error: {}", err);
      }

      return ImmutableExecutionResult.of(
          exitCode, out.toString(Charset.defaultCharset()), err.toString(Charset.defaultCharset()));
    } catch (final IOException e) {
      ExecHelper.LOGGER.warn("Standard output: {}", out);
      ExecHelper.LOGGER.warn("Standard error: {}", err);
      throw e;
    }
  }

  /**
   * @return The environment variables to be set during external command running (default: empty).
   */
  @Value.Default
  public Map<String, String> environment() {
    return Collections.emptyMap();
  }

  /**
   * @return The list of arguments to the command (default: empty).
   */
  @Value.Default
  public List<String> arguments() {
    return Collections.emptyList();
  }

  /** A result of running external command. */
  @Value.Immutable
  public interface ExecutionResult {
    /**
     * @return The exit code (0 means success).
     */
    @Value.Parameter(order = 1)
    int exitCode();

    /**
     * @return The contents of standard output stream.
     */
    @Value.Parameter(order = 2)
    String standardOutput();

    /**
     * @return The contents of standard error stream.
     */
    @Value.Parameter(order = 3)
    String standardError();
  }
}
