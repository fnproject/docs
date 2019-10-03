# Configuration Variables

You can get configuration variables into a function and make them available as environment variables.

The `configuration-variables` function creates a database connection string. This function has four variables: `DB_URL`, `DB_DRIVER`, `DB_USER`, and `DB_PASSWORD`.
 
The `DB_URL`, `DB_DRIVER`, `DB_USER`, and `DB_PASSWORD` environment variables are set on different configuration levels. 
For example, the value of the `DB_DRIVER` variable is read from the `func.yaml` file in the `config` key. The `DB_URL` and `DB_USER`
environment variables are set in the application configuration level, and the `DB_PASSWORD` environment variable is set in the 
function configuration level.

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
$ fn create app connection-app
```

(4) At the application level, configure the `DB_URL` environment variable.

```sh
$ fn config app connection-app DB_URL jdbc:mysql
```

(5) At the application level, configure the `DB_USER` environment variable.

```sh
$ fn config app connection-app DB_USER superadmin
```

(6) Deploy the application.

```sh
$ fn deploy --app connection-app --local
```
(7) At the function level, configure the `DB_PASSWORD` environment variable.

```sh
$ fn config function connection-app connection DB_PASSWORD superadmin
```

(8) Invoke the `connection` function.

```sh
$ fn invoke connection-app connection
driver: mysqlDriver; url: jdbc:mysql; user: superadmin; password: superadmin
```

## Code walkthrough

The entrypoint to the function is specified in `func.yaml` in the `cmd` key.
It is set to `com.example.fn.Connection::getConnection`. The whole class
`Connection` is shown below:

```java
package com.example.fn;

import com.fnproject.fn.api.FnConfiguration;
import com.fnproject.fn.api.RuntimeContext;


public class Connection {			
	private String url;
	private String driver;
	private String user;
	private String password;  
	
	@FnConfiguration
    public void config(RuntimeContext ctx) {
		//Set value at the application configuration level
    	url = ctx.getConfigurationByKey("DB_URL")
    			.orElse("jdbc:oracle");
    	//Set value in the func.yaml
    	driver = ctx.getConfigurationByKey("DB_DRIVER")
    			.orElse("OracleDriver");
    	//Set value at the application configuration level
    	user = ctx.getConfigurationByKey("DB_USER")
    			.orElse("admin");	
    }
    
  
    public String getUrl() {	
    	return  url;
    }

    
    public String getDriver() {
    	return  driver;        
    }
    
	
    public String getUser() {
        return  user;        
    }
    
	//Set value at the function configuration level
    public String getPassword() {
        password = System.getenv().getOrDefault("DB_PASSWORD", "admin");
    	return  password;        
    }
    
    public String getConnection() {
    	return "driver: " + getDriver() + "; url: " + url + "; user: " + getUser() + "; password: " + getPassword();
    }
}

```


