# Data Binding for function input and output
The Fn FDK for Java provides some standard tools for handling binding input to your functions to Java objects and types - You can also configure your own data binding either per-function or publish function data  binding libraries, see [Extending Data Binding](ExtendingDataBinding.md) for details of how to do this.


## Simple data binding

Fn functions are invoked from a HTTP request whose body can provide input to the function.

The Fn FDK for Java includes functionality to convert the data provided in the body of the HTTP request into frequently used types directly.

Add a string parameter to your function to receive the HTTP body of the function call as a string and return a string value to set the body of the HTTP response:

```java
package com.example.fn;

public class HelloFunction {
    public String handleRequest(String input) {
        String name = (input == null || input.isEmpty()) ? "world"  : input;
        return "Hello, " + name + "!";
    }
}
```

Similarly, you can send and receive `byte[]`s , and have functions which do not return data with  a `void` return type.

Details of how these basic builtin types are processed are defined as follows.

### Input (function parameter)

| Java Type   | Accepted Input Content Types | Interpretation |
| ------      | ---------------------------- | -------------- |
| `String`    | Any                          | The request body is parsed as a UTF-8 String |
| `byte[]`    | Any                          | The request body is provided as a raw `byte` Array |

### Output (function return value)

| Java Type   | Output content type      | Interpretation |
| ------      | ------------------------ | -------------- |
| `String`    | text/plain               | The string is serialized as bytes (with `.getBytes()`) |
| `byte[]`    | application/octet-stream | The raw bytes are used as the body of the response |
| `void`      | None                     | The response has no content |


## JSON data binding

The FDK supports binding JSON data types to POJOs. This is implemented using the Jackson API.

If the function has a POJO parameter, the function will only accept HTTP requests with an `application/json` content type and it will use the default Jackson bindings to deserialize the body of the request into the POJO.

Similarly, if the function has a POJO return value it will produce an HTTP response with the `application/json` content type and it will use the default Jackson bindings to serialize the returned POJO into the body of the response.

The following function returns `Greeting` as a JSON object containing a salutation and a name using the default jackson object binding:

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

## Customizing the Jackson object mapper

You can customize the Jackson ObjectMapper used by your function via a [function configuration method](FunctionConfiguration.md):

```java
package com.example.fn;

import com.fnproject.fn.api.FnConfiguration;
import com.fnproject.fn.api.RuntimeContext;

public class TestFn {

    @FnConfiguration
    void configureObjectMapper(RuntimeContext ctx){
         ObjectMapper om = new ObjectMapper();
         om.addMixin(JsonBean.class,JsonBeanMixin.class);

         ctx.setAttribute("com.fnproject.fn.runtime.coercion.jackson.JacksonCoercion.om",om);

    }

    public static class JsonBean {
        public String paramA;
        public int paramB;
    }
    public JsonBean testFn(JsonBean req){
        return req;
    }
}
```

## Working with raw function events
To get the most flexibility in handling data in and out of your function, the FDK also provides an abstraction of the raw Fn FDK for Java events received or returned by the function by means of the [InputEvent](../api/src/main/java/com/fnproject/fn/api/InputEvent.java) and [OutputEvent](../api/src/main/java/com/fnproject/fn/api/OutputEvent.java) interfaces.

Using this approach allows you to:

- access the headers of the incoming request;
- read the request body as an `InputStream`;
- control content type (and other headers) and status of the response.

The Fn FDK for Java can automatically convert the input HTTP request into an `InputEvent` and provide it as the parameter of the function. Similarly, it can take the `OutputEvent` returned by the function and construct an appropriate HTTP response.

Below is a function that looks at both the body of the request and a custom `X-My-Header` header.

```java
package com.example.fn;
import com.fnproject.fn.api.OutputEvent;
import com.fnproject.fn.api.InputEvent;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.stream.Collectors;

public class Events {
    private String readData(InputStream s) {
        return new BufferedReader(new InputStreamReader(s))
                .lines().collect(Collectors.joining(" / "));
    }

    public OutputEvent handleRequest(InputEvent rawInput) {
        String header = rawInput.getHeaders().get("X-My-Header").get();
        String text = rawInput.consumeBody(this::readData);

        String responseBody = "Received '" + text + "' with my header '" + header + "'.\n";

        Map<String, String> customHeaders = new HashMap<>();
        customHeaders.put("X-My-Response-Header", "any value here");

        OutputEvent out = OutputEvent.fromBytes(
            responseBody.getBytes(), // Data
            OutputEvent.SUCCESS,     // Any numeric HTTP status code can be used here
            "text/plain",            // Content type
            Headers.fromMap(customHeaders)  // and additional custom headers on output
        );
        return out;
    }
}
```

After you have [deployed the function](../README.md) you can call it via HTTP, for example:

```shell
$ curl -X POST --data-ascii "ABCD" -H "X-My-Header: EFGH" http://localhost:8080/r/java-app/events
Received 'ABCD' with my header 'EFGH'.
```

## Combining data binding and raw events
It is possible to combine a simple or POJO function parameter with an `InputEvent` parameter. This can be useful to easily have the body of the request converted to a type of your choice but still have access to the HTTP request headers.

The following function takes both a string as the request body and an input event:

```java
package com.example.fn;
import com.fnproject.fn.api.OutputEvent;
import com.fnproject.fn.api.InputEvent;

public class Events {
    public OutputEvent handleRequest(String text, InputEvent rawInput) {
        String header = rawInput.getHeaders().get("X-My-Header").get();

        String responseBody = "Received '" + text + "' with my header '" + header + "'.\n";

        OutputEvent out = OutputEvent.fromBytes(
            responseBody.getBytes(), // Data
            OutputEvent.SUCCESS,     // Status code of 200
            "text/plain"             // Content type
        );
        return out;
    }
}
```

Verify the function works as expected:

```shell
$ curl -X POST --data-ascii "ABCD" -H "X-My-Header: EFGH" http://localhost:8080/r/java-app/events
Received 'ABCD' with my header 'EFGH'.
```

### Caveats

To avoid buffering of input data in the function's heap,  _the HTTP request body can only be consumed once_. Attempting to consume the body twice (for example by using an automated conversion and also reading the body from the `InputEvent`) will cause an `IllegalStateException` to be thrown and your function will fail.


## More advanced data binding

The `InputEvent` and `OutputEvent` abstractions are very flexible but do not offer the same ease of use as the builtin conversions to data types.

To help with this, you can extend the functionality of the FDK by providing your own functionality to convert between data types. See the [Extending Data Binding](ExtendingDataBinding.md) tutorial for details of how to do this.
