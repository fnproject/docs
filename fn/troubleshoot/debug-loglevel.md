# Log to Fn Server Terminal Window with DEBUG
When working with Fn locally, you have the option to turn on the DEBUG log-level using the `fn start` command. This causes detailed information about functions to be output to the terminal after Fn server is started.

To enable DEBUG logging for Fn server, restart the server with the following command:

![user input](images/userinput.png)
>```sh
> fn start --log-level DEBUG
>```

```sh
2019/12/19 09:26:27 ¡¡¡ 'fn start' should NOT be used for PRODUCTION !!! see https://github.com/fnproject/fn-helm/
time="2019-12-19T16:26:28Z" level=info msg="Setting log level to" fields.level=DEBUG
...
```
Notice in the first couple of messages state that the log level is set to DEBUG.

Here is an example of the kind of output generated when a Runtime Exception is encountered with Java.
```sh
time="2019-12-19T16:27:55Z" level=debug msg="Caused by: java.lang.RuntimeException: Something went horribly wrong! ...\n" action="server.handleFnInvokeCall)-fm" app_id=01DWFFR290NG8G00GZJ0000001 call_id=01DWFFS7QZNG8G00GZJ0000003 fn_id=01DWFFRQVQNG8G00GZJ0000002 image="fndemouser/trouble:0.0.2" user_log=true
time="2019-12-19T16:27:55Z" level=debug msg="    at com.example.fn.HelloFunction.handleRequest(HelloFunction.java:7)\n" action="server.handleFnInvokeCall)-fm" app_id=01DWFFR290NG8G00GZJ0000001 call_id=01DWFFS7QZNG8G00GZJ0000003 fn_id=01DWFFRQVQNG8G00GZJ0000002 image="fndemouser/trouble:0.0.2" user_log=true
```
A Runtime Exception was thrown on line 7 of the HelloFunction.

Running the Fn server with the DEBUG log level is a great way to track down any issues you are having with your functions.
