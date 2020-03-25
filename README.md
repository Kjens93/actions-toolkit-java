
<p align="center">
  <img alt="Actions Toolkit" src="https://github.com/actions/toolkit/raw/master/res/at-logo.png">
  <img alt="Maven Central" src="https://img.shields.io/maven-central/v/com.github.kjens93/actions-toolkit-java">
</p>

<p align="center">
  <a href="https://github.com/Kjens93/actions-toolkit-java/actions"><img alt="GitHub Actions status" src="https://github.com/Kjens93/actions-toolkit-java/workflows/toolkit-unit-tests/badge.svg"></a>
</p>


## Actions Toolkit for Java

The GitHub Actions ToolKit for Java provides a set of packages to make creating actions easier. This software is not endorsed, distributed, or maintained by GitHub. This library is an unofficial Java port of [GitHub Actions Toolkit](https://github.com/actions/toolkit), which was originally developed exclusively for JavaScript.

## Getting Started

Java actions are not officially supported by GitHub. As such, you will need to use a Docker action to run your action code. 

The toolkit is available from Maven Central.

## Packages

:heavy_check_mark: [actions-toolkit-core](actions-toolkit-core) (Complete!)

Provides functions for inputs, outputs, results, logging, secrets and variables. Read more [here](packages/core)

```xml
<dependency>
  <groupId>com.github.kjens93.actions.toolkit</groupId>
  <artifactId>actions-toolkit-core</artifactId>
  <version>${actions-toolkit.version}</version>
</dependency>
```
<br/>

:runner: [actions-toolkit-exec](actions-toolkit-exec) (not started)

Provides functions to exec cli tools and process output. Read more [here](actions-toolkit-exec)
<!--
```xml
<dependency>
  <groupId>com.github.kjens93.actions-toolkit-java</groupId>
  <artifactId>actions-toolkit-exec</artifactId>
  <version>${actions-toolkit.version}</version>
</dependency>
```
-->
<br/>

:ice_cream: [actions-toolkit-glob](actions-toolkit-glob) (not started)

Provides functions to search for files matching glob patterns. Read more [here](actions-toolkit-glob)
<!--
```xml
<dependency>
  <groupId>com.github.kjens93.actions-toolkit-java</groupId>
  <artifactId>actions-toolkit-glob</artifactId>
  <version>${actions-toolkit.version}</version>
</dependency>
```
-->
<br/>

:pencil2: [actions-toolkit-io](actions-toolkit-io) (not started)

Provides disk i/o functions like cp, mv, rmRF, find etc. Read more [here](actions-toolkit-io)
<!--
```xml
<dependency>
  <groupId>com.github.kjens93.actions-toolkit-java</groupId>
  <artifactId>actions-toolkit-io</artifactId>
  <version>${actions-toolkit.version}</version>
</dependency>
```
-->
<br/>

:hammer: [actions-toolkit-cache](actions-toolkit-cache) (not started)

Provides functions for downloading and caching tools.  e.g. setup-* actions. Read more [here](actions-toolkit-cache)
<!--
```xml
<dependency>
  <groupId>com.github.kjens93.actions-toolkit-java</groupId>
  <artifactId>actions-toolkit-cache</artifactId>
  <version>${actions-toolkit.version}</version>
</dependency>
```
-->
<br/>

:octocat: [actions-toolkit-github](actions-toolkit-github) (not started)

Provides a GitHub client hydrated with the context that the current action is being run in. Read more [here](actions-toolkit-github)
<!--
```xml
<dependency>
  <groupId>com.github.kjens93.actions-toolkit-java</groupId>
  <artifactId>actions-toolkit-github</artifactId>
  <version>${actions-toolkit.version}</version>
</dependency>
```
-->
<br/>

:floppy_disk: [actions-toolkit-artifact](actions-toolkit-artifact) (not started)

Provides functions to interact with actions artifacts. Read more [here](actions-toolkit-artifact)
<!--
```xml
<dependency>
  <groupId>com.github.kjens93.actions-toolkit-java</groupId>
  <artifactId>actions-toolkit-artifact</artifactId>
  <version>${actions-toolkit.version}</version>
</dependency>
```
-->
<br/>

## Creating an Action with the Toolkit

:question: [Choosing an action type](https://github.com/actions/toolkit/blob/master/docs/action-types.md)

Outlines the differences and why you would want to create a JavaScript or a container based action.
<br/>
<br/>

:curly_loop: [Versioning](https://github.com/actions/toolkit/blob/master/docs/action-versioning.md)

Actions are downloaded and run from the GitHub graph of repos.  This contains guidance for versioning actions and safe releases.
<br/>
<br/>

:warning: [Problem Matchers](https://github.com/actions/toolkit/blob/master/docs/problem-matchers.md)

Problem Matchers are a way to scan the output of actions for a specified regex pattern and surface that information prominently in the UI.
<br/>
<br/>

:warning: [Proxy Server Support](https://github.com/actions/toolkit/blob/master/docs/proxy-support.md)

Self-hosted runners can be configured to run behind proxy servers. 
<br/>
<br/>

## Contributing

Contributions are welcome.  See [how to contribute](.github/CONTRIBUTING.md).
