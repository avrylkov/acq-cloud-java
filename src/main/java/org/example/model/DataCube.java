package org.example.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DataCube {

    public DataCube(String code) {
        this.code = code;
    }

    private String code;
    private List<Metric> metrics = new ArrayList<>();

}
