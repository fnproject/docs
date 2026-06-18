# `fn update function`

```c
$ fn update function

MANAGEMENT COMMAND
  fn update function - Update a function in application
    
USAGE
  fn [global options] update function [command options] <app-name> <function-name> 
    
DESCRIPTION
  This command updates a function in an application.
    
COMMAND OPTIONS
  --memory value, -m value            Memory in MiB (default: 0)
  --config value, -c value            Function configuration
  --timeout value                     Function timeout (eg. 30) (default: 0)
  --idle-timeout value                Function idle timeout (eg. 30) (default: 0)
  --annotation value                  Function annotation (can be specified multiple times)
  --image value                       Function image
  --tag value                         Freeform tag in key=value form (repeatable)
  --defined-tag value                 Defined tag in namespace.key=value form (repeatable)
  --remove-tag value                  Remove a freeform tag by key (repeatable)
  --remove-defined-tag value          Remove a defined tag by namespace.key (repeatable)
  --clear-tags                        Clear all freeform and defined tags
  --clear-freeform-tags               Clear all freeform tags
  --clear-defined-tags                Clear all defined tags
  --provisioned-concurrency value     Provisioned concurrency (none | constant:<count>)
  --detached-timeout value            Detached invocation timeout (e.g. 20m, 1h)
  --on-success value                  Success destination shorthand (<stream|queue|notifications>:<ocid>)
  --on-failure value                  Failure destination shorthand (<stream|queue|notifications>:<ocid>)
  --clear-on-success                  Clear detached success destination
  --clear-on-failure                  Clear detached failure destination
  --pbf value                         Pre-Built Function listing OCID source
  --from-json value                   Structured JSON input for command parameters
  --if-match value                    Apply optimistic concurrency control using the provided etag
  --wait-for-state value              Wait until resource reaches lifecycle state
  --max-wait-seconds value            Max waiter duration in seconds
  --wait-interval-seconds value       Waiter poll interval in seconds
  --trace-config value                OCI trace config JSON or file:// path
  --timeout-in-seconds value          OCI parity timeout option in seconds
  --detached-mode-timeout-in-seconds value  OCI parity detached timeout in seconds
  --success-destination value         OCI parity destination JSON or file:// path
  --failure-destination value         OCI parity destination JSON or file:// path
  
```

## Examples

Update provisioned concurrency:

```bash
fn update function my-app my-fn --provisioned-concurrency constant:10
```

Update long-running settings:

```bash
fn update function my-app my-fn \
  --detached-timeout 1h \
  --on-success stream:ocid1.stream.oc1..aaaa \
  --on-failure notifications:ocid1.onstopic.oc1..aaaa
```

Clear long-running destinations:

```bash
fn update function my-app my-fn --clear-on-success --clear-on-failure
```

Update from JSON:

```bash
fn update function my-app my-fn --from-json file:///absolute/path/fn-create.json
```

