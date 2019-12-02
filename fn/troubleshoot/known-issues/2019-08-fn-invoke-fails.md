# `fn invoke` fails with the error message "kernel memory accounting disabled in this runc build"
Date Reported: 7/2/19  Issue: <https://github.com/fnproject/fn/issues/1520>

### Error messages
* CentOS: "kernel memory accounting disabled in this runc build"
* Oracle Linux: "API error (500): starting container process caused process_linux.go:402: container init caused process_linux.go:367: setting cgroup config for procHooks process caused kernel memory accounting disabled in this runc build: unknown" fn_id=01DHQC8C5YNG8G00GZJ0000002 idle_timeout=30 image="javafn:0.0.3" memory=128

**Reproduced with:**

* Docker:18.09(Git commit:e32a1bd)
* Fn Server:0.3.728(latest)
* Fn Client :0.5.84(latest)
* Oracle Linux 7.x

### Problem
CentOS 7.x enables kernel memory limit by default even though it's a experimental feature(not sure why), and RunC uses this feature without checking. This is causing a problem with Docker.

**See:**

* [Fn Issue](https://github.com/fnproject/fn/issues/1520)
* [Enabling kmem accounting can break applications on CentOS7 #1725](https://github.com/opencontainers/runc/issues/1725)
* [cgroups documenation](https://lwn.net/Articles/529927/)

### Workaround
There seems to be three options:

* Downgrade your system's containerd.io to containerd.io-1.2.2-3.3.el7 (a version not built with nokmem) and hope you don't run into the same issue.
* Don't run Fn server on a CentOS based Linux kernel 3.x system.
* Downgrade Docker to v18.0
