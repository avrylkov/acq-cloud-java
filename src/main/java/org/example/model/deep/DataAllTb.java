package org.example.model.deep;

import lombok.Data;
import org.example.model.Metric;

import java.util.ArrayList;
import java.util.List;

@Data
public class DataAllTb {

    private String code = "Все ТБ";
    private List<Metric> metrics = new ArrayList<>();
    private List<DataTb> tb = new ArrayList<>();

}
