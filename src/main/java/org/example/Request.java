package org.example;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;


@Data
public class Request {
    private Map<String, String> queryStringParameters = new HashMap<>();
    private String body = "";
}
