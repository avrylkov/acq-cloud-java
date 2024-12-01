package org.example.model.up;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PageDataLookUp {

    private final Integer total;
    private List<DataCubeLookUpTb> dataCubes = new ArrayList<>();

}
