# `fn create function`

```c
$ fn create function

MANAGEMENT COMMAND
  fn create function - Create a function within an application
    
USAGE
  fn [global options] create function [command options] <app-name> <function-name> <image> 
    
DESCRIPTION
  This command creates a new function within an application.
    
COMMAND OPTIONS
  --memory value, -m value  Memory in MiB (default: 0)
  --config value, -c value  Function configuration
  --timeout value           Function timeout (eg. 30) (default: 0)
  --idle-timeout value      Function idle timeout (eg. 30) (default: 0)
  --annotation value        Function annotation (can be specified multiple times)
  --image value             Function image
  --provisioned-concurrency value  Provisioned concurrency (none | constant:<count>)
  --detached-timeout value         Detached invocation timeout (e.g. 20m, 1h)
  --on-success value               Success destination shorthand (<stream|queue|notifications>:<ocid>)
  --on-failure value               Failure destination shorthand (<stream|queue|notifications>:<ocid>)
  --tag value                      Freeform tag in key=value format (repeatable)
  --defined-tag value              Defined tag in namespace.key=value format (repeatable)
  --pbf value                      Pre-Built Function listing OCID source
  --trace-config value             OCI trace config JSON or file:// path
  --timeout-in-seconds value       OCI parity timeout option in seconds
  --detached-mode-timeout-in-seconds value  OCI parity detached timeout in seconds
  --success-destination value      OCI parity destination JSON or file:// path
  --failure-destination value      OCI parity destination JSON or file:// path
  --from-json value                Structured JSON input for command parameters
  --wait-for-state value           Wait until resource reaches lifecycle state
  --max-wait-seconds value         Max waiter duration in seconds
  --wait-interval-seconds value    Waiter poll interval in seconds
  
```

## Examples

Create function with provisioned concurrency:

```bash
fn create function my-app my-fn myrepo/myimg:0.0.1 \
  --provisioned-concurrency constant:40
```

Create long-running function with destinations:

```bash
fn create function my-app my-detached-fn myrepo/myimg:0.0.1 \
  --detached-timeout 20m \
  --on-success stream:ocid1.stream.oc1..aaaa \
  --on-failure notifications:ocid1.onstopic.oc1..aaaa
```

Create function from PBF:

```bash
fn create function my-app my-pbf-fn --pbf ocid1.pbflisting.oc1..aaaa
```

Create function from JSON input:

```bash
fn create function my-app my-json-fn --from-json file:///absolute/path/fn-create.json
```

