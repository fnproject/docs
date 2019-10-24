# Function initialization and configuration

Function objects and classes may be re-used for multiple function events in the same Java virtual machine depending on how your function is configured and how frequently it is called.
The Fn FDK for Java allows you to perform once-per-runtime initialization behavior that allows you to create long-lived objects such as static data, and database connections that can be re-used across multiple function events.

You can set configuration values on your function's routes and the function app  using the Fn tool or in your `func.yaml`. This configuration can contain deployment-specific data which you do not want to store in your source code.

The function runtime can provide you a [RuntimeContext](../api/src/main/java/com/fnproject/fn/api/RuntimeContext.java) instance that exposes an API to access these configuration variables or [configure data binding](DataBinding.md); this object can be used either in your class's constructor or via configuration methods, as described below.

## Configuring your function in the constructor

All function classes where the function method is not static must have a public constructor.

The easiest way to initialize your function class is to specify a no-arg constructor on your function class:

```java
package com.example.fn;

public class ConstructorConfig {
    private final String greeting ;

    public ConstructorConfig(){
            greeting = "Hello";
    }

    public String handleRequest() {
        return greeting + " world!";
    }
}
```

You can also add a `RuntimeContext` parameter to your constructor as follows:

```java
package com.example.fn;
import com.fnproject.fn.api.RuntimeContext;

public class ConstructorConfig {
    private final String greeting ;

    public ConstructorConfig(RuntimeContext ctx) {
         greeting = ctx.getConfigurationByKey("GREETING").orElse("Hello");
    }

     public String handleRequest() {
            return greeting + " world!";
     }
}
```

## Using function configuration methods

In addition to using the constructor you can also annotate instance or static methods with  [@FnConfiguration](../api/src/main/java/com/fnproject/fn/api/FnConfiguration.java) to initialize your function object or class state.

The configuration method will be called for you once per runtime (i.e. once for multiple invocations of a function) so changes made within this method will apply to all invocations of your function.

This method must have a `void` return value and if your function method is static, the configuration method must also be static.

Given a simple function class:

```java
package com.example.fn;

public class WithConfig {
    public String handleRequest(String input) {
        String name = (input == null || input.isEmpty()) ? "world"  : input;
        return "hello" + name + "!";
    }
}
```

Add configuration to the function class by creating a method annotated with `@FnConfiguration` as follows:

```java
package com.example.fn;

import com.fnproject.fn.api.FnConfiguration;

public class WithConfig {
    private String greeting;

    @FnConfiguration
    public void setUp() {
        greeting = "Hello ";
    }

    public String handleRequest(String input) {
        String name = (input == null || input.isEmpty()) ? "world"  : input;
        return greeting + name + "!";
    }
}
```

As with constructors, if your function configuration method takes a single `RuntimeContext` argument this can be used to get function configuration and configure data binding.

```java
package com.example.fn;

import com.fnproject.fn.api.FnConfiguration;
import com.fnproject.fn.api.RuntimeContext;

public class WithConfig {
    private String greeting;

    @FnConfiguration
    public void setUp(RuntimeContext ctx) {
        greeting = ctx.getConfigurationByKey("GREETING").orElse("Hello");
    }

    public String handleRequest(String input) {
        String name = (input == null || input.isEmpty()) ? "world"  : input;
        return greeting + name + "!";
    }
}
```

### Example: Configuring a Database Connection

Function configuration methods can be used to configure and create persisted database connections that are re-used for multiple function events.

For example:

```java
package com.example.fn;

import com.fnproject.fn.api.FnConfiguration;
import com.fnproject.fn.api.RuntimeContext;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FunctionUsingSomeDatabase {
    private Connection connection;

    @FnConfiguration
    public void setUp(RuntimeContext ctx) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");

        connection = DriverManager.getConnection(ctx.getConfiguration().get("DB_URL"),
                ctx.getConfiguration().get("DB_USER"), ctx.getConfiguration().get("DB_PASSWORD"));
    }

    public String handleRequest(String user) throws Exception {
        try(PreparedStatement userQuery = connection.prepareStatement("SELECT name from USERS where user=?")) {
            userQuery.setString(1, user);
            ResultSet rs = userQuery.executeQuery();
            if(rs.next()) {
                return rs.getString("name");
            } else {
                return "Unknown";
            }
        }
    }
}
```

Note that unless you have concurrent database access within your code, there is no need to use a connection pool as only one event will be processed by a given function runtime at a time.


## Configuration methods and inheritance

Function classes may have multiple configuration methods and may inherit from classes with configuration methods.

When multiple methods are declared in a class hierarchy, all static configuration methods will be called before instance configuration methods and methods declared in super-classes will be called before those in sub-classes.

Multiple configuration methods in the same class are supported but no guarantees are made about the ordering of calls.

When configuration methods are overridden, the `@FnConfiguration` annotation must be added to the overridden method in order for it to be called.

`@FnConfiguration` annotations on interface methods (including default methods and static methods) will be ignored.
