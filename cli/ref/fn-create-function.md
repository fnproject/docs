# `fn create function`

```yaml
$ fn create function

MANAGEMENT COMMAND
  fn create function - Create a function within an application
    
USAGE
  fn [global options] create function [command options] <app-name> <function-name> <image> 
    
DESCRIPTION
  This command creates a new function within an application.
    
COMMAND OPTIONS
  --memory value, -m value  memory in MiB (default: 0)
  --config value, -c value  fn configuration
  --format value, -f value  hot container IO format - can be one of: default, http, json or cloudevent (check FDK docs to see which are supported for the FDK in use.)
  --timeout value           fn timeout (eg. 30) (default: 0)
  --idle-timeout value      fn idle timeout (eg. 30) (default: 0)
  --annotation value        fn annotation (can be specified multiple times)
  
```

[Some link](#)

