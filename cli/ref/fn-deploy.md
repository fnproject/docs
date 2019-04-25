# `fn deploy`

```c
$ fn deploy

DEVELOPMENT COMMANDS
  fn deploy -   Deploys a function to the functions server (bumps, build, pushes and updates functions and/or triggers).
                
USAGE
  fn [global options] deploy [command options] [function-subdirectory] 
    
DESCRIPTION
  This command deploys one or all (--all) functions to the function server.
    
COMMAND OPTIONS
  --app value                     App name to deploy to
  --create-app                    Enable automatic creation of app if it doesn't exist during deploy
  --verbose, -v                   Verbose mode
  --no-cache                      Don't use Docker cache for the build
  --local, --skip-push            Do not push Docker built images onto Docker Hub - useful for local development.
  --registry --registry username  Set the Docker owner for images and optionally the registry. This will be prefixed to your function name for pushing to Docker registries.  eg: --registry username will set your Docker Hub owner. `--registry registry.hub.docker.com/username` will set the registry and owner.
  --all app.yaml                  If in root directory containing app.yaml, this will deploy all functions
  --no-bump                       Do not bump the version, assuming external version management
  --build-arg value               Set build time variables
  --working-dir value, -w value   Specify the working directory to deploy a function, must be the full path.
  
```

[Some link](#)

