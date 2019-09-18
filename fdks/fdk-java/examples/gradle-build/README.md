# Example Java Functions: Fn Gradle + Java fdk  

This example shows you how to build a Java function by using a Dockerfile.


## Key points:

* [Dockerfile](Dockerfile) - contains the containerized Docker build (based on dockerhub library/gradle images) and image build - this includes the gradle invocation
* The `cacheDeps` task in `build.gradle` pulls down dependencies into the container gradle cache to speed up docker builds. 
* The `copyDeps` task in `build.gradle` copies the functions compile deps.
* This uses JDK 11 by default

## Step by step

Ensure you have the Fn server running to host your function:

(1) Start the server

```sh
$ fn start
```

(2) Create an app for the function

```sh
$ fn create app gradle-build-app
```

(3) Deploy the function to your app from the `gradle-build` directory.

```sh
fn deploy --app gradle-build-app --local
```

(4) Invoke the function 

```sh
fn invoke gradle-build-app gradle_build
```

 