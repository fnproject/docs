# Function Development Kit for Ruby 

The Function Development Kit for Ruby (FDK for Ruby) provides a Ruby framework for developing `functions` for us with [Fn](https://fnproject.github.io).

## Function Handler

To use this FDK, you simply need to require this gem.

```ruby
require 'fdk`
```

Then create a function with with the following syntax:

```ruby
def myfunc(context, input)
    # Do some work here
    return output
end
```

* context - provides runtime information for your function, such as configuration values, headers, etc.
* input – This parameter is a string containing body of the request.
* output - is where you can return data back to the caller. Whatever you return will be sent back to the caller.
  * Default output format should be in JSON, as Content-Type header will be `application/json` by default. You can be more flexible if you create and return
    an FDK::Response object instead of a string.

Then simply pass that function to the FDK:

```ruby
FDK.handle(myfunction)
```

## Full Example

```ruby
require 'fdk'

def myfunction(context:, input:)
  input_value = input.respond_to?(:fetch) ? input.fetch('name') : input
  name = input_value.to_s.strip.empty? ? 'World' : input_value
  { message: "Hello #{name}!" }
end

FDK.handle(function: :myfunction)
```

## Running the example that is in the root directory of this repo

```sh
$ echo '{"name":"coolio"}' | fn run
{"message":"Hello coolio!"}
```

You can also specify the format (the default is JSON)

```sh
$ echo '{"name":"coolio"}' | fn run --format json
{"message":"Hello coolio!"}
```

If you want to just pass plain text to the function, specify a format of __default__:

```sh
$ echo 'coolio' | fn run --format default
{"message":"Hello coolio!"}
```

Deploy:

```sh
fn deploy --app myapp --local && echo '{"name":"coolio"}' | fn call myapp /fdk-ruby
```

Change to hot:

Update func.yaml: `format: json`

```sh
fn deploy --app myapp --local && echo '{"name":"coolio"}' | fn call myapp /fdk-ruby
```

## Compare cold and hot

Run

```sh
ruby loop.rb
```
