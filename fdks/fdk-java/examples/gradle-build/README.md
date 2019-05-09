# Fn Gradle + Java fdk example 

Fn uses Maven by default for  builds. This is an example that uses Fn's `docker` runtime format to build a Java function using the [Fn Java FDK](https://github.com/fnproject/fdk-java).

The example consists of a `Dockerfile` that builds the function using gradle and copies the function's dependencies to `build/deps` and a func.yaml that uses that `Dockerfile` to build the function.

Note that fdk.versions are hard-coded in this example, you may need to update them manually to more recent version. 

Key points:

* [Dockerfile](Dockerfile) - contains the containerised docker build (based on dockerhub library/gradle images) and image build - this includes the gradle invocation
* The `cacheDeps` task in `build.gradle` is invoked within the Dockerfile - The task pulls down dependencies into the container gradle cache to speed up docker builds. 
* The `copyDeps` task in `build.gradle` copies the functions compile deps 
* This uses JDK 8 by default  - you can change this to Java 11 by changing :  `FROM gradle:4.5.1-jdk8 as build-stage` to `FROM gradle:4.5.1-jre11 as build-stage` and  `FROM fnproject/fn-java-fdk:1.0.85` to `FROM fnproject/fn-java-fdk:jre11-1.0.85` 