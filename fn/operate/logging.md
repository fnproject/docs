# Logging

There are a few things to note about what Fn logs.

## Logspout

We recommend using [logspout](https://github.com/gliderlabs/logspout) to forward your logs to a log aggregator of your choice.

## Format

All logs are emitted in [logfmt](https://godoc.org/github.com/kr/logfmt) format for easy parsing.

Each line contains timestamp, app id, and function id. Here's a sample:

```txt
2018-11-21T13:50:28-05:00 linuxkit-025000000001 app_id=01CV2SYZ9NNG8G00RZJ0000001,fn_id=01CWVSNBD0NG8G00RZJ00000 Caused by: java.lang.RuntimeException: Something went horribly wrong!
```

## Remote syslog for functions

You may add a syslog url to any function application and all functions that
exist under that application will ship all of their logs to it. You may
provide a comma separated list, if desired. Currently, we support `tcp`,
`udp`, and `tcp+tls`, and this will not work if behind a proxy [yet?] (this is
my life now).

An example syslog url is:

```
tcp+tls://logs.papertrailapp.com:1
```

We log in a syslog format, with some variables added in logfmt format. If you
find logfmt format offensive, please open an issue and we will consider adding
more formats (or open a PR that does it, with tests, and you will receive 1
free cookie along with the feature you want). The logs from the functions
themselves are not formatted, only our pre-amble, thus, if you'd like a fully
logfmt line, you must use a logfmt logger to log from your function.

* All log lines are sent as level error w/ the current time and `fnrunner` as hostname.
* app_id will prefix every log line.

```
<11>2 1982-06-25T12:00:00Z fnrunner - - - - app_id=54321 this is your log line
```
