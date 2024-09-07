package org.example;

import com.jsoniter.JsonIterator;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
public class Handler implements Function<Request, Response> {
    private Integer statusCode = 200;
    private Boolean isBase64Encoded = false;

    private static LocalDateTime localDate = LocalDateTime.now();
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Response apply(Request request) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/plain");

//        String name = request.queryStringParameters.get("name");
//        return new Response(statusCode, headers, isBase64Encoded, String.format("Hello, %s!", name));

        InputStream resourceAsStream = Main.class.getResourceAsStream("/json/data.json");
        String str = null;
        try {
            str = new String(resourceAsStream.readAllBytes());
            Request request2 = JsonIterator.deserialize(str, Request.class);
            log.info(request2.toString());
            return new Response(statusCode, headers, isBase64Encoded, String.format("Hello, %s!. time=%s",
                    request2.getQueryStringParameters().get("name"), formatter.format(localDate)));
        } catch (IOException e) {
            return new Response(statusCode, headers, isBase64Encoded, String.format("Hello, %s!", e.getMessage()));
        }
    }
}