package org.example.model.deep;

import lombok.Data;
import org.example.model.Metric;

import java.util.ArrayList;
import java.util.List;

@Data
public class DataCube {

    private final String code;
    private List<Metric> metrics = new ArrayList<>();

}
