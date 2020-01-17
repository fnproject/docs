# Fn Variable Commands
The Fn CLI can store variable information in the following spaces:

* **Application:** Variables can be accessed by all functions in the application.
* **Function:** Variables that can only by accessed by a single function.


## Fn Variable Command Reference
The following sections provide definitions and examples of all the Fn commands related to the Fn context.


### Fn Application Cheat Sheet
* Create: `fn cf a <your-app-name> <key> <value>`
* Delete: `fn d cf a <your-app-name> <key>`
* List: `fn ls cf a <your-app-name>`
* Update Name/Value: `fn cf a <your-app-name> <key> <value>`
    * Overwrites the value.


### Fn Function Cheat Sheet
* Create: `fn cf f <your-app-name> <your-function-name> <key> <value>`
* Delete: `fn d cf f <your-app-name> <your-function-name> <key>`
* List: `fn ls cf f <your-app-name> <your-function-name>`
* Update Name/Value: `fn cf f <your-app-name> <your-function-name> <key> <value>`
    * Overwrites the value.


## Usage: Setup a new App and Variable
Here are the steps to setup a new app and add a key/value pair.
* Create new app.
    * `fn c a <your-app-name>`
* Add a variable to your application.
    * `fn cf a <your-app-name> <key> <value>`
* Get list of all the variables set for the application.
    * `fn ls cf a <your-app-name>`


## Usage: Setup a new Function and Variable
Here are the steps to setup a function key/value pair.
* Create new app.
    * `fn c a <your-app-name>`
* Deploy your function to the application.
    * `fn dp --app <your-app-name> --local`
* Add a variable to your function.
    * `fn cf f <your-app-name> <your-function-name> <key> <value>`
* Get list of all the variables set for the application.
    * `fn ls cf f <your-app-name> <your-function-name>`


### Application Usage Examples
* Create: `fn cf a my-app DB_USER my-user-name`
* Delete: `fn d cf a my-app DB_USER`
* List: `fn ls cf a my-app`
* Update Name/Value: `fn cf a my-app DB_USER my-different-user-name`
    * Overwrites the value.



### Detailed Application Command Examples
* **Create** a new Fn app variable.
    * `fn config app <your-app-name> <key> <value>`
* **Delete** an Fn app variable.
    * `fn delete config app <your-app-name> <key>`
* **List** all contexts on this system.
    * `fn list config app <your-app-name>`
* **Update** key/value data for your application.
    * `fn config app <your-app-name> <key> <value>`
