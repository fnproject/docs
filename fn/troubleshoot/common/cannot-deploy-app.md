# Cannot Deploy a Function with Fn client

### Error Messages
`fn --verbose deploy --app app-name --local` fails with error message.

* "Fn: Get http://localhost:8080/v2/apps?name=nodeapp: dial tcp [::1]:8080: connect: connection refused"

### Problem
The most common causes of this error message is:

* The Fn server has not been started.
* Another service is using port 8080.

### Solution
To solve this problem:

* Start Fn server
* If Fn server does not start
    * Run `netstat -a` to see if there is a server on port 8080.
    * Stop the other server.
    * Start Fn server.
