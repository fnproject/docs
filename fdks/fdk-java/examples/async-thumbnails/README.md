# Example Fn Java FDK / Fn Flows Project: Asynchronous Thumbnails

This example provides an HTTP endpoint for asynchronously creating three
thumbnails of an image whose data is provided as the body of the HTTP request
as an octet stream. The thumbnails are then transferred to an object storage
server, credentials for which are configured in the function configuration.
The function returns a string unique ID which corresponds to the image.

The intention is that such a function would be used in the context of a wider
system which then would store a correspondence between that ID and further data.
For example, the thumbnail image might be a user's avatar in a web app, and
its ID would be linked to the user's ID in a database somewhere.

## Dependencies

* [minio client `mc`](https://github.com/minio/mc) installed locally and
  available on your path

## Demonstrated FDK features

This example showcases how to use the Fn Flow API to invoke other
Fn functions asynchronously in a workflow expressed in code.


## Step by step

A `setup.sh` script is provided to set up the environment you will need for
this example. Run:

```bash
./setup/setup.sh
```

This will start a local functions service, a local flow completion
service, and will set up a `myapp` application and three functions: `resize128`,
`resize256` and `resize512`. These functions just invoke `imagemagick` to convert the images to the specified sizes.

The setup script also starts a docker container with an object storage daemon
based on `minio` (with access key `alpha` and secret key `betabetabetabeta`).
To view the files uploaded to minio you will need to download the [`mc` minio
client](https://docs.minio.io/docs/minio-client-quickstart-guide) and place it
somewhere on your path.

The local directories `./storage-upload` is mapped as a volume in the
docker container, so that you can verify when the thumbnails are uploaded.

Build the function locally:

```bash
$ fn deploy --local --app myapp 
```


Configure the app. In order to do this you must determine the IP address of the
storage server docker container:

```bash
$ docker inspect --type container -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' example-storage-server
172.17.0.4
```

and then use it as the storage host:

```bash
$ fn config app myapp  OBJECT_STORAGE_URL http://172.17.0.4:9000
myapp /async-thumbnails updated OBJECT_STORAGE_URL with http://172.17.0.4:9000
$ fn config app myapp  OBJECT_STORAGE_ACCESS alpha
myapp /async-thumbnails updated OBJECT_STORAGE_ACCESS with alpha
$ fn config app myapp OBJECT_STORAGE_SECRET betabetabetabeta
myapp /async-thumbnails updated OBJECT_STORAGE_SECRET with betabetabetabeta
```

Invoke the function by passing the provided test image:

```bash
$ curl -X POST --data-binary @test-image.png -H "Content-type: application/octet-stream" "http://localhost:8080/t/myapp/async-thumbnails"
{"imageId":"bd74fff4-0388-4c6f-82f2-8cde9ba9b6fc"}
```

After a while, the files will be uploaded and can be viewed using `mc ls -r example-storage-server`

```bash
$ mc ls -r example-storage-server
[2017-08-31 09:18:10 BST]  25KiB alpha/678a162e-a4e1-4926-bb9f-f552598ee4c0-128.png
[2017-08-31 09:18:12 BST]  25KiB alpha/678a162e-a4e1-4926-bb9f-f552598ee4c0-256.png
[2017-08-31 09:18:12 BST]  25KiB alpha/678a162e-a4e1-4926-bb9f-f552598ee4c0-512.png
[2017-08-31 09:18:08 BST]  25KiB alpha/678a162e-a4e1-4926-bb9f-f552598ee4c0.png
```


## Code walkthrough

The entrypoint to the function is specified in `func.yaml` in the `cmd` key.
It is set to `com.example.fn.ThumbnailsFunction::handleRequest`.

The class `ThumbnailsFunction` has a constructor [which reads configuration data](../../docs/FunctionConfiguration.md)
to obtain the details of the storage server host, username and password:

```java
public class ThumbnailsFunction {

    private final String storageUrl;
    private final String storageAccessKey;
    private final String storageSecretKey;

    public ThumbnailsFunction(RuntimeContext ctx) {
        storageUrl = ctx.getConfigurationByKey("OBJECT_STORAGE_URL")
                .orElseThrow(() -> new RuntimeException("Missing configuration: OBJECT_STORAGE_URL"));
        storageAccessKey = ctx.getConfigurationByKey("OBJECT_STORAGE_ACCESS")
                .orElseThrow(() -> new RuntimeException("Missing configuration: OBJECT_STORAGE_ACCESS"));
        storageSecretKey = ctx.getConfigurationByKey("OBJECT_STORAGE_SECRET")
                .orElseThrow(() -> new RuntimeException("Missing configuration: OBJECT_STORAGE_SECRET"));
        
        resize128ID = ctx.getConfigurationByKey("RESIZE_128_FN_ID")
                  .orElseThrow(() -> new RuntimeException("Missing configuration: RESIZE_128_FN_ID"));
        resize256ID = ctx.getConfigurationByKey("RESIZE_256_FN_ID")
          .orElseThrow(() -> new RuntimeException("Missing configuration: RESIZE_256_FN_ID"));
        resize512ID = ctx.getConfigurationByKey("RESIZE_512_FN_ID")
          .orElseThrow(() -> new RuntimeException("Missing configuration: RESIZE_512_FN_ID"));
    }

    // ...
}
```

It also features an inner `Response` class which is used as a POJO return type, which the runtime will serialize to
JSON. See [Data Binding](../../docs/DataBinding.md) for more information on how marshalling is performed.

```java
public class ThumbnailsFunction {

    // ...

    public class Response {
        Response(String imageId) { this.imageId = imageId; }
        public String imageId;
    }

    // ...
}
```

The main method of the function makes use of the Fn Flow API, spawning three concurrent asynchronous tasks, each
of which performs the creation of one thumbnail and its upload to the object storage server. The original image is also
uploaded alongside the thumbnails.

The file names are generated from a UUID which is then returned by the function in the response.

```java
public class ThumbnailsFunction {

    // ...

    public Response handleRequest(byte[] imageBuffer) {
        String id = java.util.UUID.randomUUID().toString();
        Flow runtime = Flows.currentFlow();

        runtime.allOf(
                runtime.invokeFunction(resize128ID, HttpMethod.POST, Headers.emptyHeaders(), imageBuffer)
                        .thenAccept((img) -> objectUpload(img.getBodyAsBytes(), id + "-128.png")),
                runtime.invokeFunction(resize256ID, HttpMethod.POST, Headers.emptyHeaders(), imageBuffer)
                        .thenAccept((img) -> objectUpload(img.getBodyAsBytes(), id + "-256.png")),
                runtime.invokeFunction(resize512ID, HttpMethod.POST, Headers.emptyHeaders(), imageBuffer)
                        .thenAccept((img) -> objectUpload(img.getBodyAsBytes(), id + "-512.png")),
                runtime.supply(() -> objectUpload(imageBuffer, id + ".png"))
        );
        return new Response(id);
    }

    // ...
}
```

The `objectUpload` method is private and just uses a `minio` client to perform the upload.

```java
public class ThumbnailsFunction {

    // ...

    /**
     * Uploads the provided data to the storage server, as an object named as specified.
     *
     * @param imageBuffer the image data to upload
     * @param objectName the name of the remote object to create
     */
    private void objectUpload(byte[] imageBuffer, String objectName) {
        try {
            MinioClient minioClient = new MinioClient(storageUrl, storageAccessKey, storageSecretKey);

            // Ensure the bucket exists.
            if(!minioClient.bucketExists("alpha")) {
                minioClient.makeBucket("alpha");
            }

            // Upload the image to the bucket with putObject
            minioClient.putObject("alpha", objectName, new ByteArrayInputStream(imageBuffer), imageBuffer.length, "application/octet-stream");
        } catch(Exception e) {
            System.err.println("Error occurred: " + e);
            e.printStackTrace();
        }
    }

    // ...
}
```

## Test walkthrough

Two simple tests are provided for this example function. One of them checks the
normal behavior of the function, and the other one checks what happens when one
of the resize functions fails.

The tests can be found in the class `ThumbnailsFunctionTest`.

First of all, the class initializes the `FnTestingRule` harness, as explained
in [Testing Functions](../../docs/TestingFunctions.md).

```java
public class ThumbnailsFunctionTest {

     @Rule
     public final FnTestingRule fn = FnTestingRule.createDefault();
     private final FlowTesting flow = FlowTesting.create(fn);

    // ...
}
```

Because we need to test the side effects of our asynchronous upload processes,
the test code then mocks an HTTP server pretending to be `minio`. We use
`WireMockRule` for this, but really any framework or library can be used.

```java
public class ThumbnailsFunctionTest {

    // ...

    @Rule
    public final WireMockRule mockServer = new WireMockRule();

    // ...

    private void mockMinio() {
        // ... the code here sets up the mocks in `mockServer`
    }

    // ...
}
```

We can now look at the proper testing methods.

The `testThumbnail()` method checks the normal behavior of the function, by
using the `FnTestingRule` API to mock the calls to external functions.

```java
public class ThumbnailsFunctionTest {

    // ...

   @Test
    public void testThumbnail() {

                 fn.setConfig("OBJECT_STORAGE_URL", "http://localhost:" + mockServer.port())
                   .setConfig("OBJECT_STORAGE_ACCESS", "alpha")
                   .setConfig("OBJECT_STORAGE_SECRET", "betabetabetabeta")
                   .setConfig("RESIZE_128_FN_ID","myapp/resize128")
                   .setConfig("RESIZE_256_FN_ID","myapp/resize256")
                   .setConfig("RESIZE_512_FN_ID","myapp/resize512");

                flow.givenFn("myapp/resize128")
                    .withAction((data) -> "128".getBytes())
                .givenFn("myapp/resize256")
                    .withAction((data) -> "256".getBytes())
                .givenFn("myapp/resize512")
                    .withAction((data) -> "512".getBytes())

                fn.givenEvent()
                    .withBody("testing".getBytes())
                    .enqueue();

        // Mock the http endpoint
        mockMinio();

        testing.thenRun(ThumbnailsFunction.class, "handleRequest");

        // Check the final image uploads were performed
        mockServer.verify(1, putRequestedFor(urlMatching("/alpha/.*\\.png")).withRequestBody(equalTo("testing")));

        // ... and the other checks
    }

    // ...
}
```

Similarly, the `anExternalFunctionFailure()` method checks what happens when one
of the function invocations fails:

```java
public class ThumbnailsFunctionTest {

    // ...

    @Test
    public void anExternalFunctionFailure() {
        fn.setConfig("OBJECT_STORAGE_URL", "http://localhost:" + mockServer.port())
          .setConfig("OBJECT_STORAGE_ACCESS", "alpha")
          .setConfig("OBJECT_STORAGE_SECRET", "betabetabetabeta")
          .setConfig("RESIZE_128_FN_ID","myapp/resize128")
          .setConfig("RESIZE_256_FN_ID","myapp/resize256")
          .setConfig("RESIZE_512_FN_ID","myapp/resize512");
        
        flow.givenFn("myapp/resize128")
                .withResult("128".getBytes())
            .givenFn("myapp/resize256")
                .withResult("256".getBytes())
            .givenFn("myapp/resize512")
                .withFunctionError();
        
        fn.givenEvent()
             .withBody("testing".getBytes())
             .enqueue();

        // Mock the http endpoint
        mockMinio();

        testing.thenRun(ThumbnailsFunction.class, "handleRequest");

        // Confirm that one image upload didn't happen
        mockServer.verify(0, putRequestedFor(urlMatching("/alpha/.*\\.png")).withRequestBody(equalTo("512")));

        mockServer.verify(3, putRequestedFor(urlMatching(".*")));

    }

    // ...
}
```
