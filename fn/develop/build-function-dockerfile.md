# Build Functions using Dockerfile
Fn uses Docker to deploy functions.  Have your ever wondered, where is the `Dockerfile` that builds my image?  Doesn't Docker need one?

The answer is yes, a `Dockerfile` is created and used during the function's build process. The Dockerfile is installed when the build process starts and deleted once the build is complete. But this begs the question, can I capture the Fn `Dockerfile`, tweak it, and use it to build my function?  The answer is again yes. 


## Capturing the Dockerfile
First, create a default Fn function which can be used to `build` or `deploy` our function.

(1) Create a "hello world" function and change into the function directory.

```sh
fn init --runtime python pyhello
cd pyhello
```

(2) Create an app so you can deploy your function.

```sh
fn create app pyapp
```

(3) Now, build your function with the verbose switch to see all the steps in the build process.

```sh
$ fn --verbose build
Building image fndemouser/pyhello:0.0.1 
FN_REGISTRY:  fndemouser
Current Context:  default
Sending build context to Docker daemon  6.144kB
Step 1/13 : FROM fnproject/python:3.6-dev as build-stage
 ---> cdf5a7155ecc
Step 2/13 : WORKDIR /function
 ---> Using cache
 ---> 1a7c6d16ae63
Step 3/13 : ADD requirements.txt /function/
 ---> Using cache
 ---> 8df0fd7a59dc
Step 4/13 : RUN pip3 install --target /python/  --no-cache --no-cache-dir -r requirements.txt &&			 rm -fr ~/.cache/pip /tmp* requirements.txt func.yaml Dockerfile .venv
 ---> Using cache
 ---> 165ef454bdfa
Step 5/13 : ADD . /function/
 ---> e6c8049eaec1
Step 6/13 : RUN rm -fr /function/.pip_cache
 ---> Running in d8b5784d8169
Removing intermediate container d8b5784d8169
 ---> 6bb78917832f
Step 7/13 : FROM fnproject/python:3.6
 ---> e33fde7116e2
Step 8/13 : WORKDIR /function
 ---> Using cache
 ---> d64051e4412d
Step 9/13 : COPY --from=build-stage /python /python
 ---> Using cache
 ---> da7edae08f48
Step 10/13 : COPY --from=build-stage /function /function
 ---> 6e1c7ccb069c
Step 11/13 : RUN chmod -R o+r /python /function
 ---> Running in 338003ba63a7
Removing intermediate container 338003ba63a7
 ---> fde0a3cc40ee
Step 12/13 : ENV PYTHONPATH=/function:/python
 ---> Running in 2cd7350ef0fd
Removing intermediate container 2cd7350ef0fd
 ---> 0f8310f595c6
Step 13/13 : ENTRYPOINT ["/python/bin/fdk", "/function/func.py", "handler"]
 ---> Running in 2bdb3d0fe39c
Removing intermediate container 2bdb3d0fe39c
 ---> d8a532969fda
Successfully built d8a532969fda
Successfully tagged fndemouser/pyhello:0.0.1

Function fndemouser/pyhello:0.0.1 built successfully.
```

If you have some familiarity with Dockerfiles, you should notice that each step looks pretty Docker-like.

(4) You can create a `Dockerfile.new` file using this command.

```sh
fn build --verbose | grep Step |> Dockerfile.new cut -d ' ' -f4-
```

**Note:** The command identifies each line with "Step" in it, then grabs the 4th field in the line and uses it to create `Dockerfile.new`.

The result, should be a `Dockerfile.new` that looks like this:

```dockerfile
FROM fnproject/python:3.6-dev as build-stage
WORKDIR /function
ADD requirements.txt /function/
RUN pip3 install --target /python/  --no-cache --no-cache-dir -r requirements.txt &&			 rm -fr ~/.cache/pip /tmp* requirements.txt func.yaml Dockerfile .venv
ADD . /function/
RUN rm -fr /function/.pip_cache
FROM fnproject/python:3.6
WORKDIR /function
COPY --from=build-stage /python /python
COPY --from=build-stage /function /function
RUN chmod -R o+r /python /function
ENV PYTHONPATH=/function:/python
ENTRYPOINT ["/python/bin/fdk", "/function/func.py", "handler"]
```
You have your `Dockerfile.new`, but you need to make a couple more tweaks before you can use it.

(5) To finish up, you must configure `func.yaml` file. Run the following commands.

```sh
cp Dockerfile.new Dockerfile
rm func.yaml
fn init
```
Running the commands makes `fn` recognize the `Dockerfile` and creates a new `func.yaml` file. Which looks like this:

```yaml
schema_version: 20180708
name: pyhello
version: 0.0.1
runtime: docker
```

## Deploying your Function with Dockerfile
You can now deploy and test your function using a `Dockerfile`.

```sh
$ fn --verbose deploy --app pyapp --local
$ fn invoke pyapp pyhello
{"message": "Hello World"}
```
