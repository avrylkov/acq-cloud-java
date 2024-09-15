package org.example.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DataTb {

    private String code;
    private List<Metric> metrics = new ArrayList<>();
    private List<DataGosb> gosb = new ArrayList<>();

}
