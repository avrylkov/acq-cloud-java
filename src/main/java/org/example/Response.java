package org.example;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Response {
    private int statusCode;
    private Map<String, String> headers = new HashMap<>();
    private Boolean isBase64Encoded;
    private String body;

    public Response(int statusCode, Map<String, String> headers, Boolean isBase64Encoded, String body) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.isBase64Encoded = isBase64Encoded;
        this.body = body;
    }
}
