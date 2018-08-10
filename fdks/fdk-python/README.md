# FN development kit for Python

Purpose of this library to provide simple interface to parse HTTP 1.1 requests represented as string

Following examples are showing how to use API of this library to work with streaming HTTP requests from Fn service.

## Handling Hot JSON Functions

A main loop is supplied that can repeatedly call a user function with a series of HTTP requests.
In order to utilise this, you can write your `app.py` as follows:

```py
import fdk


def handler(context, data=None, loop=None):
    return data


if __name__ == "__main__":
    fdk.handle(handler)

```

Applications powered by Fn: Concept
-----------------------------------

FDK is not only about developing functions, but providing necessary API to build serverless applications
that look like nothing but classes with methods powered by Fn.

```py
import requests

from fdk.application import decorators


@decorators.fn_app
class Application(object):

    def __init__(self, *args, **kwargs):
        pass

    @decorators.with_fn(fn_image="denismakogon/fdk-python-echo:0.0.1")
    def env(self, fn_data=None, **kwargs):
        return fn_data

    @decorators.fn(fn_type="sync")
    def square(self, x, y, *args, **kwargs):
        return x * y

    @decorators.fn(fn_type="sync", dependencies={
        "requests_get": requests.get
    })
    def request(self, *args, **kwargs):
        requests_get = kwargs["dependencies"].get("requests_get")
        r = requests_get('https://api.github.com/events')
        r.raise_for_status()
        return r.text


if __name__ == "__main__":
    app = Application(config={})

    res, err = app.env()
    if err:
        raise err
    print(res)

    res, err = app.traceback()
    if err:
        raise err
    print(res)

    res, err = app.square(10, 20)
    if err:
        raise err
    print(res)

    res, err = app.request()
    if err:
        raise err
    print(res)

```
In order to identify to which Fn instance code needs to talk set following env var:

```bash
    export FN_API_URL = http://localhost:8080
```
with respect to IP address or domain name where Fn lives.


## Applications powered by Fn: supply data to a function


At this moment those helper - decorators let developers interact with Fn - powered functions as with regular class methods.
In order to pass necessary data into a function developer just needs to do following
```py

if __name__ == "__main__":
    app = Application(config={})

    app.env(keyone="blah", keytwo="blah", somethingelse=3)

```
Key - value args will be turned into JSON instance and will be sent to a function as payload body.


## Applications powered by Fn: working with function's result


In order to work with result from function you just need to read key - value argument `fn_data`:
```py
    @decorators.with_fn(fn_image="denismakogon/py-traceback-test:0.0.1",
                        fn_format="http")
    def traceback(self, fn_data=None):
        return fn_data
```

## Applications powered by Fn: advanced serverless functions

Since release v0.0.3 developer can consume new API to build truly serverless functions
without taking care of Docker images, application, etc.

```py
    @decorators.fn(fn_type="sync")
    def square(self, x, y, *args, **kwargs):
        return x * y

    @decorators.fn(fn_type="sync", dependencies={
        "requests_get": requests.get
    })
    def request(self, *args, **kwargs):
        requests_get = kwargs["dependencies"].get("requests_get")
        r = requests_get('https://api.github.com/events')
        r.raise_for_status()
        return r.text
```

Each function decorated with `@decorator.fn` will become truly serverless and distributed.
So, how it works?

    * A developer writes function
    * FDK(Fn - powered app) creates a recursive Pickle v4.0 with 3rd - party dependencies
    * FDK(Fn - powered app) transfers pickled object to a function based on Python3 GPI(general purpose image)
    * FDK unpickles function and its 3rd - party dependencies and runs it
    * Function sends response back to Fn - powered application function caller

So, each CPU - intensive functions can be sent to Fn with the only load on networking(given example creates 7kB of traffic between app's host and Fn).


## Applications powered by Fn: exceptions


Applications powered by Fn are following Go - like errors concept. It gives you full control on errors whether raise them or not.
```py
    res, err = app.env()
    if err:
        raise err
    print(res)

```
Each error is an instance fn `FnError` that encapsulates certain logic that makes hides HTTP errors and turns them into regular Python - like exceptions.
