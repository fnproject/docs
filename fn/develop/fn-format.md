# Fn container contract

This document will describe the details of how a function works, inputs/outputs, etc.

The basic idea is to handle http requests over a unix domain socket. Each
container will only receive one request at a time, in that until a response is
returned from a previous request, no new requests will be issued to a
container's http server.

### FDK Contract outline

Function Development Kits (FDKs) are libraries for various languages that implement Fn's container contract for function input, output and configuration. In order to be a fully 'FDK compliant', below are the rules:

If `FN_FORMAT` variable is http-stream, then FDKs __MUST__ parse `FN_LISTENER` environment variable.

`FN_LISTENER` must contain the listener address for FDKs to bind/listen to. Currently we only support unix-domain-socket (UDS) with `SOCK_STREAM` (TCP). This means `FN_LISTENER` prefix is always `unix:/`.

For example:

```
FN_LISTENER=unix:/var/run/fn/listener/listen.sock
FN_FORMAT=http-stream
```

If `FN_FORMAT` is `http-stream`, then absence of `FN_LISTENER` or "unix:" prefix in `FN_LISTENER` is an error and FDKs are REQUIRED to terminate/exit with error.

Before exiting, FDKs __SHOULD__ remove the UDS file (from `FN_LISTENER` path).

FDKs upon creation of UDS file on disk with bind system call __SHOULD__ be ready to receive and handle traffic. Upon creation of the file specified at `FN_LISTENER`, the UDS file __MUST__ be writable by fn-agent. In order to create a properly permissioned UDS file, FDKs __MUST__ create a file with [at least] permissions of `0666`, if the language provides support for creating this file with the right permissions this may be easily achieved; users may alternatively bind to a file that is not `FN_LISTENER`, modify its permissions to the required setting, and then symlink that file to `FN_LISTENER` (see fdk-go for an example).

Path in `FN_LISTENER` (after "unix:" prefix) cannot be larger than 107 bytes.

FDKs __MUST__ listen on the unix socket within 5 seconds of startup, Fn will enforce time limits and will terminate such FDK containers.

Once initialised the FDK should respond to HTTP requests by accepting connections on the created unix socket.

The FDK __SHOULD NOT__ enforce any read or write timeouts on incoming requests or responses

The FDK __SHOULD__ support HTTP/1.1 Keep alive behaviour

The agent __MUST__ maintain no more than one concurrent HTTP connection to the FDK HTTP servers

Containers __MUST__ implement HTTP/1.1

Any data sent to Stdout and Stderr will be logged by Fn and sent to any configured logging facility

Each function container is responsible for handling multiple function
invocations against it, one at a time, for as long as the container lives.

Fn will make HTTP requests to the container on the `/call` URL of the containers HTTP UDS port using.

```
POST /call HTTP/1.1
Host: localhost:8080
Fn-Call-Id : <call_id>
Fn-Deadline: <date/time>
Content-type: application/cloudevent+json

<Body here>
```

Invoke Response: 

```
HTTP/1.1 200 OK
Fn-Fdk-Version: fdk-go/0.0.42
Content-type: text/plain
My-Header: foo

<Body here>
```

### Environment Variables

The below are the environment variables that a function can expect to use.
FDKs __SHOULD__ provide a facility to easily access these without having to
use an OS library.

* `FN_FN_ID` - fn id
* `FN_APP_ID` - app id of the fn
* `FN_NAME` - name of the fn
* `FN_APP_NAME` - the name of the application of the fn
* `FN_FORMAT` - `http-stream` (DEPRECATED)
* `FN_LISTENER` - the path where a unix socket file should accept
* `FN_MEMORY` - a number representing the amount of memory available to the call, in MB
* `FN_TMPSIZE` - a number representing the amount of writable disk space available under /tmp for the call, in MB

In addition to these, all config variables set on `app.config` or `fn.config` will be populated into the environment exactly as configured, for example if `app.config = { "HAMMER": "TIME" }` your environment will be populated with `HAMMER=TIME`.

### Headers

Request:

* `Fn-Call-Id` - id for the call
* `Fn-Deadline` - RFC3339 timestamp indicating the deadline for a function call
* `Fn-Intent` - optional, may contain value for handling certain kinds of requests (see gateway protocol)
* `Fn-*` - reserved for future usage
* `*` - any other headers, potentially rooted from an http trigger

Response:

* `Fn-Fdk-Version` - (optional, not required for unofficial FDK) header carrying the fdk version, e.g. `fdk-go/0.0.42`

###  HTTP Gateway Protocol Extension

This protocol applies for any requests where `Fn-Intent` is set to `httprequest`. 

This is an extension to the invoke protocol designed to encapsulate existing HTTP requests and responses (e.g. from an upstream HTTP gateway or  a Fn Trigger Endpoint)  - It transposes request and response attributes in to invoke attributes (sent as headers in request/response objects) under the following terms:

* Request URL is available via the `Fn-Http-Request-URL` header
* Request method is available via the `Fn-Http-Method` header
* all incoming headers will be available via `Fn-Http-H-{orig-key}`, with one exception:
* `Content-Type` is not modified with any prefix

FDKs should allow users to access headers under the original key rather than the modified `Fn-Http-H-X` header. 

FDKs are also responsible for encapsulating http response headers under certain rules, as well:

* http status code must be set via `Fn-Http-Status`
* all outgoing headers that are not prefixed with `Fn-` (eg `Fn-Fdk-Version`) must be prefixed with `Fn-Http-H-`, with the exception of `Content-Type`
* `Content-Type` should not be modified with any prefix

Example request / response:

```
POST /call HTTP/1.1
Fn-Call-Id : 12345678910
Fn-Deadline: <date/time>
Fn-Http-Request-Url: https://my.fn.com/t/hello/world
Fn-Http-Request-Method: PUT
Fn-Http-H-Custom-Header: foo
Content-type: text/plain

<Body here>
```

Trigger Response:

```
HTTP/1.1 200 OK
Fn-Http-Status: 204
Fn-Http-H-My-Header: foo
Fn-Fdk-Version: fdk-go/0.0.42
Content-type: text/plain

<Body here>
```
