package org.example.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DataAllTb {

    private String code = "Все ТБ";
    private List<Metric> metrics = new ArrayList<>();
    private List<DataTb> tb = new ArrayList<>();

}
