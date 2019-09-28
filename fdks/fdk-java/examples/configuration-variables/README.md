# Setting Configuration Variables

You can get configuration variables into a function and make them available as environment variables.

The `configuration-variables` function creates a database connection string. This function has four variables: `DB_URL`, `DB_DRIVER`, `DB_USER`, and `DB_PASSWORD`.
 
The `DB_DRIVER` configuration variable is set during deployment and it is defined in the `func.yaml` file.
The rest of the configuration variables are set while configuring the application and the function.

## Step by step: Set the configuration values 
Ensure you have the Fn server running to host your function.

(1) Start the server.

```sh
$ fn start
```

(2) Go to the `configuration-variables` directory.

```sh
$ cd configuration-variables
```

(3) Create an app for the function.

```sh
fn create app configuration-variables-app
```

(4) At the application level, configure the `DB_URL` environment variable.

```sh
fn config app configuration-variables-app DB_URL jdbc:mysql
```

(5) At the application level, configure the `DB_USER` environment variable.

```sh
fn config app configuration-variables-app DB_USER superadmin
```

(6) At the function level, configure the `DB_PASSWORD` environment variable.

```sh
fn config function configuration-variables-app configuration-variables DB_PASSWORD superadmin
```

(7) Deploy the application.

```sh
fn deploy --app configuration-variables-app --local
```

(8) Invoke the `configuration-variables` function.

```sh
fn invoke configuration-variables-app configuration-variables
```

