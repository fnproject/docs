# Using Fn with Podman instead of Docker

By default, Fn Project assumes the use of Docker to build and deploy function images. However, Fn Project now supports Podman as an alternative to Docker. When using Fn CLI version 0.6.12 and above, you can specify a configuration setting to use Podman instead of Docker.

Having installed the Fn CLI ([Install Fn](https://fnproject.io/tutorials/install/)), specify that you want to use Podman as follows:

1. Install Podman (version 3.4 or later) and add Podman to the system path. See [Podman Installation Instructions](https://podman.io/getting-started/installation).

2. Add the `container-enginetype` configuration setting to the `~/.fn/config.yaml` file as follows:

```yaml
container-enginetype: podman
```

If you subsequently want to use Docker rather than Podman, do either of the following:

* Remove the `container-enginetype` configuration setting from the `~/.fn/config.yaml` file.
* Update the `container-enginetype` configuration setting in the `~/.fn/config.yaml` file to specify Docker rather than Podman, as follows:

```yaml
container-enginetype: docker
```

**Note:** If Podman is chosen as the `container-enginetype`, Fn server does not work. You are not able to deploy your functions to a local Fn server.
