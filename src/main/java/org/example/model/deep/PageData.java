package org.example.model.deep;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PageData {

    private final Integer total;
    private List<DataCube> dataCubes = new ArrayList<>();

}
