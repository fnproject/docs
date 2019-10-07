# Fn Java Example Project: Regex Query
This example provides an HTTP endpoint for performing regex matching on strings.

```bash
$ curl -d '{"text":"One, 2, Three, 4, Five", "regex":"\\d"}' 'http://localhost:8080/t/regex-query/query'
{"regex":"\\d","text":"One, 2, Three, 4, Five","matches":[{"start":5,"end":6,"match":"2"},{"start":15,"end":16,"match":"4"}]}
```


## FDK features

The Function Development Kit for Java (FDK for Java) API supports JSON input and
output through [Jackson](https://github.com/FasterXML/jackson). This example
function demonstrates the input/output mapping through use of POJO data transfer
objects.

## Step by step

Ensure you have the functions server running using, this will host your
function and provide the HTTP endpoints that invoke it:

```bash
$ fn start
```

Create an app to host the function
```bash
$ fn create app regex-query
```

Deploy the function to the Fn server.
```bash
$ fn --verbose deploy --app regex-query --local
```

Invoke the function to perform a regex search
```bash
$ curl -d '{ "text": "One, 2, Three, 4, Five", "regex": "\\\\d" }' 'http://localhost:8080/r/regex-query/query'
{"regex":"\\d","text":"One, 2, Three, 4, Five","matches":[{"start":5,"end":6,"match":"2"},{"start":15,"end":16,"match":"4"}]}
```


## Code walkthrough

The entrypoint to the function is specified in `func.yaml` in the `cmd` key.
This is set to be `com.example.fn.RegexQuery::query`.
```js
schema_version: 20180708
name: query
version: 0.0.1
runtime: java
build_image: fnproject/fn-java-fdk-build:jdk11-1.0.96
run_image: fnproject/fn-java-fdk:jre11-1.0.96
cmd: com.example.fn.RegexQuery::query
triggers:
- name: query
  type: http
  source: /query
```

The class containing the function is shown below:

```java
package com.fnproject.fn.examples;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexQuery {
    public Response query(Query query) {
        return new Response(query.regex, query.text, getMatches(query));
    }

    private List<Match> getMatches(Query query) {
        Pattern pattern = Pattern.compile(query.regex);
        Matcher matcher = pattern.matcher(query.text);
        List<Match> matches = new ArrayList<>();
        while (matcher.find()) {
            matches.add(new Match(matcher.start(), matcher.end(), query.text.substring(matcher.start(), matcher.end())));
        }
        return matches;
    }
}
```

The Fn FDK for Java has Jackson coercion support out of the box, annotate your
objects with Jackson annotations or use POJOs and Jackson will attempt to coerce
them to and from JSON. The FDK supports a variety of coercions and provides an
extensibility point for you to inject your own custom coercions see [Extending
Data Binding](../../ExtendingDataBinding.md).

### Testing

The Fn FDK for Java provides a testing module in `com.fnproject.fn.testing`
providing a DSL to easily functionally test your code

`com.fnproject.fn.examples.RegexQueryTests` demonstrates the use
of the `com.fnproject.fn.testing` module to functionally test your function.

A [JUnit rule](https://github.com/junit-team/junit4/wiki/rules)
field takes care of faking the `fn` server, the rule annotation manages
the lifetime of the fake server. `fn.buildEvent()` create an event
builder to construct and queue events into the function under test.

```java
...
    @Rule
    public FnTesting fn = FnTesting.createDefault();
...
```

The function is invoked using `fn.thenRun(<FunctionClass>,
<FunctionMethodName>)` and assertions use the familiar `Assert.assert*` family
of static methods (we make use of the
[JSONAssert](http://jsonassert.skyscreamer.org/) library to simplify testing
JSON APIs). A variety of methods live on `FnTesting` to get a handle on
result(s).

```java
...
    @Test
    public void matchingSingleCharacter() throws JSONException {
        String text = "a";
        String regex = ".";
        fn.givenEvent()
                .withMethod("POST")
                .withBody(String.format("{\"text\": \"%s\", \"regex\": \"%s\"}", text, regex))
                .enqueue();

        fn.thenRun(RegexQuery.class, "query");

        JSONAssert.assertEquals(String.format("{\"text\":\"%s\"," +
                        "\"regex\":\"%s\"," +
                        "\"matches\":[" +
                        "{\"start\": 0, \"end\": 1, \"match\": \"a\"}" +
                        "]}", text, regex),
                fn.getOnlyResult().getBodyAsString(), false);
    }
...
```

See the [Testing Functions](../../TestingFunctions.md) docs for more information
on testing.
