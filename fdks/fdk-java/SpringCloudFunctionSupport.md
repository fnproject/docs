# Spring cloud functions with Fn

[Spring cloud function](https://github.com/spring-cloud/spring-cloud-function)
is a [Spring](https://spring.io/) project for building Java functions with the
(optional) support of the spring framework.

We support execution of Spring cloud functions through the
`SpringCloudFunctionInvoker` which manages the discovery and invocation of your
functions.

## Tutorial

The minimal necessary function you must provide is:

```yaml
cmd: com.example.fn.FunctionConfig::handleRequest
```

```java
package com.example.fn;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@Import(ContextFunctionCatalogAutoConfiguration.class)
public class FunctionConfig {
    public static String consumedValue;
    public static String suppliedValue = "Hello";

    @FnConfiguration
    public static void configure(RuntimeContext ctx) {
        ctx.setInvoker(new SpringCloudFunctionInvoker(FunctionConfig.class));
    }


    // Blank entrypoint necessary to load the FunctionConfig class, this isn't invoked
    public void handleRequest() { }
	
	@Bean
	public Function<String, String> upperCase(){
	    return String::toUpperCase;
	}
}
```

