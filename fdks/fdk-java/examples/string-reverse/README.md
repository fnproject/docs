# Example Java Function: String Reverse

This example provides an HTTP trigger endpoint for reversing strings.

```bash
$ curl -d "Hello World" http://localhost:8080/t/string-reverse-app/string-reverse
dlroW olleH
```


## Demonstrated FDK features

This example uses **none** of the Fn Java FDK features, in fact it doesn't have
any dependency on the Java FDK.  It is just plain old Java code.

## Step by step

Ensure you have the Fn server running to host your function and provide the HTTP endpoint that invokes it:

(1) Start the server

```sh
$ fn start
```

(2) Create an app for the function

```sh
$ fn create app string-reverse-app
```

(3) Deploy the function to your app from the `string-reverse` directory.

```sh
fn deploy --app string-reverse-app --local
```

(4) Invoke the function and reverse the string.

```sh
echo "Hello World" | fn invoke string-reverse-app string-reverse
dlroW olleH
```

(5) Invoke the function using curl and  a trigger to reverse a string.

```bash
$ curl -d "Hello World" http://localhost:8080/t/string-reverse-app/string-reverse
dlroW olleH
```


## Code walkthrough

The entrypoint to the function is specified in `func.yaml` in the `cmd` key.
It is set to `com.example.fn.StringReverse::reverse`. The whole class
`StringReverse` is shown below:


```java
package com.example.fn;

public class StringReverse {
    public String reverse(String str) {
        return new StringBuilder(str).reverse().toString();
    }
}
```

As you can see, this is plain Java with no references to the Fn API. The
Fn Java FDK handles the marshalling of the HTTP body into the `str`
parameter as well as the marshalling of the returned reversed string into the HTTP
response body (see [Data Binding](../../DataBinding.md) for more
information on how marshalling is performed).


