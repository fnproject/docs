# Building Custom Server with Extensions

You can easily add any number of extensions to Fn and then build your own custom image.

An example of fn w/ extensions may be found [here](https://github.com/fnproject/fn-ext-example).

Simply create an `ext.yaml` file with the extensions you want added:

```yaml
extensions:
  - name: github.com/treeder/fn-ext-example/logspam
  - name: github.com/treeder/fn-ext-example/logspam2
```

Build it:

```sh
fn build-server -t imageuser/imagename
```

`-t` takes the same input as `docker build -t`, tagging your image.

Now run your new server:

```sh
docker run --rm --name fnserver -it -v /var/run/docker.sock:/var/run/docker.sock -v $PWD/data:/app/data -p 8080:8080 imageuser/imagename
```

## datastores / mqs / drivers

Users that construct their own fn `main()` to build with extensions will need
to import any pieces they need to configure a datastore, mq and drivers for
the agent. `github.com/fnproject/fn/api/server/defaultexts` will import
everything in core and allowing configuring them at runtime. To get a smaller
binary, users may import only what they need. See the `defaultexts` file for
import paths to various pieces that may be configured. It's also possible to
use an entirely separate datastore, mq, or driver if a user wishes.
