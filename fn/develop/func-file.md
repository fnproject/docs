# Func files

A func file contains metadata that help the fn project when you create a function.

Example of a func file:

```yaml
schema_version: 20180708
name: fnproject/hello
version: 0.0.1
runtime: java
build_image: x/y
run_image: x/y
cmd: com.example.fn.HelloFunction::handleRequest
memory: 128
timeout: 30
config:
  key: value
  key2: value2
  keyN: valueN
expects:
  config:
    - name: SECRET_1
      required: true
    - name: SECRET_2
      required: false
triggers:
  - name: triggerOne
    type: http
    source: /trigger-path
```
**schema_version:** Represents the version of the specification for this file.

**name:** Unique name for this function.

**version:** Represents the current version of the function. After deploying, it is appended to the image as a tag.

**runtime:** Represents programming language runtime, for example,
'go', 'python', 'java', etc.  The runtime 'docker' will use the existing Dockerfile if one exists. Current valid values for runtime as of v0.5.86 are: go, java8, java9, java, java11, node, python, python3.6, python, python3.7, ruby, and kotlin.

**build_image:** (Optional) Base Docker image used for building your function. Default images used are the `dev` tagged images from the [dockers repo](https://github.com/fnproject/dockers).

**run_image:** (Optional) Base Docker image used for running your function, as part of a multi-stage build. Function will be built with `build_image` and run with `run_image`. Default images used from the [dockers repo](https://github.com/fnproject/dockers).

**cmd:** (Optional) Execution command for jvm based runtimes.

**entrypoint:** (Optional) Execution entry point for native runtimes.

**memory:** (Optional) Maximum memory threshold for this
function. If this function exceeds this limit during execution, it is stopped
and error message is logged. Set this value in multiples of 64. The maximum memory value allowed is 8 (GB) and the default value is 128 (MB).

**timeout:** (Optional) Maximum runtime allowed for this function in seconds. The maximum value is 300 and the default values is 30.

**config:** (Optional) Set of configuration variables pass onto the function as environment variables.
These configuration options override application configuration during functions execution. See [Configuration](configs.md)
for more information.

**expects:** (Optional) List of configuration environment variables required to run this function. These variables are used when you run or test locally. If these variables are not found in your local environment, then your local testing fails.

**triggers:** (Optional) Array of `trigger` entities that specific triggers for the function. See [Trigger](triggers.md) for more information.
