# Create and Run Functions with Fn
This how-to covers how to create and test functions.

## Create a Boilerplate Function with Init
Fn provides the `init` option to setup or create a function you can test. To created a "Hello World" function using boilerplate code type: 

    fn init --runtime node nodefn

The command generates four files: 

* `func.js` - A boilerplate Node.js JavaScript function.
* `func.yaml` - A boilerplate YAML file containing boilerplate configuration options for the function.
* `package.json` - A list of packages required for the function.
* `test.json` - A file for running tests on your function.

The key Fn configuration file here is the `func.yaml` file. The file created from the sample command would be: 

```yaml
name: nodefn
version: 0.0.1
runtime: node
entrypoint: node func.js
format: json
```

For more information on possible value in the `func.yaml` file see **Link to funcfile doc**.

## Set the Version Number when Creating a Function
By default, when the `func.yaml` file is created, the version is set to `0.0.1`. If you with to start with a different number use the version command.

    fn init --version 1.0.1  --runtime node t1

This creates a `func.yaml` file with a version set to `1.01`.

```yaml
name: t1
version: 1.0.1
runtime: node
entrypoint: node func.js
format: json
```

## Create a `func.yaml` using an Existing Function File
You can create a `func.yaml` file from an existing function file. For example, if i have a function named `func.js` in a directory named `t3`. Run the init command:

    fn init

Fn will find your function file, determine it is a type Node and generate a func.yaml for you. For example

```yaml
name: t3
version: 0.0.1
runtime: node
entrypoint: node func.js
format: json
```




