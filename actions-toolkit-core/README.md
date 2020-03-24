# `actions-toolkit-core`

> Core functions for setting results, logging, registering secrets and exporting variables across actions

## Usage

### Import the class

```java
import com.github.kjens93.actions.toolkit.core.Core;
```

#### Inputs/Outputs

Action inputs can be read with `getInput`.  Outputs can be set with `setOutput` which makes them available to be mapped into inputs of other actions to ensure they are decoupled.

```java
// Get an optional value
String optionalValue = Core.getInput("inputName");

// Get a required value
String requiredValue = Core.getInput("inputName", true);

// Set an output
Core.setOutput("outputKey", "outputVal");
```

#### Exporting variables

You can use `exportVariable` to add environment variables to future steps' environment blocks.

```java
Core.exportVariable("envVar", "Val");
```

> Modifying environment variables for the currently running process is not supported
> in Java. Exported variables will not be available to the current process. To set
> environment variables for the current action, export them as part of the Dockerfile or 
> entrypoint instead. Variables exported via this method should be available in subsequent
> actions. This is slightly different behavior from the JavaScript implementation.

#### Setting a secret

Setting a secret registers the secret with the runner to ensure it is masked in logs.

```java
Core.setSecret("myPassword");
```

#### PATH Manipulation

To make a tool's path available in the path for future steps, use addPath. The runner will prepend the path given to future jobs' PATH.

```java
Core.addPath("/path/to/mytool");
```

> PATH manipulation for the currently running process is not supported in Java.
> Exported PATH items will not be available to the current process. To modify PATH for the
> current action, do it as part of the Dockerfile or entrypoint instead. Items added via this
> method should be available in subsequent actions. This is slightly different behavior from
> the JavaScript implementation.

#### Exit codes

You should use this library to set the failing exit code for your action.  If status is not set and the script runs to completion, that will lead to a success.

```java
try {
  // Do stuff
}
catch (Exception e) {
  // setFailed logs the message and sets a failing exit code
  Core.setFailed("Action failed with error " + e.getMessage());
}
```

> Note that `setNeutral` is not yet implemented but equivalent functionality is being planned.

#### Logging

Finally, this library provides some utilities for logging. Note that debug logging is hidden from the logs by default. This behavior can be toggled by enabling the [Step Debug Logs](../../docs/action-debugging.md#step-debug-logs).

```java
const myInput = Core.getInput("input");

try {
  Core.debug("Inside try block");
  
  if (!myInput) {
    Core.warning("myInput was not set");
  }
  
  if (Core.isDebug()) {
    // curl -v https://github.com
  } else {
    // curl https://github.com
  }

  // Do stuff
}
catch (Exception e) {
  Core.error("Error " + e.getMessage() + ", action may still succeed though");
}
```

This library can also wrap chunks of output in foldable groups.

```java
// Manually wrap output
Core.startGroup("Do some function");
doSomeFunction();
Core.endGroup();

// Wrap a runnable
Core.group('Do something', () -> {
  doSomeWrappedTask();
  doAnotherTask();
});

// Wrap a supplier
var result = Core.group('Do something', () -> {
  var response = doSomeHTTPRequest();
  return response;
});
```

#### Action state

You can use this library to save state and get state for sharing information between a given wrapper action: 

In some action:
```java
Core.saveState("someStateVariable", 12345);
```

In another action:
```java
var val = Core.getState("someStateVariable");
```

> It is unclear whether this functionality translates well to Docker-based actions.
