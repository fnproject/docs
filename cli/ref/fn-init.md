# `fn init`

```c
$ fn init

DEVELOPMENT COMMANDS
  fn init -   Create a local func.yaml file
              
USAGE
  fn [global options] init [command options] [function-subdirectory] 
    
DESCRIPTION
  This command creates a func.yaml file in the current directory.
    
COMMAND OPTIONS
  --name value                   Name of the function. Defaults to directory name in lowercase.
  --force                        Overwrite existing func.yaml
  --runtime value                Choose an existing runtime - go, java, java11, java8, kotlin, node, python, python3.6, python3.7.1, ruby
  --init-image value             A Docker image which will create a function template
  --entrypoint value             Entrypoint is the command to run to start this function - equivalent to Dockerfile ENTRYPOINT.
  --cmd value                    Command to run to start this function - equivalent to Dockerfile CMD.
  --version value                Set initial function version (default: "0.0.1")
  --working-dir value, -w value  Specify the working directory to initialise a function, must be the full path.
  --trigger value                Specify the trigger type - permitted values are 'http'.
  --memory value, -m value       Memory in MiB (default: 0)
  --config value, -c value       Function configuration
  --timeout value                Function timeout (eg. 30) (default: 0)
  --idle-timeout value           Function idle timeout (eg. 30) (default: 0)
  --annotation value             Function annotation (can be specified multiple times)
  --tag value                    Freeform tag in key=value form (repeatable)
  --defined-tag value            Defined tag in namespace.key=value form (repeatable)
  --provisioned-concurrency value  Provisioned concurrency (none | constant:<count>)
  --detached-timeout value       Detached invocation timeout (e.g. 20m, 1h)
  --on-success value             Success destination shorthand (<stream|queue|notifications>:<ocid>)
  --on-failure value             Failure destination shorthand (<stream|queue|notifications>:<ocid>)
  --pbf value                    Initialize function from a Pre-Built Function listing OCID
  
```

## Examples

Initialize with provisioned concurrency:

```bash
fn init --runtime go hello-pc --provisioned-concurrency constant:5
```

Initialize with long-running settings:

```bash
fn init --runtime java hello-long \
  --detached-timeout 20m \
  --on-success stream:ocid1.stream.oc1..aaaa \
  --on-failure notifications:ocid1.onstopic.oc1..aaaa
```

Initialize from PBF:

```bash
fn init --name hello-pbf --pbf ocid1.pbflisting.oc1..aaaa
```

