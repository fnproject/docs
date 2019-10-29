# Fn Function Development Kit for Node.js

This Function Development Kit makes it easy to deploy Node.js functions to Fn.


## Creating a Node Function

Writing a Node.js function is simply a matter of writing a handler function
that you pass to the FDK to invoke each time your function is called.

Start by creating a node function with `fn init` and installing the FDK:

```sh
fn init --runtime node nodefunc
cd nodefunc
```

This creates a simple hello world function in `func.js`:

```javascript
var fdk = require('@fnproject/fdk');

fdk.handle(function(input) {
  var name = 'World';
  if (input.name) {
    name = input.name;
  }
  response = {'message': 'Hello ' + name}
  return response
})
```

The handler function takes the string input that is sent to the function
and returns a response string.  Using the FDK you don't have to worry about reading
input from standard input and writing to standard output to return your response.
The FDK let's you focus on your function logic and not the mechanics.

Now run it!

```sh
fn run
```

Now you have a basic running Node function that you can modify and add what you want.

From this point on, it's the same as any other function in any language. 
deploy the function:

```sh
fn deploy --app fnfdk --local
```

Once deployed, you and invoke the function:

```sh
echo -n "Tom" | fn call fdkdemo /hello
```

or

```sh
curl -d "Tom" http://localhost:8080/r/fdkdemo/hello
```

In both cases you'll get the response:

```sh
Hello Tom from Node!
```

## Function Context

Function invocation context details are available through an optional function argument.
To receive a context object, simply add a second argument to your handler function.
in the following example the `call_id` is obtained from the context and included in 
the response message:

```javascript
var fdk = require('@fnproject/fdk');

fdk.handle(function(input, ctx) {
  var name = 'World';
  if (input) {
    name = input;
  }
  return 'Hello ' + name + ' from Node call ' + ctx.callId + '!';
})
```

In the case of `default` format functions the context give you access to all environment variables
including those defined through function or app config as well as those automatically provided
by Fn like `app_name`, `path`, `memory`, etc.


## Asynchronous function responses

You return an asynchronous response from a function by returning a Javascript `Promise` from the function body: 

```javascript
var fdk = require('@fnproject/fdk');

fdk.handle(function(input, ctx) {
  return new Promise((resolve, reject) => {
     setTimeout(() => resolve("Hello"), 1000);
  });
})
```

## Handling non-json input and output

By default the FDK will convert input with a content-type matching `application/json` into a JSON object as the function input, if the incoming content type is different from `application/json` then the input will be the raw string value of the input. In both cases,  the raw (string) version of the input is also available in `ctx.body`.

Likewise by default the output of a function will be treated as a JSON object and converted using JSON.stringify - you can change this behaviour by setting the content type of the response in the context using `ctx.responseContentType='application/text-plain'`. Changing the content type to non-json will result in the output being treated as a string.

## Using HTTP headers and setting HTTP status codes

You can read http headers passed into a function invocation using `ctx.protocol.header(key)`, this returns the first header value of the header matching `key` (after canonicalization)  and `ctx.protocol.headers` which returns an object containing all headers.  

```javascript
var fdk = require('@fnproject/fdk');

fdk.handle(function(input, ctx) {
  console.log("Authorization header:" , ctx.protocol.header("Authorization"))
  console.log( ctx.protocol.headers) // prints e.g. { "Content-Type": ["application/json"],"Accept":["application/json","text/plain"] } 
})
```

Outbound headers and the HTTP status code can be modified when the function uses the `json` request format. 

To update the outbound status-code set  `ctx.protocol.statusCode`.  To modify outbound headers use `ctx.protocol.setHeader(k,v)`  or `ctx.protocol.addHeader(k,v)` which set (overwriting existing headers) or add (preserving existing headers) headers to the response respectively.  


```javascript
var fdk = require('@fnproject/fdk');

fdk.handle(function(input, ctx) {
   ctx.protocol.setHeader("Location", "http://example.com")
   ctx.protocol.statusCode = 302
})
```

## Fn and Node.js Dependencies
Fn handles Node.js dependencies in the following way:

* If a `package.json` is present without a `node_modules` directory, an Fn build runs an `npm install` within the build process and installs your dependencies.
* If the `node_modules` is present, Fn assumes you have provided the dependencies yourself and no installation is performed.
