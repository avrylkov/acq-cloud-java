package org.example.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class DataCube {

    public DataCube(String code) {
        this.code = code;
    }

    private String code;
    private Map<String, Long> metrics = new HashMap<>();

}
