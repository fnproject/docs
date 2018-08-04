# `fn create route`

```yaml
$ fn create route

MANAGEMENT COMMAND
  fn create route - Create a route in an application
    
USAGE
  fn [global options] create route [command options] <app-name> </path> <image> 
    
DESCRIPTION
  This command creates a new route within an application.
    
COMMAND OPTIONS
  --memory value, -m value  Memory in MiB (default: 0)
  --type value, -t value    Route type - sync or async
  --config value, -c value  Route configuration
  --headers value           Route response headers
  --format value, -f value  Hot container IO format - default or http
  --timeout value           Route timeout (eg. 30) (default: 0)
  --idle-timeout value      Route idle timeout (eg. 30) (default: 0)
  --annotation value        Route annotation (can be specified multiple times)
  
```

[Some link](#)

