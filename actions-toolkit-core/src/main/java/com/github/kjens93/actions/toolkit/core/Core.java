package com.github.kjens93.actions.toolkit.core;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.github.kjens93.actions.toolkit.core.Command.issueCommand;

public final class Core {

  // -----------------------------------------------------------------------
  // Constructors (hidden to enforce static usage)
  // -----------------------------------------------------------------------

  private Core() {}

  // -----------------------------------------------------------------------
  // Variables
  // -----------------------------------------------------------------------

  /**
   * Sets env variable for future actions in the job
   *
   * @param name the name of the variable to set
   * @param val the value of the variable
   * @deprecated Modifying environment variables for the currently running process is not supported
   *     in Java. Exported variables will not be available to the current process. To set
   *     environment variables for the current action, export them as part of the Dockerfile or
   *     entrypoint instead. Variables exported via this method should be available in subsequent
   *     actions. This is slightly different behavior from the JavaScript implementation.
   */
  public static void exportVariable(@NonNull String name, @NonNull Object val) {
    Map<String, Object> props = new HashMap<>();
    props.put("name", name);
    issueCommand("set-env", props, val);
  }

  /**
   * Gets the value of an environment variable. The value is also trimmed.
   *
   * @param name name of the variable to get
   * @param required whether the variable is required; if required and not present, will throw
   * @return string
   */
  public static String getVariable(@NonNull String name, boolean required) {
    String val = Optional.ofNullable(System.getenv(name)).orElse("");
    if (required && StringUtils.isBlank(val)) {
      throw new IllegalStateException("Variable required and not supplied: " + name);
    }
    return val.trim();
  }

  /**
   * Gets the value of an environment variable. The value is also trimmed.
   *
   * @param name name of the variable to get
   * @return string
   */
  public static String getVariable(@NonNull String name) {
    return getVariable(name, false);
  }

  /**
   * Registers a secret which will get masked from logs
   *
   * @param secret value of the secret
   */
  public static void setSecret(@NonNull String secret) {
    issueCommand("add-mask", secret);
  }

  /**
   * Prepends inputPath to the PATH (for future actions)
   *
   * @param inputPath the string to be prepended to the PATH
   * @deprecated PATH manipulation for the currently running process is not supported in Java.
   *     Exported PATH items will not be available to the current process. To modify PATH for the
   *     current action, do it as part of the Dockerfile or entrypoint instead. Items added via this
   *     method should be available in subsequent actions. This is slightly different behavior from
   *     the JavaScript implementation.
   */
  public static void addPath(@NonNull String inputPath) {
    issueCommand("add-path", inputPath);
  }

  /**
   * Gets the value of an input. The value is also trimmed.
   *
   * @param name name of the input to get
   * @param required whether the input is required; if required and not present, will throw
   * @return string
   */
  public static String getInput(@NonNull String name, boolean required) {
    String key = "INPUT_" + name.replaceAll(" ", "_").toUpperCase();
    String val = Optional.ofNullable(System.getenv(key)).orElse("");
    if (required && StringUtils.isBlank(val)) {
      throw new IllegalStateException("Input required and not supplied: " + name);
    }
    return val.trim();
  }

  /**
   * Gets the value of an input. The value is also trimmed.
   *
   * @param name name of the input to get
   * @return string
   */
  public static String getInput(@NonNull String name) {
    return getInput(name, false);
  }

  /**
   * Sets the value of an output.
   *
   * @param name name of the output to set
   * @param value value to store
   */
  public static void setOutput(@NonNull String name, @NonNull Object value) {
    Map<String, Object> props = new HashMap<>();
    props.put("name", name);
    issueCommand("set-output", props, value);
  }

  // -----------------------------------------------------------------------
  // Results
  // -----------------------------------------------------------------------

  /**
   * Sets the action status to failed. When the action exits it will be with an exit code of 1
   *
   * @param message add error issue message
   */
  public static void setFailed(@NonNull String message) {
    error(message);
    System.exit(ExitCode.FAILURE);
  }

  // -----------------------------------------------------------------------
  // Logging Commands
  // -----------------------------------------------------------------------

  /** Gets whether Actions Step Debug is on or not */
  public static boolean isDebug() {
    return Objects.equals(System.getenv("RUNNER_DEBUG"), "1");
  }

  /**
   * Writes debug message to user log
   *
   * @param message debug message
   */
  public static void debug(@NonNull String message) {
    issueCommand("debug", message);
  }

  /**
   * Adds an error issue
   *
   * @param message error issue message
   */
  public static void error(@NonNull String message) {
    issueCommand("error", message);
  }

  /**
   * Adds an warning issue
   *
   * @param message warning issue message
   */
  public static void warning(@NonNull String message) {
    issueCommand("warning", message);
  }

  /**
   * Writes info to log with console.log.
   *
   * @param message info message
   */
  public static void info(@NonNull String message) {
    System.out.println(message);
  }

  /**
   * Begin an output group.
   *
   * <p>Output until the next `groupEnd` will be foldable in this group
   *
   * @param name The name of the output group
   */
  public static void startGroup(@NonNull String name) {
    issueCommand("group", name);
  }

  /** End an output group. */
  public static void endGroup() {
    issueCommand("endgroup");
  }

  /**
   * Wrap a function call in a group.
   *
   * <p>Returns the same type as the function itself.
   *
   * @param name The name of the group
   * @param fn The function to wrap in the group
   */
  public static <T, E extends Throwable> T group(@NonNull String name, @NonNull TSupplier<T, E> fn)
      throws E {
    startGroup(name);
    try {
      return fn.get();
    } finally {
      endGroup();
    }
  }

  /**
   * Wrap a function call in a group.
   *
   * <p>Returns the same type as the function itself.
   *
   * @param name The name of the group
   * @param fn The function to wrap in the group
   */
  public static <E extends Throwable> void group(@NonNull String name, @NonNull TRunnable<E> fn)
      throws E {
    startGroup(name);
    try {
      fn.run();
    } finally {
      endGroup();
    }
  }

  // -----------------------------------------------------------------------
  // Wrapper action state
  // -----------------------------------------------------------------------

  /**
   * Saves state for current action, the state can only be retrieved by this action's post job
   * execution.
   *
   * @param name name of the state to store
   * @param value value to store
   */
  public static void saveState(@NonNull String name, @NonNull Object value) {
    Map<String, Object> props = new HashMap<>();
    props.put("name", name);
    issueCommand("save-state", props, value);
  }

  /**
   * Gets the value of an state set by this action's main execution.
   *
   * @param name name of the state to get
   * @return string
   */
  public static String getState(@NonNull String name) {
    return Optional.ofNullable(System.getenv("STATE_" + name)).orElse("");
  }

  // -----------------------------------------------------------------------
  // Exit codes
  // -----------------------------------------------------------------------

  /** The code to exit an action */
  public abstract static class ExitCode {

    /** A code indicating that the action was successful */
    public static final int SUCCESS = 0;

    /** A code indicating that the action was a failure */
    public static final int FAILURE = 1;
  }

  // -----------------------------------------------------------------------
  // Convenience interfaces
  // -----------------------------------------------------------------------

  @FunctionalInterface
  public interface TRunnable<E extends Throwable> {
    void run() throws E;
  }

  @FunctionalInterface
  public interface TSupplier<T, E extends Throwable> {
    T get() throws E;
  }
}
