package com.github.kjens93.actions.toolkit.core;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

final class Command {

  private static final String CMD_STRING = "::";

  private final String command;
  private final String message;
  private final Map<String, Object> properties;

  public Command(String command, Map<String, Object> properties, String message) {
    if (StringUtils.isBlank(command)) {
      command = "missing.command";
    }
    this.command = command;
    this.properties = properties;
    this.message = message;
  }

  @Override
  public String toString() {
    String cmdStr = CMD_STRING + this.command;

    if (nonNull(this.properties) && this.properties.size() > 0) {
      cmdStr += " " + getPropertiesString();
    }

    cmdStr += CMD_STRING + escapeData(this.message);
    return cmdStr;
  }

  private String getPropertiesString() {
    return this.properties.entrySet().stream()
        .sorted(Map.Entry.comparingByKey())
        .filter(this::valueIsNotBlank)
        .map(this::joinWithEquals)
        .collect(Collectors.joining(","));
  }

  private boolean valueIsNotBlank(@NonNull Map.Entry<String, Object> entry) {
    return Optional.ofNullable(entry.getValue())
        .map(Object::toString)
        .map(StringUtils::isNotBlank)
        .orElse(false);
  }

  private String joinWithEquals(@NonNull Map.Entry<String, Object> entry) {
    String key = entry.getKey();
    String val = Optional.ofNullable(entry.getValue()).map(Object::toString).orElse("");
    return String.join("=", key, escapeProperty(val));
  }

  public static void issueCommand(String command, Map<String, Object> properties, Object message) {
    String msg = Optional.ofNullable(message).map(Object::toString).orElse("");
    Command cmd = new Command(command, properties, msg);
    System.out.println(cmd.toString());
  }

  public static void issueCommand(String command, String message) {
    issueCommand(command, Collections.emptyMap(), message);
  }

  public static void issueCommand(String command) {
    issueCommand(command, Collections.emptyMap(), "");
  }

  private static String escapeData(@NonNull String value) {
    return Optional.ofNullable(value)
        .orElse("")
        .replace("%", "%25")
        .replace("\r", "%0D")
        .replace("\n", "%0A");
  }

  private static String escapeProperty(@NonNull String value) {
    return Optional.ofNullable(value)
        .orElse("")
        .replace("%", "%25")
        .replace("\r", "%0D")
        .replace("\n", "%0A")
        .replace(":", "%3A")
        .replace(",", "%2C");
  }
}
