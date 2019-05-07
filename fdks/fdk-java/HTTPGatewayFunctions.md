# Accessing HTTP information from functions

Functions can be used to handle events, RPC calls or HTTP requests.  When you are writing a function that handles an HTTP request you frequently need access to the HTTP headers of the incoming request or need to set HTTP headers or the status code on the outbound respsonse.


In Fn for Java, when your function is being served by an HTTP trigger (or another compatible HTTP gateway) you can get access to both the incoming request headers for your function by adding a 'com.fnproject.fn.api.httpgateway.HTTPGatewayContext' parameter to your function's parameters.


 Using this  allows you to :

 * Read incoming headers
 * Access the method and request URL for the trigger
 * Write outbound headers to the response
 * Set the status code  of the response


 For example this function reads a request header the method and request URL, sets an response header and sets the response status code to perform an HTTP redirect.

```java
package com.fnproject.fn.examples;
import com.fnproject.fn.api.httpgateway.HTTPGatewayContext;


public class RedirectFunction {

    public void redirect(HTTPGatewayContext hctx) {
        System.err.println("Request URL is:" + hctx.getRequestURL());
        System.err.println("Trace ID" + hctx.getHeaders().get("My-Trace-ID").orElse("N/A"));

        hctx.setResponseHeader("Location","http://example.com");
        hctx.setStatusCode(302);

    }
}

```
