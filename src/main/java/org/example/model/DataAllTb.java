package org.example.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class DataAllTb {

    private String code = "Все ТБ";
    private Map<String, Long> metrics = new HashMap<>();
    private List<DataTb> tb = new ArrayList<>();

}
