# Fn Function Format

This document will describe the details of how a function works, inputs/outputs, etc.

## I/O Formats

Historically, Fn has supported multiple format for exchanging data with a function. These could be specified in the `func.yaml` file. These formats included: I/O format (stdin/stdout), `cloudevent`, `json`, and `http`. All these formats along with the `format` field have been depricated in favor of a single format.

## http-stream Format
The http-stream format uses language FDKS to make HTTP requests and receive responses using UNIX sockets as the transport. Data is passed in the HTTP request/response body and depends on the `Content-Type` header to identify the payload.

The switch to the http-stream format was made for the following reasons. The new format:

* is largely compatible with the old approach for handling input and output.
* transparently carries HTTP content without the need to modify request bodies or fully buffer request content for translation.
* can be efficiently transmitted/received to containers over HTTP.
* represent internal events as an HTTP request/response.
* disambiguates RPC requests and events using an FDK event type. Thus, a single function could handle multiple event types.

Configuration values and environment information are passed in through environment variables.


#### Environment Variables used with http-stream

Your function has access to a set of environment variables, they include:

* `FN_ID` - The function ID of the current function.
* `FN_APP_ID` - The app ID of the current function.
* `FN_NAME` - The human readble name of the current function. (e.g., `myfunc`)
* `FN_APP_NAME` - The human readble name of the current function. (e.g., `myapp`)
* `FN_FORMAT` - Value is `http-stream`.
* `FN_LISTENER` - A socket address (prefixed with "unix://") on the file system the FDK should create to listen for requests. The platform should guarantee that the directory is writable. FDKs should only write to the socket. (e.g. `unix:/var/run/fn/listener/listen.sock`)
* `FN_MEMORY` - a number representing the amount of memory available to the call, in MB. (e.g., `128`)
* `FN_TMPSIZE` - a number representing the amount of writable disk space available under `/tmp` for the call, in MB. (e.g., `512`)


## http-stream Input and Output

Certain HTTP headers and response code an be used with Fn.

### Request

```text
GET / HTTP/1.1
Content-Length: 5

world
```

#### Input

The input to the function is in standard HTTP format with the
following additional headers:

* `Fn-Call-id` - Fn identifier for this call.
* `Fn_deadline` - RFC3339 time stamp of the expiration (deadline) date of function execution.
* `Fn-Intent` - The callers intent for how the message should be processed. (e.g., &lt;empty&gt;, "httprequest")
* `Content-Type` - Content type of the incoming message. Must be set if the request has a body.
* `Fn-*` - Fn specific headers. (e.g., "Fn-Http-H-Accept":"*/*")

#### Output

Your function should output the exact response in HTTP format you'd like to be returned to the client:

```text
HTTP/1.1 200 OK
Content-Length: 11

hello world
```

Suggested HTTP responses include:

* `200` - Success. Container handled request
* `502` - Detectable user code error. Something went wrong in the function.
* `504` - Detectable function timeout.
  