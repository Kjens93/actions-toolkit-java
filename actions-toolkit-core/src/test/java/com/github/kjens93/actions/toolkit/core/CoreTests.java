package com.github.kjens93.actions.toolkit.core;

import org.junit.*;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.SystemOutRule;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CoreTests {

  @Rule public final SystemOutRule out = new SystemOutRule().enableLog();

  @Rule public final ExpectedSystemExit exit = ExpectedSystemExit.none();

  @Rule public final EnvironmentVariables env = new EnvironmentVariables();

  private static final Map<String, String> TEST_ENV_VARS = new HashMap<>();

  @BeforeClass
  public static void boostrap() {
    TEST_ENV_VARS.put("my var", "");
    TEST_ENV_VARS.put("special char var \r\n];", "");
    TEST_ENV_VARS.put("my var2", "");
    TEST_ENV_VARS.put("my secret", "");
    TEST_ENV_VARS.put("special char secret \r\n];", "");
    TEST_ENV_VARS.put("my secret2", "");
    TEST_ENV_VARS.put("PATH", String.join(":", "path1", "path2"));

    // Set inputs
    TEST_ENV_VARS.put("INPUT_MY_INPUT", "val");
    TEST_ENV_VARS.put("INPUT_MISSING", "");
    TEST_ENV_VARS.put("INPUT_SPECIAL_CHARS_'\t\"\\", "'\t\"\\ response ");

    TEST_ENV_VARS.put("INPUT_MULTIPLE_SPACES_VARIABLE", "I have multiple spaces");

    // Save inputs
    TEST_ENV_VARS.put("STATE_TEST_1", "state_val");
  }

  @Before
  public void setup() {
    TEST_ENV_VARS.forEach(env::set);
  }

  @After
  public void teardown() {
    env.clear(TEST_ENV_VARS.keySet().toArray(new String[0]));
    out.clearLog();
  }

  @Test
  public void exportVariable_produces_the_correct_command_and_sets_the_env() {
    Core.exportVariable("my var", "var val");
    assertWriteCalls("::set-env name=my var::var val");
  }

  @Test
  public void exportVariable_escapes_variable_names() {
    Core.exportVariable("special char var \r\n,:", "special val");
    assertWriteCalls("::set-env name=special char var %0D%0A%2C%3A::special val");
  }

  @Test
  public void exportVariable_escapes_variable_values() {
    Core.exportVariable("my var2", "var val\r\n");
    assertWriteCalls("::set-env name=my var2::var val%0D%0A");
  }

  @Test
  public void setSecret_produces_the_correct_command() {
    Core.setSecret("secret val");
    assertWriteCalls("::add-mask::secret val");
  }

  @Test
  public void prependPath_produces_the_correct_commands_and_sets_the_env() {
    Core.addPath("myPath");
    assertWriteCalls("::add-path::myPath");
  }

  @Test
  public void getInput_gets_non_required_input() {
    assertThat(Core.getInput("my input")).isEqualTo("val");
  }

  @Test
  public void getInput_gets_required_input() {
    assertThat(Core.getInput("my input", true)).isEqualTo("val");
  }

  @Test
  public void getInput_throws_on_missing_required_input() {
    assertThatThrownBy(() -> Core.getInput("missing", true))
        .hasMessage("Input required and not supplied: missing");
  }

  @Test
  public void getInput_does_not_throw_on_missing_non_required_input() {
    assertThat(Core.getInput("missing")).isEqualTo("");
  }

  @Test
  public void getInput_is_case_insensitive() {
    assertThat(Core.getInput("My InPuT")).isEqualTo("val");
  }

  @Test
  public void getInput_handles_special_characters() {
    assertThat(Core.getInput("special chars_'\t\"\\")).isEqualTo("'\t\"\\ response");
  }

  @Test
  public void getInput_handle_multiple_spaces() {
    assertThat(Core.getInput("multiple spaces variable")).isEqualTo("I have multiple spaces");
  }

  @Test
  public void setOutput_produces_the_correct_command() {
    Core.setOutput("some output", "some value");
    assertWriteCalls("::set-output name=some output::some value");
  }

  @Test
  public void setFailure_sets_the_correct_exit_code_and_failure_message() {
    exit.expectSystemExitWithStatus(Core.ExitCode.FAILURE);
    // FIXME: assertion does not work for some reason:
    // exit.checkAssertionAfterwards(() -> {
    //   assertWriteCalls("::error::Failure message");
    // });
    Core.setFailed("Failure message");
  }

  @Test
  public void setFailure_escapes_the_failure_message() {
    exit.expectSystemExitWithStatus(Core.ExitCode.FAILURE);
    // FIXME: assertion does not work for some reason:
    // exit.checkAssertionAfterwards(() -> {
    //   assertWriteCalls("::error::Failure %0D%0A%0Amessage%0D");
    // });
    Core.setFailed("Failure \r\n\nmessage\r");
  }

  @Test
  public void error_sets_the_correct_error_message() {
    Core.error("Error message");
    assertWriteCalls("::error::Error message");
  }

  @Test
  public void error_escapes_the_error_message() {
    Core.error("Error message\r\n\n");
    assertWriteCalls("::error::Error message%0D%0A%0A");
  }

  @Test
  public void warning_sets_the_correct_message() {
    Core.warning("Warning");
    assertWriteCalls("::warning::Warning");
  }

  @Test
  public void warning_escapes_the_message() {
    Core.warning("\r\nwarning\n");
    assertWriteCalls("::warning::%0D%0Awarning%0A");
  }

  @Test
  public void startGroup_starts_a_new_group() {
    Core.startGroup("my-group");
    assertWriteCalls("::group::my-group");
  }

  @Test
  public void endGroup_ends_new_group() {
    Core.endGroup();
    assertWriteCalls("::endgroup::");
  }

  @Test
  public void group_wraps_a_runnable_in_a_group() {
    Core.group("mygroup", () -> System.out.println("in my group"));
    assertWriteCalls("::group::mygroup", "in my group", "::endgroup::");
  }

  @Test
  public void group_wraps_a_supplier_in_a_group() {
    boolean result =
        Core.group(
            "mygroup",
            () -> {
              System.out.println("in my group");
              return true;
            });
    assertThat(result).isTrue();
    assertWriteCalls("::group::mygroup", "in my group", "::endgroup::");
  }

  @Test
  public void debug_sets_the_correct_message() {
    Core.debug("Debug");
    assertWriteCalls("::debug::Debug");
  }

  @Test
  public void debug_escapes_the_message() {
    Core.debug("\r\ndebug\n");
    assertWriteCalls("::debug::%0D%0Adebug%0A");
  }

  @Test
  public void saveState_produces_the_correct_command() {
    Core.saveState("state_1", "some value");
    assertWriteCalls("::save-state name=state_1::some value");
  }

  @Test
  public void getState_gets_wrapper_action_state() {
    assertThat(Core.getState("TEST_1")).isEqualTo("state_val");
  }

  @Test
  public void isDebug_check_debug_state() {
    String current = System.getenv("RUNNER_DEBUG");
    try {
      env.clear("RUNNER_DEBUG");
      assertThat(Core.isDebug()).isEqualTo(false);
      env.set("RUNNER_DEBUG", "1");
      assertThat(Core.isDebug()).isEqualTo(true);
    } finally {
      env.set("RUNNER_DEBUG", current);
    }
  }

  // Assert that System.out.println calls called only with the given arguments.
  private void assertWriteCalls(String... calls) {
    String[] logLines = out.getLog().split(System.lineSeparator());
    assertThat(logLines).containsExactly(calls);
  }
}
