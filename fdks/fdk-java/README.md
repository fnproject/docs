# Fn Java Functions Developer Kit - Docs and Examples
[![CircleCI](https://circleci.com/gh/fnproject/fdk-java.svg?style=svg&circle-token=348bec5610c34421f6c436ab8f6a18e153cb1c01)](https://circleci.com/gh/fnproject/fdk-java)

This page provides links to docs and examples on how to use the Java Functions Development Kit (Java FDK) to develop applications.

## Docs
* [Data Binding for function input and output](DataBinding.md)
* [Extending the data binding functionality](ExtendingDataBinding.md)
* [Function initialization and configuration](FunctionConfiguration.md)
* [Accessing HTTP information From functions](HTTPGatewayFunction.md)
* [Spring cloud functions with Fn](SpringCloudFunctionSupport.md)
* [Testing your functions](TestingFunctions.md)

## Examples
* [String reverse](examples/string-reverse/README.md): This function takes a string and returns the reverse of the string. The Java FDK handles marshalling data into your
function without the function having any knowledge of the FDK API.
* [Regex query](examples/regex-query/README.md): This function takes a JSON object containing a `text` field
and a `regex` field and returns a JSON object with a list of matches in the `matches` field. It
demonstrates the builtin JSON support of the Fn Java wrapper (provided through Jackson) and how the
platform handles serialization of POJO return values.
* [QR Code gen](examples/qr-code/README.md): This function parses the query parameters of a GET request (through the `InputEvent` passed into the function) to generate a QR code. It demonstrates
the `InputEvent` and `OutputEvent` interfaces which provide low level access to data entering the Fn Java FDK.
* [Asynchronous thumbnails generation](examples/async-thumbnails/README.md): This example showcases the Fn Flow asynchronous execution API, by creating a workflow that takes an image and asynchronously generates three thumbnails for it, then uploads them to an object storage.
* [Gradle build](examples/gradle-build/README.md): This shows how to use Gradle to build functions using the Java FDK. 

## Contribute to the Java FDK
If wish to contribute to the Java FDK development see our [Contributing to Fn Guide](https://github.com/fnproject/fn/tree/master/docs#for-contributors).

For details on the Java FDK Development see the [Java FDK Repo](https://github.com/fnproject/fdk-java).

