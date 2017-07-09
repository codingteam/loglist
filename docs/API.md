LogList API
===========

LogList public API consists of multiple HTTP endpoints designed to produce
various data. All parameters should be passed as query string parameters; the
responses are always in JSON format.

CORS
----

If you're calling LogList API from the browser, you should follow the [CORS
specification][cors]. Usually it's handled by the browser automatically; the
only thing you need is to set up the `cors.allowedOrigins` parameter in the
server configuration file.

If you want your origin to be added to the main LogList configuration (so your
browser agents could access our main [loglist.net][] instance), please [raise an
issue](https://github.com/codingteam/loglist/issues).

API Types
---------

This section explains some query parameter types that will be used in the API
methods.

### Quote Ordering

- `time`: reverse sorts the quotes by their publication time (i.e. newest first)
- `rating`: reverse sorts the quotes by their rating (i.e. top rated first)

### Quote Filtering

- `none`: no filtering, show all
- `year`: show quotes posted from the start of the current UTC year
- `month`: show quotes posted from the start of the current UTC month
- `week`: show quotes posted from the start of the current UTC week, where
  "week" starts on Monday according to [ISO 8601][iso-8601-weeks]
- `day`: show quotes posted from the start of the current UTC day

API Methods
-----------

### Quote access

#### Post the quote

Send `POST` request to `/api/quote/new`. Quote should have the following form:

```json
{
    "text": "quote text",
    "apiKey": "xxx"
}
```

To get your secret API key, please contact the server administrator.

Example:

```console
$ curl --include --header "Content-type: application/json" --request POST --data "{\"text\": \"xxx\", \"apiKey\": \"xxx\"}" https://loglist.net/api/quote/new
HTTP/1.1 200 OK
```

The new quote will be posted into the approval queue.

#### Get quote by id

Send `GET` request to `/api/quote/xxxx`, where `xxxx` is quote id. Example:

```
GET /api/quote/7921
=> { "id": "7921",
     "source": "loglist.ru",
     "sourceUrl": "http://loglist.ru/",
     "time": "1418549262553", # UTC timestamp
     "content": "<quote content>",
     "rating": 5 }
```

(e.g. https://loglist.net/api/quote/8119)

#### Get random quote

Send `GET` request to `/api/quote/random`. Example:

```
GET /api/quote/random
=> { "id": "7921",
     "source": "loglist.ru",
     "sourceUrl": "http://loglist.ru/",
     "time": "1418549262553",
     "content": "<quote content>",
     "rating": 5 }
```

(e.g. https://loglist.net/api/quote/random)

#### Get quote count

Send `GET` request to `/api/quote/count`. Example:

```
GET /api/quote/count
=> { "count": 7921 }
```

(e.g. https://loglist.net/api/quote/count)

#### Get suggested quote count

Send `GET` request to `/api/quote/count/suggested`. Example:

```
GET /api/quote/count/suggested
=> { "count": 7921 }
```

(e.g. https://loglist.net/api/quote/count/suggested)

#### Get quote list

Send `GET` request to `/api/quote/list?limit=x&page=y&order=o&filter=f`, where:

- `limit` is quote count per page between `1` and `1000` inclusive, `50` by
  default
- `page` is page number starting from `0`; `0` by default
- `order` is quote ordering mode, see above
- `filter` is quote filtering mode, see below

```
GET /api/quote/list?limit=10&page=1&filter=none&order=time
=> [{ "id": "7911",
      "source": "loglist.ru",
      "sourceUrl": "http://loglist.ru/",
      "time": "1301903967000",
      "content": "<quote text>",
      "rating": 0 },
    { "id": "7912",
      "source": "loglist.ru",
      "sourceUrl": "http://loglist.ru/",
      "time": "1301903967001",
      "content": "<quote text>",
      "rating": 0 }]
```

(e.g. https://loglist.net/api/quote/list?limit=10&page=1&filter=none&order=time)

### Voting access

Send `POST` request to `/api/quote/xxxx/upvote` or `/api/quote/xxxx/downvote`,
where `xxxx` is quote id. The server will report the new quote rating value.
Examples:

```
POST /api/quote/1234/upvote
=> { "rating": 9001 }
```

```
POST /api/quote/1234/downvote
=> { "rating": 9000 }
```

[cors]: https://developer.mozilla.org/en-US/docs/Web/HTTP/Access_control_CORS
[iso-8601-weeks]: https://en.wikipedia.org/wiki/ISO_8601#Week_dates
[loglist.net]: https://loglist.net/