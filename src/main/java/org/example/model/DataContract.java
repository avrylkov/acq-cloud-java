package org.example.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class DataContract {

    private String code;
    private Map<String, Long> metrics = new HashMap<>();

}
