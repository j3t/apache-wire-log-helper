package com.github.j3t.apache;

import java.io.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class DecodeGunzipSample {

    public static void main(String... args) throws IOException {
        // pre conditions
        // 1. you have a apache http client wire log
        // 2. the relevant response content type is gzip
        // 3. all messages of the response are copied to content.gzip
        // 4. all lines look like: <TIMESTAMP> DEBUG org.apache.http.wire - http-outgoing-28 << "<MSG>")
        // 5. all characters in each line are removed except <MSG>
        // 6. all lines before the line starting with [0x1f][0x8b] are removed

        // if you need an example how it should looks like see sample.log and content.gzip

        // read gzipped encoded content into a string
        InputStream is = DecodeGunzipSample.class.getResourceAsStream("/content.gzip");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        String content = reader.lines().collect(Collectors.joining());
        reader.close();

        // decode content
        String encodedContent = ApacheWireLogHelper.decodeMessage(content);
        byte[] bytes = encodedContent.getBytes("ISO-8859-15");

        // print out decoded gunzipped content
        reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new ByteArrayInputStream(bytes))));
        reader.lines().forEach(System.out::println);
        reader.close();
    }

}
