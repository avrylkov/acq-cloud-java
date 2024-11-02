package org.example;

import com.jsoniter.JsonIterator;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Base64Util;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
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
        headers.put("Content-Type", "application/json"); // "application/json text/plain"

//        String name = request.queryStringParameters.get("name");
//        return new Response(statusCode, headers, isBase64Encoded, String.format("Hello, %s!", name));

        InputStream resourceAsStream = Main.class.getResourceAsStream("/json/data.json");
        String strResource = null;
        try {
            String body = request.getBody();
            //String body = new String(Base64.getDecoder().decode(request.getBody()));
            strResource = new String(resourceAsStream.readAllBytes());
            Request requestResource = JsonIterator.deserialize(strResource, Request.class);
            return new Response(statusCode, headers, isBase64Encoded, String.format("Hello, %s, %s!. time=%s",
                    requestResource.getQueryStringParameters().get("name"),
                    body,
                    formatter.format(localDate))
            );
        } catch (Exception e) {
            log.error("err", e);
            return new Response(statusCode, headers, isBase64Encoded, String.format("Error, %s!", e.getMessage()));
        }
    }
}