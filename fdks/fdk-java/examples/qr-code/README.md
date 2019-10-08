# Java Fn Function Example: QR Code Gen

This example provides an HTTP endpoint for generating QR Codes:

```bash
$ curl "http://localhost:8080/t/qrcode/qr?contents=Hello%20world&format=png" -o hello.png
```


## FDK features

The Fn FDK for Java provides an `InputEvent` object, representing the function's
input. Output should be returned as an `OutputEvent` instance where fine-grained
control over the output (such as setting a MIME type) is needed. See [Data Binding](../../DataBinding.md) for further information.

## Step by step

Ensure you have the Fn server running (`fn start`). The Fn server will host your
function and provide the HTTP endpoints that invoke it:

```bash
$ fn start
```

Create an app.
```bash
$ fn create app qrcode
```


Deploy the function.

```bash
$ fn --verbose deploy --app qrcode --local
```

Invoke the function to create a QR code

```bash
$ curl "http://localhost:8080/t/qrcode/qr?contents=Hello%20World" -o hello.png
$ open hello.png # or for linux: xdg-open hello.png
```

Checkout `example.html` which uses the function to present a variety of
QR codes. The examples include:

* The response from default URL
* The text string Hello
* Instructions phone to make a call to 01234-567890
* Opens https://www.google.com URL
* Creates a text message from a QR Code

To try out the examples, point the camera of your iOS or Android device at the QRCode. The appropriate text and actions should just popup in the camera app.

## Code walkthrough

The entry point to the function is specified in `func.yaml` in the `cmd`
key. This is set to be `com.fnproject.fn.examples.QRGen::create`, the entrypoint
of the example function. The body of the function is shown below:


```java
public byte[] create(HTTPGatewayContext hctx) {
    // If format or contents is empty, set default to png and Hello World
    ImageType type = getFormat(hctx.getQueryParameters().get("format").orElse(defaultFormat));
    System.err.println("Format set to: " + type.toString());

    String contents = hctx.getQueryParameters().get("contents").orElse("QRCode Hello World!");
    System.err.println("QR code generated from contents: " + contents);

    ByteArrayOutputStream stream = QRCode.from(contents).to(type).stream();

    hctx.setResponseHeader("Content-Type", getMimeType(type));
    return stream.toByteArray();
}
```

The Fn FDK for Java facilitates access to the HTTP context of events triggered
from HTTP gateways via `HTTPGatewayContext`. The function response is a byte
array. See [Data Binding](../../DataBinding.md) for further information on the
types of input that the FDK provides.

In this example we take the request URL from the `HTTPGatewayContext` and parse
the query parameters present in the URL (using `QueryParameters`).
A QR code is created using `net.glxn.qrgen.javase.QRCode` which is
serialised to a byte array and returned in an HTTP response.

Logs are written via `System.err` for processing by the `functions` server.
When a QR code is generated the example logs `"QR code generated from contents: [...]"`.

Function configuration is injected into the constructor optionally taking
a `RuntimeContext` parameter giving access to parameters set for the
application or the function.

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

Input events are constructed using `fn.givenEvent()` providing an
`FnEventBuilder` interface for customising the input event which is then queued
event using `enqueue`. The function is finally run by
`fn.thenRun(<FunctionClass>.class, "<FunctionMethodName>")`. The results are
retrieved by `fn.getOnlyResult().getBodyAsBytes()` to then compare with a
pregenerated QR code read from disk.

See [Function Testing](../../FunctionTesting.md) for further information
on the features the testing package provides.
