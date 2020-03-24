package com.github.kjens93.actions.toolkit.core;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

import java.util.HashMap;
import java.util.Map;

import static com.github.kjens93.actions.toolkit.core.Command.issueCommand;
import static org.assertj.core.api.Assertions.assertThat;

public class CommandTests {

  @Rule public final SystemOutRule out = new SystemOutRule().enableLog();

  @After
  public void teardown() {
    out.clearLog();
  }

  @Test
  public void command_only() {
    issueCommand("some-command", "");
    assertWriteCalls("::some-command::");
  }

  @Test
  public void command_escapes_message() {
    issueCommand("some-command", "percent % percent % cr \r cr \r lf \n lf \n");
    issueCommand("some-command", "%25 %25 %0D %0D %0A %0A");

    assertWriteCalls(
        "::some-command::percent %25 percent %25 cr %0D cr %0D lf %0A lf %0A",
        "::some-command::%2525 %2525 %250D %250D %250A %250A");
  }

  @Test
  public void command_escapes_property() {
    Map<String, Object> props = new HashMap<>();
    props.put(
        "name", "percent % percent % cr \r cr \r lf \n lf \n colon : colon : comma , comma ,");

    issueCommand("some-command", props, "");
    issueCommand("some-command", "%25 %25 %0D %0D %0A %0A %3A %3A %2C %2C");

    assertWriteCalls(
        "::some-command name=percent %25 percent %25 cr %0D cr %0D lf %0A lf %0A colon %3A colon %3A comma %2C comma %2C::",
        "::some-command::%2525 %2525 %250D %250D %250A %250A %253A %253A %252C %252C");
  }

  @Test
  public void command_with_message() {
    issueCommand("some-command", "some message");
    assertWriteCalls("::some-command::some message");
  }

  @Test
  public void command_with_message_and_properties() {
    Map<String, Object> props = new HashMap<>();
    props.put("prop1", "value 1");
    props.put("prop2", "value 2");
    issueCommand("some-command", props, "some message");
    assertWriteCalls("::some-command prop1=value 1,prop2=value 2::some message");
  }

  @Test
  public void command_with_one_property() {
    Map<String, Object> props = new HashMap<>();
    props.put("prop1", "value 1");
    issueCommand("some-command", props, "");
    assertWriteCalls("::some-command prop1=value 1::");
  }

  @Test
  public void command_with_two_properties() {
    Map<String, Object> props = new HashMap<>();
    props.put("prop1", "value 1");
    props.put("prop2", "value 2");
    issueCommand("some-command", props, "");
    assertWriteCalls("::some-command prop1=value 1,prop2=value 2::");
  }

  @Test
  public void command_with_three_properties() {
    Map<String, Object> props = new HashMap<>();
    props.put("prop1", "value 1");
    props.put("prop2", "value 2");
    props.put("prop3", "value 3");
    issueCommand("some-command", props, "");
    assertWriteCalls("::some-command prop1=value 1,prop2=value 2,prop3=value 3::");
  }

  // Assert that System.out.println calls called only with the given arguments.
  private void assertWriteCalls(String... calls) {
    String[] logLines = out.getLog().split(System.lineSeparator());
    assertThat(logLines).containsExactly(calls);
  }
}
