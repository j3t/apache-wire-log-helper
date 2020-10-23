# apache-wire-log-helper
Tool to decrypt [ApacheHttpClient](https://hc.apache.org) logs which contain gzipped messages.

## How is this different to a normal log message?
In wire logs, character 10 is replaced with `[\n]`, character 13 with `[\r]` and characters < 32 and > 127 with 
`[0xN]` where N is char's hexadecimal representation (see 
[org.apache.hc.client5.http.impl.logging.Wire](http://svn.apache.org/viewvc/httpcomponents/httpclient/trunk/httpclient5/src/main/java/org/apache/hc/client5/http/impl/logging/Wire.java?view=markup#l46) for more details).

## How it works?
Your log file looks like the following
```
[Jan 04 2015 05:38:14.109 AM] DEBUG wire:72 - http-outgoing-2 >> "POST /app HTTP/1.1[\r][\n]"
[Jan 04 2015 05:38:14.109 AM] DEBUG wire:72 - http-outgoing-2 >> "User-Agent: Mozilla/5.0[\r][\n]"
[Jan 04 2015 05:38:14.109 AM] DEBUG wire:72 - http-outgoing-2 >> "Content-Length: 47[\r][\n]"
[Jan 04 2015 05:38:14.109 AM] DEBUG wire:72 - http-outgoing-2 >> "Content-Type: application/x-www-form-urlencoded[\r][\n]"
[Jan 04 2015 05:38:14.109 AM] DEBUG wire:72 - http-outgoing-2 >> "Host: test.com[\r][\n]"
[Jan 04 2015 05:38:14.109 AM] DEBUG wire:72 - http-outgoing-2 >> "Connection: Keep-Alive[\r][\n]"
[Jan 04 2015 05:38:14.110 AM] DEBUG wire:72 - http-outgoing-2 >> "Accept-Encoding: gzip,deflate[\r][\n]"
[Jan 04 2015 05:38:14.110 AM] DEBUG wire:72 - http-outgoing-2 >> "[\r][\n]"
[Jan 04 2015 05:38:14.489 AM] DEBUG wire:72 - http-outgoing-2 << "HTTP/1.1 200 OK[\r][\n]"
[Jan 04 2015 05:38:14.489 AM] DEBUG wire:72 - http-outgoing-2 << "Server: nginx[\r][\n]"
[Jan 04 2015 05:38:14.490 AM] DEBUG wire:72 - http-outgoing-2 << "Date: Sun, 04 Jan 2015 10:37:31 GMT[\r][\n]"
[Jan 04 2015 05:38:14.490 AM] DEBUG wire:72 - http-outgoing-2 << "Content-Type: text/html;charset=UTF-8[\r][\n]"
[Jan 04 2015 05:38:14.490 AM] DEBUG wire:72 - http-outgoing-2 << "Transfer-Encoding: chunked[\r][\n]"
[Jan 04 2015 05:38:14.490 AM] DEBUG wire:72 - http-outgoing-2 << "Connection: keep-alive[\r][\n]"
[Jan 04 2015 05:38:14.490 AM] DEBUG wire:72 - http-outgoing-2 << "Vary: Accept-Encoding[\r][\n]"
[Jan 04 2015 05:38:14.490 AM] DEBUG wire:72 - http-outgoing-2 << "Pragma: no-cache[\r][\n]"
[Jan 04 2015 05:38:14.490 AM] DEBUG wire:72 - http-outgoing-2 << "Expires: Thu, 01 Jan 1970 00:00:00 GMT[\r][\n]"
[Jan 04 2015 05:38:14.491 AM] DEBUG wire:72 - http-outgoing-2 << "Cache-Control: no-cache[\r][\n]"
[Jan 04 2015 05:38:14.491 AM] DEBUG wire:72 - http-outgoing-2 << "Cache-Control: no-store[\r][\n]"
[Jan 04 2015 05:38:14.491 AM] DEBUG wire:72 - http-outgoing-2 << "Set-Cookie: JSESSIONID=0C1444D7E0FA6B0F3CCE20AFBA28237E; Path=/; HttpOnly[\r][\n]"
[Jan 04 2015 05:38:14.491 AM] DEBUG wire:72 - http-outgoing-2 << "Front-End-Https: on[\r][\n]"
[Jan 04 2015 05:38:14.491 AM] DEBUG wire:72 - http-outgoing-2 << "Content-Encoding: gzip[\r][\n]"
[Jan 04 2015 05:38:14.491 AM] DEBUG wire:72 - http-outgoing-2 << "[\r][\n]"
[Jan 04 2015 05:38:14.491 AM] DEBUG wire:72 - http-outgoing-2 << "37[\r][\n]"
[Jan 04 2015 05:38:14.492 AM] DEBUG wire:72 - http-outgoing-2 << "[0x1f][0x8b][0x8][0x0][0x0][0x0][0x0][0x0][0x0][0x3][0xb3])J-.[0xc8][0xcf]+N[0xb5][0xe3][0xb2][0x81]1[0xe3][0x93][0xf3]S[0x80][0xfc][0xe0][0xd2][0xe4][0xe4][0xd4][0xe2]b.[0x1b]}4[0x9][0x84][0x80][0x1d][0x17][0x0]<Xn]@[0x0][0x0][0x0][\r][\n]"
[Jan 04 2015 05:38:14.492 AM] DEBUG wire:72 - http-outgoing-2 << "0[\r][\n]"
[Jan 04 2015 05:38:14.492 AM] DEBUG wire:72 - http-outgoing-2 << "[\r][\n]"
````

You are interested in the content received.
````
[Jan 04 2015 05:38:14.491 AM] DEBUG wire:72 - http-outgoing-2 << "[\r][\n]"
[Jan 04 2015 05:38:14.491 AM] DEBUG wire:72 - http-outgoing-2 << "37[\r][\n]"
[Jan 04 2015 05:38:14.492 AM] DEBUG wire:72 - http-outgoing-2 << "[0x1f][0x8b][0x8][0x0][0x0][0x0][0x0][0x0][0x0][0x3][0xb3])J-.[0xc8][0xcf]+N[0xb5][0xe3][0xb2][0x81]1[0xe3][0x93][0xf3]S[0x80][0xfc][0xe0][0xd2][0xe4][0xe4][0xd4][0xe2]b.[0x1b]}4[0x9][0x84][0x80][0x1d][0x17][0x0]<Xn]@[0x0][0x0][0x0][\r][\n]"
[Jan 04 2015 05:38:14.492 AM] DEBUG wire:72 - http-outgoing-2 << "0[\r][\n]"
[Jan 04 2015 05:38:14.492 AM] DEBUG wire:72 - http-outgoing-2 << "[\r][\n]"
````

Relevant is the part starts with `[0x1f][0x8b]`. That's are the first two bytes of the gzipped content.
````
[0x1f][0x8b][0x8][0x0][0x0][0x0][0x0][0x0][0x0][0x3][0xb3])J-.[0xc8][0xcf]+N[0xb5][0xe3][0xb2][0x81]1[0xe3][0x93][0xf3]S[0x80][0xfc][0xe0][0xd2][0xe4][0xe4][0xd4][0xe2]b.[0x1b]}4[0x9][0x84][0x80][0x1d][0x17][0x0]<Xn]@[0x0][0x0][0x0][\r][\n]
````

Decoded, unzipped and printed out ...
````
String gzipped = ApacheWireLogHelper.decodeMessage("[0x1f][0x8b] ...");

InputStream is = new GZIPInputStream(new ByteArrayInputStream(gzipped.getBytes("ISO-8859-15")))
BufferedReader reader = new BufferedReader(new InputStreamReader(is));

reader.lines().forEach(System.out::println);
````

you get ...
````
<response>
<response_code>
Success
</response_code>
</response>
````

See [DecodeGunzipSample.java](./src/test/java/com/github/j3t/apache/DecodeGunzipSample.java) for a full example.
