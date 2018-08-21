# Create an init-image for Fn
This how-to shows how to create an init-image for Fn

## What is an init-image?

An init-image can be used with `fn init --init-image=<init_image_name>` to create a template for a function. Init-images
can be used to create a HelloWorld function showing how to use a particular language, or for more complex
templating.

The requirements for an init-image are:

  - it will be run with `docker run -e FN_FUNCTION_NAME=<function_name> init_image_name`
  - the output will be treated as a [tarball](https://en.wikipedia.org/wiki/Tar_(computing))
  - the files in the tarball should form a valid function which can be run using `fn run`, with the exception of `func.yaml`
  - the output _should not_ contain a `func.yaml` file
  - the output _must_ contain a file called `func.init.yaml` which has a subset of the keys from a `func.yaml`
  - the `func.init.yaml` _must_ contain the following keys: `runtime`
  - the `func.init.yaml` _may_ also contain the following keys: `build`, `buildImage`, `cmd`, `content_type`, `entrypoint`, `expects`, `format`, `headers`, `runImage`, and `tests`


The CLI will:
  - put the files in the archive on disk, owned by the current user and with correct permissions
  - merge the content of `func.init.yaml` with other properties such as `name` and `version` and create a `func.yaml`
  - remove the left-over `func.init.yaml`

## Creating an example init-image

The easiest way to create an init-image is to base it on an existing function

### A simple function

We'll use a really simple function as our example, one which we could create by hand that uses the string-reversing tool called `rev` that is bundled in Alpine Linux:

```
$ ls
Dockerfile  func.yaml
```

```
$ cat Dockerfile
FROM alpine:latest
CMD ["rev"]
```

```
$ cat func.yaml
name: reverser
version: 0.0.1
runtime: docker
```

```
$ echo HELLO | fn run
Building image blah:0.0.1
OLLEH
```

### Creating the init-image

Firstly we need to create `func.init.yaml`.  It'll be the same as the existing `func.yaml` but without `name` and `version` (these are provided by the CLI when the init-image is used.

```
$ cat func.init.yaml
runtime: docker
```

We need to create a tarball of the files we want the init-image to create:

```
$ tar -cf init.tar Dockerfile func.init.yaml
```

There is no need to use any compression here as Docker does that for us.

We'll be creating the init-image here, so we need another Dockerfile to do that:

```
$ cat Dockerfile-init-image
FROM alpine:latest
COPY init.tar /
CMD ["cat", "/init.tar"]
```

Now we can create our init-image:

```
$ docker build -t reverser-init -f Dockerfile-init-image .

Sending build context to Docker daemon  15.36kB
Step 1/3 : FROM alpine:latest
 ---> 3fd9065eaf02
Step 2/3 : COPY init.tar /
 ---> 03ad6bc830c3
Step 3/3 : CMD cat /init.tar
 ---> Running in b8dad41e966f
 ---> decb5e898b1d
Removing intermediate container b8dad41e966f
Successfully built decb5e898b1d
Successfully tagged reverser-init:latest
```

And we are done.

### Testing out the init-image

Move into an empty directory, and we can create a function from the init-image:

```
$ mkdir new && cd new
$ fn init --init-image=reverser-init
Building from init-image: reverser-init
func.yaml created.
```

```
$ echo "YES MATE" | fn run
Building image new:0.0.1 
ETAM SEY
```

