# Fn Function Development Kit for Java - Docs and Examples
[![CircleCI](https://circleci.com/gh/fnproject/fdk-java.svg?style=svg&circle-token=348bec5610c34421f6c436ab8f6a18e153cb1c01)](https://circleci.com/gh/fnproject/fdk-java)

This page provides links to docs and examples on how to use the Function Development Kit for Java (FDK for Java) to develop applications.

## Docs
* [Data Binding for function input and output](DataBinding.md)
* [Extending the data binding functionality](ExtendingDataBinding.md)
* [Function initialization and configuration](FunctionConfiguration.md)
* [Accessing HTTP information From functions](HTTPGatewayFunction.md)
* [Spring cloud functions with Fn](SpringCloudFunctionSupport.md)
* [Testing your functions](TestingFunctions.md)

## Examples
* [String reverse](examples/string-reverse/README.md): This function takes a string and returns the reverse of the string. The FDK for Java handles marshalling data into your
function without the function having any knowledge of the FDK API.
* [Regex query](examples/regex-query/README.md): This function takes a JSON object containing a `text` field
and a `regex` field and returns a JSON object with a list of matches in the `matches` field. It
demonstrates the builtin JSON support of the Fn Java wrapper (provided through Jackson) and how the
platform handles serialization of POJO return values.
* [QR Code gen](examples/qr-code/README.md): This function parses the query parameters of a GET request (through the `InputEvent` passed into the function) to generate a QR code. It demonstrates
the `InputEvent` and `OutputEvent` interfaces which provide low level access to data entering the Fn FDK for Java.
* [Asynchronous thumbnails generation](examples/async-thumbnails/README.md): This example showcases the Fn Flow asynchronous execution API, by creating a workflow that takes an image and asynchronously generates three thumbnails for it, then uploads them to an object storage.
* [Gradle build](examples/gradle-build/README.md): This shows how to use Gradle to build functions using the FDK for Java.

## Contribute to the FDK for Java
If wish to contribute to the FDK for Java development see our [Contributing to Fn Guide](https://github.com/fnproject/fn/tree/master/docs#for-contributors).

For details on the FDK for Java Development see the [FDK for Java Repo](https://github.com/fnproject/fdk-java).

Before you get started you will need the following things:

* The [Fn CLI](https://github.com/fnproject/cli) tool
* [Docker-ce 17.06+ installed locally](https://docs.docker.com/engine/installation/)

### Install the Fn CLI tool

To install the Fn CLI tool, just run the following:

```
curl -LSs https://raw.githubusercontent.com/fnproject/cli/master/install | sh
```

This will download a shell script and execute it. If the script asks for
a password, that is because it invokes sudo.

## Your first Function

### 1. Create your first Java Function:

```bash
$ mkdir hello-java-function && cd hello-java-function
$ fn init --runtime=java --name your_dockerhub_account/hello
Runtime: java
function boilerplate generated.
func.yaml created
```

This creates the boilerplate for a new Java Function based on Maven and Oracle
Java 9. The `pom.xml` includes a dependency on the latest version of the Fn
FDK for Java that is useful for developing your Java functions.

You can now import this project into your favourite IDE as normal.

### 2. Deep dive into your first Java Function:
We'll now take a look at what makes up our new Java Function. First, lets take
a look at the `func.yaml`:

```bash
$ cat func.yaml
name: your_dockerhub_account/hello
version: 0.0.1
runtime: java
cmd: com.example.fn.HelloFunction::handleRequest
```

The `cmd` field determines which method is called when your function is
invoked. In the generated Function, the `func.yaml` references
`com.example.fn.HelloFunction::handleRequest`. Your functions will likely live
in different classes, and this field should always point to the method to
execute, with the following syntax:

```text
cmd: <fully qualified class name>::<method name>
```

For more information about the fields in `func.yaml`, refer to the [Fn platform
documentation](../../fn/develop/func-file.md)
about it.

Let's also have a brief look at the source:
`src/main/java/com/example/fn/HelloFunction.java`:

```java
package com.example.fn;

public class HelloFunction {

    public String handleRequest(String input) {
        String name = (input == null || input.isEmpty()) ? "world"  : input;

        return "Hello, " + name + "!";
    }

}
```

The function takes some optional input and returns a greeting dependent on it.

### 3. Run your first Java Function:
You are now ready to run your Function locally using the Fn CLI tool.

```bash
$ fn build
Building image your_dockerhub_account/hello:0.0.1
Sending build context to Docker daemon  14.34kB
Step 1/11 : FROM fnproject/fn-java-fdk-build:jdk9-latest as build-stage
 ---> 5435658a63ac
Step 2/11 : WORKDIR /function
 ---> 37340c5aa451

...

Step 5/11 : RUN mvn package dependency:copy-dependencies -DincludeScope=runtime -DskipTests=true -Dmdep.prependGroupId=true -DoutputDirectory=target --fail-never
---> Running in 58b3b1397ba2
[INFO] Scanning for projects...
Downloading: https://repo.maven.apache.org/maven2/org/apache/maven/plugins/maven-compiler-plugin/3.3/maven-compiler-plugin-3.3.pom
Downloaded: https://repo.maven.apache.org/maven2/org/apache/maven/plugins/maven-compiler-plugin/3.3/maven-compiler-plugin-3.3.pom (11 kB at 21 kB/s)

...

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 2.228 s
[INFO] Finished at: 2017-06-27T12:06:59Z
[INFO] Final Memory: 18M/143M
[INFO] ------------------------------------------------------------------------

...

Function your_dockerhub_account/hello:0.0.1 built successfully.

$ fn run
Hello, world!
```

The next time you run this, it will execute much quicker as your dependencies
are cached. Try passing in some input this time:

```bash
$ echo -n "Universe" | fn run
...
Hello, Universe!
```

### 4. Testing your function
The Fn FDK for Java includes a testing library providing useful [JUnit
4](http://junit.org/junit4/) rules to test functions. Look at the test in
`src/test/java/com/example/fn/HelloFunctionTest.java`:

```java
package com.example.fn;

import com.fnproject.fn.testing.*;
import org.junit.*;

import static org.junit.Assert.*;

public class HelloFunctionTest {

    @Rule
    public final FnTestingRule testing = FnTestingRule.createDefault();

    @Test
    public void shouldReturnGreeting() {
        testing.givenEvent().enqueue();
        testing.thenRun(HelloFunction.class, "handleRequest");

        FnResult result = testing.getOnlyResult();
        assertEquals("Hello, world!", result.getBodyAsString());
    }

}
```

This test is very simple: it just enqueues an event with empty input and then
runs the function, checking its output. Under the hood, the `FnTestingRule` is
actually instantiating the same runtime wrapping function invocations, so that
during the test your function will be invoked in exactly the same way that it
would when deployed.

### 5. Run using HTTP and the local Fn server
The previous example used `fn run` to run a function directly via docker, you
can also  use the Fn server locally to test the deployment of your function and
the HTTP calls to your functions.

Open another terminal and start the Fn server:

```bash
$ fn start
```

Then in your original terminal create an app:

```bash
$ fn create app java-app
Successfully created app: java-app
```

Now deploy your Function using the `fn deploy` command. This will bump the
function's version up, rebuild it, and push the image to the Docker registry,
ready to be used in the function deployment. Finally it will create a route on
the local Fn server, corresponding to your function.

We are using the `--local` flag to tell fn to skip pushing the image anywhere
as we are just going to run this on our local fn server that we started with
`fn start` above.

```bash
$ fn deploy --app java-app --local
...
Bumped to version 0.0.2
Building image hello:0.0.2
Sending build context to Docker daemon  14.34kB

...

Successfully built bf2b7fa55520
Successfully tagged your_dockerhub_account/hello:0.0.2
Updating route /hello-java-function using image your_dockerhub_account/hello:0.0.2...
```

Call the Function via the Fn CLI:

```bash
$ fn call java-app /hello-java-function
Hello, world!
```

You can also call the Function via curl:

```bash
$ curl http://localhost:8080/r/java-app/hello-java-function
Hello, world!
```

### 6. Something more interesting
The Fn FDK for Java supports [flexible data binding](DataBinding.md)  to make
it easier for you to map function input and output data to Java objects.

Below is an example to of a Function that returns a POJO which will be
serialized to JSON using Jackson:

```java
package com.example.fn;

public class PojoFunction {

    public static class Greeting {
        public final String name;
        public final String salutation;

        public Greeting(String salutation, String name) {
            this.salutation = salutation;
            this.name = name;
        }
    }

    public Greeting greet(String name) {
        if (name == null || name.isEmpty())
            name = "World";

        return new Greeting("Hello", name);
    }

}
```

Update your `func.yaml` to reference the new method:

```yaml
cmd: com.example.fn.PojoFunction::greet
```

Now run your new function:

```bash
$ fn run
...
{"name":"World","salutation":"Hello"}

$ echo -n Michael | fn run
...
{"name":"Michael","salutation":"Hello"}
```

## 7. Where do I go from here?

Learn more about the Fn FDK for Java by reading the next tutorials in the series.
Also check out the examples in the [`examples` directory](examples) for some
functions demonstrating different features of the Fn FDK for Java.

### Configuring your function

If you want to set up the state of your function object before the function is
invoked, and to use external configuration variables that you can set up with
the Fn tool, have a look at the [Function
Configuration](FunctionConfiguration.md) tutorial.

### Input and output bindings

You have the option of taking more control of how serialization and
deserialization is performed by defining your own bindings.

See the [Data Binding](DataBinding.md) tutorial for other out-of-the-box
options and the [Extending Data Binding](ExtendingDataBinding.md) tutorial
for how to define and use your own bindings.

### Asynchronous workflows

Suppose you want to call out to some other function from yours - perhaps
a function written in a different language, or even one maintained by
a different team. Maybe you then want to do some processing on the result. Or
even have your function interact asynchronously with a completely different
system. Perhaps you also need to maintain some state for the duration of your
function, but you don't want to pay for execution time while you're waiting for
someone else to do their work.

If this sounds like you, then have a look at the [Fn Flow
quickstart](https://github.com/fnproject/fdk-java/blob/master/docs/FnFlowsUserGuide.md).

# Get help

   * Come over and chat to us on the [fnproject Slack](https://join.slack.com/t/fnproject/shared_invite/enQtMjIwNzc5MTE4ODg3LTdlYjE2YzU1MjAxODNhNGUzOGNhMmU2OTNhZmEwOTcxZDQxNGJiZmFiMzNiMTk0NjU2NTIxZGEyNjI0YmY4NTA).
   * Raise an issue in [our github](https://github.com/fnproject/fn-java-fdk/).

# Contributing

Please see "[For Contributors](https://github.com/fnproject/fn/tree/master/docs#for-contributors)".
