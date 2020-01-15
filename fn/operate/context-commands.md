# Fn Context Commands
The Fn context is a configuration file stored in the `~/fn/contexts` directory. Each context file stores all the information needed to make a connection to an Fn server. The Fn context determines where the CLI will find an Fn server which allow you to issue command for that server.


## Sample Context File
When you run Fn the first time, a default context YAML file is created (`~./fn/contexts/default.yaml`). A sample Fn default context file looks like this.

```yaml
api-url: http://localhost:8080
provider: default
registry: fndemouser
```

* `api-url` - URL location of the Fn server.
* `provider` - Service provider for Fn. Contains the name of the provider or "default" by default.
* `registry` - Login name for the Docker registry used with this context. In this example, `fndemouser` (fn demo user) is a made up name which causes Docker to store image data locally. If you wish to use Docker Hub to store your images, you will put your Docker Hub login ID in this field.

## Fn Context Command Reference
The following sections provide definitions and examples of all the Fn commands related to the Fn context.

### Cheat Sheet
* Create: `fn c ctx <context-name> --api-url http://<host>:<port> --registry <docker-hub-id>`
* Delete: `fn d ctx <context-name>`
* List: `fn ls <context-name>`
* Update (Add) Name/Value: `fn up ctx <key> <value>`
* Update (Delete) Name/Value: `fn up ctx --delete <key>`


## Usage: Setup a new Context
Here are the steps to setup a new context.
* Create context
    * `fn c ctx my-ctx --api-url http://localhost:8080 --registry yourGitHubUserName`
* Use the new context:
    * `fn u ctx my-ctx`
* Get list of contexts:
    * `fn ls ctx`
    * Should show all the contexts that exist with your new context selected.


### Usage Examples
* Create: `fn c ctx my-ctx --api-url http://localhost:8080 --registry youridhere`
* Delete: `fn d ctx my-ctx`
* List: `fn ls ctx`
* Update (Add) Name/Value: `fn u ctx KEY value`
* Update (Delete) Name/Value: `fn u ctx --delete KEY`



### Detailed Command Examples
* **Create** a new Fn context with the name and data provided.
    * `fn create context <context-name> --api-url http://<host>:<port> --registry <docker-hub-id>`
* **Delete** an Fn context with the specified name.
    * `fn delete context <context-name>`
* **List** all contexts on this system.
    * `fn list <context-name>`
* **Add or Update** key/value data in your context.
    * `fn update context <key> <value>`
* **Update (Delete)** a name/value pair from your context.
    * `fn update context --delete <key>`
