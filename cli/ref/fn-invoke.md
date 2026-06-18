# `fn invoke`

```c
$ fn invoke

DEVELOPMENT COMMANDS
  fn invoke -   Invoke a remote function
                
USAGE
  fn [global options] invoke [command options] [app-name] [function-name] 
    
DESCRIPTION
  This command invokes a function. Users may send input to their function by passing input to this command via STDIN.
    
COMMAND OPTIONS
  --endpoint value        Specify the function invoke endpoint for this function, the app-name and func-name parameters will be ignored
  --content-type value    The payload Content-Type for the function invocation.
  --display-call-id       whether display call ID or not
  --output value          Output format (json)
  --fn-intent value       Optional intent header for function invocation, e.g. httprequest or cloudevent
  --is-dry-run            Send invocation as a dry run without executing the function
  --fn-invoke-type value  Invoke type for Oracle Functions: sync or detached
  
```

## Examples

Invoke in detached mode (subcommand):

```bash
fn invoke detached my-app my-fn --display-call-id
```

Invoke with OCI parity detached flag:

```bash
fn invoke my-app my-fn --fn-invoke-type detached --display-call-id
```

Invoke dry-run with intent:

```bash
fn invoke my-app my-fn --is-dry-run --fn-intent cloudevent
```

