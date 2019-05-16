# Function Config Vars

There are three ways to get configuration variables into a function, all of which will be available as environment variables.
These are ordered in order of preference, the later ones overriding the previous ones.

## (1) Application level configuration

```sh
fn config app myapp LOG_LEVEL debug
```


## (2) Function configuration from func.yaml

See [Function file](func-file.md) for more info.


## (3) Function level configuration

```sh
fn config function myapp nodefn LOG_LEVEL info
```
