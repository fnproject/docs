# Example oFunctions project: QR Code Gen

This example provides an HTTP endpoint for generating QR Codes:

```bash
$ curl "http://localhost:8080/r/qr-app/qr?contents=Hello%20world&format=png" -o hello-world.png
```


## FDK features

The fn Java FDK API provides an `InputEvent` object, representing the function's input;
output should be returned as an `OutputEvent` instance where fine-grained control over
the output (such as setting a MIME type) is needed.

## Step by step

Ensure you have the functions server running using; this will host your
function and provide the HTTP endpoints that invoke it:

```bash
$ fn start
```

Build the function locally

```bash
$ fn build
```

Create an app and route to host the function

```bash
$ fn create app qr-app
$ fn create route qr-app /qr
```

Invoke the function to create a QR code

```bash
$ curl "http://localhost:8080/r/qr-app/qr?contents=Hello%20World" -o hello-world.png
$ open hello-world.png # or for linux: xdg-open hello-world.png
```

Checkout `example.html` which uses the function to present a variety of
QR codes.


## Code walkthrough

The entry point to the function is specified in `func.yaml` in the `cmd`
key. This is set to be `com.fnproject.fn.examples.QRGen::create`, the entrypoint
of the example function. The body of the function is shown below:


```java

    public byte[] create(HTTPGatewayContext hctx) {
        ImageType type = getFormat(hctx.getQueryParameters().get("format").orElse(defaultFormat));
        System.err.println("Default format: " + type.toString());
        String contents = hctx.getQueryParameters().get("contents").orElseThrow(() -> new RuntimeException("Contents must be provided to the QR code"));

        ByteArrayOutputStream stream = QRCode.from(contents).to(type).stream();
        System.err.println("Generated QR Code for contents: " + contents);

        hctx.setResponseHeader("Content-Type", getMimeType(type));
        return stream.toByteArray();
    }

```

The fn Java FDK facilitates access to the HTTP context of events triggered from HTTP gateways via `HTTPGatewayContext` , and response of the function as a bye array, for more fine grained control of the platform. See
[Data Binding](/docs/DataBinding.md) for further information on the types
of input that the fn Java FDK provides.

In this example we take the request URL from the `InputEvent` and parse
the query parameters present in the URL (using `QueryParametersParser`).
A QR code is created using `net.glxn.qrgen.javase.QRCode` which is
then serialised to a byte array used to create an output event `OutputEvent`
which the fn Java FDK then serialises to an HTTP response.

Logs are written via `System.err` for processing by the `functions` server.
When a QR code is generated the example logs `"Generated QR Code for contents: [...]"`
which can be seen by inspecting the functions server logs.

Function configuration is injected into the constructor optionally taking
a `RuntimeContext` parameter giving access to parameters set on the
app and route hosting the function.

```java
...
    private final String defaultFormat;

    public QRGen(RuntimeContext ctx) {
        defaultFormat = ctx.getConfigurationByKey("FORMAT").orElse("png");
    }
...
```

### Testing

The fn Java FDK provides a `com.fnproject.fn.testing` package for simulating
function invocations and inspecting function results. This is accomplished
by a fake functions server, `FnTesting`, whose life cycle is managed by a [JUnit rule](
https://github.com/junit-team/junit4/wiki/rules).

```java
...
    @Rule
    public FnTesting fn = FnTesting.createDefault();
...
```

A `FnTesting` object is used to simulate interactions with the function,
 this is setup as a JUnit . We will use
this to handle invocations of functions and retrieving function results

```java
...
  @Test
     public void textHelloWorld() throws Exception {
         String content = "hello world";
         fn.givenEvent()
           .withHeader("Fn-Http-Request-Url", "http://www.example.com/qr?contents=hello+world&format=png")
           .withHeader("Fn-Http-Method","GET")
           .enqueue();
         fn.thenRun(QRGen.class, "create");
 
         assertEquals(content, decode(fn.getOnlyResult().getBodyAsBytes()));
     }
```

Input events are constructed using `fn.givenEvent()` providing an `FnEventBuilder`
interface for customising the input event which is then queued event using `enqueue`.
The function is finally run by `fn.thenRun(<FunctionClass>.class, "<FunctionMethodName>")`.
The results are retrieved by `fn.getOnlyResult().getBodyAsBytes()` to then compare
with a pregenerated QR code read from disk.

See [Function Testing](/docs/FunctionTesting.md) for further information
on the features the testing package provides.
