# Extending the data binding functionality

By following this step-by-step guide you will learn to configure custom handling for input and output types of a simple Java function running on the Fn platform.


## Overview

In the [Data Binding](DataBinding.md) tutorial you have seen how the raw data received and returned by the function is represented by [InputEvent](../api/src/main/java/com.fnproject.fn/api/InputEvent.java)s and [OutputEvent](../api/src/main/java/com.fnproject.fn/api/OutputEvent.java)s. The Fn FDK for Java provides out-of-the-box functionality to convert these to some simple types and POJOs, but you might want to customize the way your input and output data is marshalled from the HTTP request and to the HTTP response.

This is done through the *Coercion* abstractions.

An [InputCoercion](../api/src/main/java/com/fnproject/fn/api/InputCoercion.java) is used to process an [InputEvent](../api/src/main/java/com/fnproject/fn/api/InputEvent.java) and turn it into the custom type required by a user function parameter.

Similarly, an [OutputCoercion](../api/src/main/java/com/fnproject/fn/api/OutputCoercion.java) is the abstraction used to take the return value of the user function and create an [OutputEvent](../api/src/main/java/com/fnproject/fn/api/OutputEvent.java) from it.

To make use of a custom coercion, you have to write a class that implements the appropriate interface, and then instruct your function to use it. This tutorial will explain how to do this.

First of all, let's create a new function project. If you haven't done it already, start a local Fn server and create an app:

```shell
$ fn start &
$ fn create app java-app
Successfully created app:  java-app
```

Then create a new project for the custom I/O tutorial.

```shell
$ mkdir custom-io && cd custom-io
$ fn init --runtime=java your_dockerhub_account/custom-io
Runtime: java
function boilerplate generated.
func.yaml created
$ mv src/main/java/com.example.fn/HelloFunction.java src/main/java/com.example.fn/CustomIO.java
```

Again, you will have to provide your Docker Hub account name.

Set the name, path and cmd accordingly in `func.yaml`:

```
name: your_dockerhub_account/custom-io
version: 0.0.1
runtime: java
cmd: com.example.fn.CustomIO::handleRequest
path: /custom-io
```

Edit `CustomIO.java` and create a function that merely echoes the input to the output:

```java
package com.example.fn;

public class CustomIO {
    public String handleRequest(String input) {
        return input;
    }
}
```

Then build and deploy the function, and test it with `curl`:

```shell
$ fn deploy java-app
...
Updating route /custom-io using image your_dockerhub_account/custom-io:0.0.2...

$ curl -X POST --data-ascii "ABCDE" -H "Content-type: text/plain" http://localhost:8080/r/java-app/custom-io
ABCDE
```

## Creating custom coercions

Coercion classes must have a zero-arguments public constructor.

Input coercions must implement the `tryCoerceParam` method, while output coercions must implement the `wrapFunctionResult` method.

Both these methods return a `java.util.Optional`. The contract for this return value is as follows:

- If an empty `Optional` is returned, the coercion is not the appropriate coercion to be used for the provided data, and the runtime is allowed to attempt to use another coercion.
- If the `Optional` contains an object, the coercion has successfully performed the required conversion.
- If a `RuntimeException` is thrown, the coercion was supposed to work with the provided data but some kind of error has occurred. Because the data may have been partially consumed it is not safe to continue so the function invocation will be completed unsuccessfully and no further action will be attempted.

A common pattern for coercions is to first check that they can process the provided data into the required type, and return an empty `Optional` if they cannot. Then they can try to perform the conversion and wrap any exception (for example I/O exceptions) into an input/output handling exception.

### Creating an input coercion

We will write our first input coercion, which takes the body of the HTTP request and converts it to a String, but then reverses the order of the characters in the string.

We're going to use an Apache Commons library so let's add it to our `pom.xml`:

```
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-io</artifactId>
    <version>1.3.2</version>
</dependency>
```

Create and edit `src/main/java/com/example/fn/ReverseStringInputCoercion.java`:

```java
package com.example.fn;

import com.fnproject.fn.api.InvocationContext;
import com.fnproject.fn.api.InputCoercion;
import com.fnproject.fn.api.InputEvent;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class ReverseStringInputCoercion implements InputCoercion<String> {

    @Override
    public Optional<String> tryCoerceParam(InvocationContext currentContext, int arg, InputEvent input) {
        if (!currentContext.getRuntimeContext().getTargetMethod().getParameterTypes()[arg].equals(String.class)) {
            return Optional.empty();
        }
        Optional<String> ct = input.getHeaders().get("Content-type");
        if (!ct.isPresent() || !ct.get().toLowerCase().startsWith("text/plain")) {
            return Optional.empty();
        }
        String reversedString = input.consumeBody(is -> {
            try {
                return new StringBuilder(IOUtils.toString(is, StandardCharsets.UTF_8.toString())).reverse().toString();
            } catch (IOException e) {
                throw new IllegalArgumentException("Error reading input as string", e);
            }
        });
        return Optional.of(reversedString);
    }
}
```

Input coercions receive three parameters to the `tryCoerceParam` method:

- The [InvocationContext](../api/src/main/java/com.fnproject.fn/api/InvocationContext.java) representing a description of the current invocation of the user function.
- An `int` which represents the index of the function parameter currently being considered for binding the data.
- The `InputEvent` representing the data coming from the HTTP request.

First of all, our new coercion examines the type of the function parameter currently being considered for data binding. It is possible to get the function `Method` data via `currentContext.getRuntimeContext().getTargetMethod()`, at which point the Java reflection API can be used to check the type of the parameter, using `arg` as the index of the parameter.

If the required type is not a `String`, this is not the correct coercion to use for the parameter, and therefore the coercion exits early by returning an empty `Optional`.

In addition to this, the coercion only handles input data with a `text/plain` content type, therefore it also exits early if this is the not the case.

Then, the coercion attempts to consume the body of the `InputEvent` and reverse the string. A `RuntimeException` may be thrown.

Finally, a valid `Optional` is returned.

### Using an input coercion

To apply a specific coercion to a given input parameter, you can use an [@InputBinding](../api/src/main/java/com.fnproject.fn/api/InputBinding.java) annotation with the `coercion` parameter to specify the coercion class.

Edit `CustomIO.java` and add this annotation:

```java
package com.example.fn;

import com.fnproject.fn.api.InputBinding;

public class CustomIO {
    public String handleRequest(@InputBinding(coercion=ReverseStringInputCoercion.class) String input) {
        return input;
    }
}
```

Now redeploy the function and test it with `curl`:

```shell
$ fn deploy java-app
...
Updating route /custom-io using image your_dockerhub_account/custom-io:0.0.3...

$ curl -X POST --data-ascii "ABCDE" -H "Content-type: text/plain" http://localhost:8080/r/java-app/custom-io
EDCBA
```

The input was reversed!

### Creating an output coercion

We will now write an output coercion, which reverses the string result again before returning it as the body of the HTTP response.

Create and edit `src/main/java/com.example.fn/ReverseStringOutputCoercion.java`:

```java
package com.example.fn;

import com.fnproject.fn.api.InvocationContext;
import com.fnproject.fn.api.OutputCoercion;
import com.fnproject.fn.api.OutputEvent;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class ReverseStringOutputCoercion implements OutputCoercion {
    @Override
    public Optional<OutputEvent> wrapFunctionResult(InvocationContext ctx, Object output) {
        if (!ctx.getRuntimeContext().getTargetMethod().getReturnType().equals(String.class)) {
            return Optional.empty();
        }
        OutputEvent event = OutputEvent.fromBytes(
                new StringBuilder((String) output).reverse().toString().getBytes(),
                true,
                "text/plain"));
        return Optional.of(event);
    }
}
```

Output coercions receive two parameters to the `wrapFunctionResult` method:

- The [InvocationContext](../api/src/main/java/com.fnproject.fn/api/InvocationContext.java) representing a description of the current invocation of the user function.
- An `Object` which is actually the return value of the user function.

First of all, our output coercion examines the type of the function return value. Similarly to the input coercion seen above, it gets the function `Method` data via `currentContext.getRuntimeContext().getTargetMethod()`, at which point the Java reflection API can be used to check the type of the return value.

If the type is not a `String`, this is not the correct coercion to use for the return value, and therefore the coercion exits early by returning an empty `Optional`.

Otherwise, the coercion now knows that the `Object` it received must actually be a `String`, so it can cast it, reverse it, and construct an `OutputEvent` from its bytes.

Finally, a valid `Optional` is returned.

### Using an output coercion

To apply a specific coercion from the return type of the function, you can provide an [@OutputBinding](../api/src/main/java/com.fnproject.fn/api/OutputBinding.java) annotation, which (similarly to the input one) has a `coercion` parameter. This annotation is attached to your function method.

Let's edit `CustomIO.java` again:

```java
package com.example.fn;

import com.fnproject.fn.api.InputBinding;
import com.fnproject.fn.api.OutputBinding;

public class CustomIO {
    @OutputBinding(coercion=ReverseStringOutputCoercion.class)
    public String handleRequest(@InputBinding(coercion=ReverseStringInputCoercion.class) String input) {
        return input;
    }
}
```

The input and output coercion should neutralize each other, therefore the combination of the two functionalities should revert back to being a simple echo of the input. Let's test this:

```shell
$ fn deploy java-app
...
Updating route /custom-io using image your_dockerhub_account/custom-io:0.0.4...

$ curl -X POST --data-ascii "ABCDE" -H "Content-type: text/plain" http://localhost:8080/r/java-app/custom-io
ABCDE
```


## Using a function configuration method to specify custom coercions

In the [Function Configuration](FunctionConfiguration.md) tutorial, you have seen how to decorate your function classes with a configuration method that can be used to initialize their state from configuration data.

That same method can be used to specify input and output coercions by using the API provided in the [RuntimeContext](../api/src/main/java/com.fnproject.fn/api/RuntimeContext.java).

Let's edit our `CustomIO.java` again:

```java
package com.example.fn;

import com.fnproject.fn.api.FnConfiguration;
import com.fnproject.fn.api.RuntimeContext;

public class CustomIO {
    @FnConfiguration
    public void configure(RuntimeContext ctx){
        ctx.addInputCoercion(new ReverseStringInputCoercion());
        ctx.addOutputCoercion(new ReverseStringOutputCoercion());
    }

    public String handleRequest(String input) {
        return input;
    }
}
```

We have removed the input and output binding annotations, but we have provided our custom coercions in the configuration method.

Let's test that this new code has the same effect (a double reversal, i.e. a simple echo).

```shell
$ fn deploy java-app
...
Updating route /custom-io using image your_dockerhub_account/custom-io:0.0.5...

$ curl -X POST --data-ascii "ABCDE" -H "Content-type: text/plain" http://localhost:8080/r/java-app/custom-io
ABCDE
```

### What's the difference?

There is a difference in using the function configuration method to specify coercions compared to the annotations.

When directly annotating the function method with the [@InputBinding](../api/src/main/java/com.fnproject.fn/api/InputBinding.java) and [@OutputBinding](../api/src/main/java/com.fnproject.fn/api/OutputBinding.java) annotations, _only the specified coercion will be tried_.

In other words, the annotations are an explicit requirement on the input and output conversions to perform.

When using the function configuration method, the provided coercions _will be tried in order_ and _before any builtin coercion is tried_. As soon as one of the provided coercions is successful, no other coercions will be tried.

In other words, the additional coercions specified in the function configuration methods form a priority list but they are not exclusive. The runtime will:

- Try the first coercion provided in the configuration method.
  - If it returns a full `Optional`, it successfully converted the input and no other coercion is tried.
  - Otherwise, the runtime will proceed to the next coercion.
- Try the second coercion provided in the configuration method...
- ...
- Only if no custom coercion has succeeded so far, try the builtin coercions (to `String`, `byte[]` and to a POJO).
