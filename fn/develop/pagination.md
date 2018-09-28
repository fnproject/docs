# Pagination

The Fn api utilizes "cursoring" to paginate large result sets on endpoints
that list resources. The parameters are read from query parameters on incoming
requests, and a cursor will be returned to the user if they receive a full
page of data to use to retrieve the next page. We'll walk through with a
concrete example in just a minute.

To begin paging through a results set, a user should provide a `?cursor` with an
empty string or omit the cursor query parameter altogether. A user may specify
how many results per page they would like to receive with the `?per_page`
query parameter, which defaults to 30 and has a max of 100. After calling a
list endpoint, a user may receive a `response.next_cursor` value in the
response, next to the list of resources. If `next_cursor` is an empty string,
then there is no further data to retrieve and the user may stop paging. If
`next_cursor` is a non-empty string, the user may provide it in the next
request's `?cursor` parameter to receive the next page.

Briefly, what this means, is user code should look similar to this:

```
req = "http://my.fn.com/v1/apps/"
cursor = ""

for {
  req_with_cursor = req + "?" + cursor
  resp = call_http(req_with_cursor)
  do_things_with_apps(resp["apps"])

  if resp["next_cursor"] == "" {
    break
  }
  cursor = resp["next_cursor"]
}

# done!
```

Client libraries will have variables for each of these variables in their
respective languages to make this a bit easier.
