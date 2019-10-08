# Function Development Kits (FDKs)

FDKs are language kits designed to help make working with Fn easier in each language.

Using the `fn init` CLI command will produce a boilerplate function along with the FDK imported and ready to go for runtime specified using `--runtime`. (eg `fn init --runtime go`).

The Fn team has chosen a set of FDKs to initially support, while other FDKs will be labeled as "experimental".

## Officially Supported FDKs

* [Go](https://github.com/fnproject/docs/tree/master/fdks/fdk-go)
* [Java](https://github.com/fnproject/docs/blob/master/fdks/fdk-java/README.md)
* [Python](https://github.com/fnproject/docs/blob/master/fdks/fdk-python/README.md)
* [Ruby](https://github.com/fnproject/docs/tree/master/fdks/fdk-ruby)
* [NodeJS](https://github.com/fnproject/docs/tree/master/fdks/fdk-node)

To search for all FDK work, you can [search the fnproject GitHub org](https://github.com/fnproject?utf8=%E2%9C%93&q=FDK).

## Community Supported FDKs
 * [C# / .NET Core](https://github.com/Daniel15/fdk-dotnet)

## Bring your own Container

Because the Fn Project can use any container, almost any language or package can be used to build functions. The following tutorial helps with this process:

https://fnproject.io/tutorials/ContainerAsFunction/

Also read about the [Fn Container Contract](fn-format.md) for more information.

## Examples
* FDK for Java
    * [Configuration Variables](https://github.com/fnproject/docs/tree/master/fdks/fdk-java/examples/configuration-variables): Set configuration values for applications, function, and in `func.yaml`.
    * [Gradle Build](https://github.com/fnproject/docs/tree/master/fdks/fdk-java/examples/gradle-build): Use a function to build and run a Java app using Gradle.
    * [Regex](https://github.com/fnproject/docs/tree/master/fdks/fdk-java/examples/regex-query): Pass a regex to a function.
    * [Simple Reverse String](https://github.com/fnproject/docs/tree/master/fdks/fdk-java/examples/string-reverse): Reverse the letters in a string.
