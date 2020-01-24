# Fn Variable Commands
The Fn CLI can store config variable information in the following places:

* **Applications:** Variables can be accessed by all functions in the application.
* **Functions:** Variables that can only by accessed by a single function.


## Fn Variable Command Reference
The following sections provide definitions and examples of all the Fn commands related to the Fn context.


### Fn Application Vars Cheat Sheet
* Create: `fn cf a <your-app-name> <key> <value>`
* Delete: `fn d cf a <your-app-name> <key>`
* List: `fn ls cf a <your-app-name>`
* List all Apps: `fn ls a`
* Update Name/Value: `fn cf a <your-app-name> <key> <value>`
    * Overwrites the value.


### Fn Function Vars Cheat Sheet
* Create: `fn cf f <your-app-name> <your-function-name> <key> <value>`
* Delete: `fn d cf f <your-app-name> <your-function-name> <key>`
* List: `fn ls cf f <your-app-name> <your-function-name>`
* List all Functions: `fn ls f <your-app-name>`
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


### Fn Application Vars Usage Examples
* Create: `fn cf a my-app DB_USER my-user-name`
* Delete: `fn d cf a my-app DB_USER`
* List: `fn ls cf a my-app`
* List all Apps: `fn ls a`
* Update Name/Value: `fn cf a my-app DB_USER my-different-user-name`
    * Overwrites the value.


### Fn Function Vars Usage Examples
* Create: `fn cf f my-app my-func DB_HOST myhost`
* Delete: `fn d cf f my-app my-func DB_HOST`
* List: `fn ls cf f my-app my-func`
* List all Functions: `fn ls f my-app`
* Update Name/Value: `fn cf f my-app my-func DB_HOST mynewhost`
    * Overwrites the value.


### Detailed Fn Application Vars Command Examples
* **Create** a new Fn app variable.
    * `fn config app <your-app-name> <key> <value>`
* **Delete** an Fn app variable.
    * `fn delete config app <your-app-name> <key>`
* **List** all contexts on this system.
    * `fn list config app <your-app-name>`
* **Update** key/value data for your application.
    * `fn config app <your-app-name> <key> <value>`


### Detailed Fn Function Vars Command Examples
* Create: `fn config function <your-app-name> <your-function-name> <key> <value>`
* Delete: `fn delete config function <your-app-name> <your-function-name> <key>`
* List: `fn list config function <your-app-name> <your-function-name>`
* List all Functions: `fn list functions <your-app-name>`
* Update Name/Value: `fn config function <your-app-name> <your-function-name> <key> <value>`
    * Overwrites the value.
