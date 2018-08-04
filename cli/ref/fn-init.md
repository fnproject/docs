# `fn init`

```yaml
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
  --runtime value                Choose an existing runtime - dotnet, go, java8, java9, java, lambda-nodejs4.3, lambda-node-4, node, php, python, python3.6, ruby, rust, kotlin
  --entrypoint value             Entrypoint is the command to run to start this function - equivalent to Dockerfile ENTRYPOINT.
  --cmd value                    Command to run to start this function - equivalent to Dockerfile CMD.
  --version value                Set initial function version (default: "0.0.1")
  --working-dir value, -w value  Specify the working directory to initialise a function, must be the full path.
  --trigger value                Specify the trigger type.
  --memory value, -m value       Memory in MiB (default: 0)
  --type value, -t value         Route type - sync or async
  --config value, -c value       Route configuration
  --headers value                Route response headers
  --format value, -f value       Hot container IO format - default or http
  --timeout value                Route timeout (eg. 30) (default: 0)
  --idle-timeout value           Route idle timeout (eg. 30) (default: 0)
  --annotation value             Route annotation (can be specified multiple times)
  
```

[Some link](#)

