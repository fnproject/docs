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
  
```

[Some link](#)

