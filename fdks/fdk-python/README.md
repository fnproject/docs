# FN development kit for Python

Purpose of this library to provide simple interface to parse HTTP 1.1 requests.

Following examples are showing how to use API of this library to work with streaming HTTP requests from Fn service.

## Handling JSON Functions

A main loop is supplied that can repeatedly call a user function with a series of HTTP requests.
In order to utilise this, you can write your `func.py` as follows:

```py
import json
import io

from fdk import response


def handler(ctx, data: io.BytesIO=None):
    name = "World"
    try:
        body = json.loads(data.getvalue())
        name = body.get("name")
    except (Exception, ValueError) as ex:
        print(str(ex))

    return response.Response(
        ctx, response_data=json.dumps(
            {"message": "Hello {0}".format(name)}), 
        headers={"Content-Type": "application/json"}
    )

```
