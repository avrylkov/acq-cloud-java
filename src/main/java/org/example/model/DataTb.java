package org.example.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class DataTb {

    private String code;
    private Map<String, Long> metrics = new HashMap<>();
    private List<DataGosb> gosb = new ArrayList<>();

}
