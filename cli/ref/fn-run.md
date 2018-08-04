# `fn run`

```yaml
$ fn run

DEVELOPMENT COMMANDS
  fn run -   Run a function locally
             
USAGE
  fn [global options] run [command options] [function-subdirectory] 
    
DESCRIPTION
  This command runs a function locally.
    
COMMAND OPTIONS
  --env value, -e value          Select environment variables to be sent to function
  --link value                   Select container links for the function
  --method value                 Http method for function
  --format default               Format to use. default and `http` (hot) formats currently supported.
  --runs runs                    For hot functions only, will call the function runs times in a row. (default: 0)
  --memory value                 RAM to allocate for function, Units: MB (default: 0)
  --no-cache                     Don't use Docker cache for the build
  --content-type value           The payload Content-Type for the function invocation.
  --build-arg value              Set build time variables
  --working-dir value, -w value  Specify the working directory to run a function, must be the full path.
  
```

[Some link](#)

