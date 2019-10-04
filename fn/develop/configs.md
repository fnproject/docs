# Use of environment variables

Do you want to use configuration variables into a function and make them available as environment variables? You can set configuration variables using the Fn CLI or the `func.yaml` file.
 
By using the Fn CLI, your can set configuration variables on the application or function level.

# Set environment variables

The following three examples shows you how to set the configuration variables on different levels using the `connection-app` sample application. The configuration examples are listed in order of precedent, the later examples override the previous ones. For example, adding a function key/value pair overrides an application key/value pair with the same name.

(1) Application level configuration

Configuration variables set at the application level apply to all functions contained in that application.
 
```sh
fn config app <app-name> <key> <value>
```
For example, set the `DB_URL` configuration variable with value of `jdbc:mysql`. 
```sh
$ fn config app connection-app DB_URL jdbc:mysql
```

(2) Function configuration from `func.yaml` file

Use the `func.yaml` file to define your configuration variables in the `config` key. For example: set the `DB_DRIVER` configuration variable with value of `mysqlDriver`.

```sh
schema_version: 20180708
name: connection
version: 0.0.16
runtime: java
build_image: fnproject/fn-java-fdk-build:jdk11-1.0.99
run_image: fnproject/fn-java-fdk:jre11-1.0.99
cmd: com.example.fn.Connection::getConnection
config:
  DB_DRIVER: mysqlDriver
```
See [Function file](func-file.md) for more info.

(3) Function level configuration

After you deploy your function, you can set function level configuration variables. The variables only apply to the specific function identified.
```sh
fn config function <app-name> <fn-name> <key> <value>
```

For example, set the `DB_PASSWORD` configuration variable with value of `superadmin`.
```sh
$ fn config function connection-app connection DB_PASSWORD superadmin
```


 
# Get environment variables

You can use your environment variable in your function by using a `RuntimeContext` instance or the `getenv()` method of the `System` class.


The following example shows the `config` function configuration method, which is used to get the values of the `DB_URL`,`DB_DRIVER`, and  `DB_USER` configuration variables. The `getPassword` method gets the value of the `DB_PASSWORD` configuration variable by using the `System` class.

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
	url = ctx.getConfigurationByKey("DB_URL")
    			.orElse("jdbc:oracle");
    	driver = ctx.getConfigurationByKey("DB_DRIVER")
    			.orElse("OracleDriver");
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
    
	public String getPassword() {
        password = System.getenv().getOrDefault("DB_PASSWORD", "admin");
    	return  password;        
    }
    
    public String getConnection() {
    	return "driver: " + getDriver() + "; url: " + url + "; user: " + getUser() + "; password: " + getPassword();
    }
}
```

See [configuration-variables example application](../../fdks/fdk-java/examples/configuration-variables) for more info.






