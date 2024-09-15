package org.example.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DataGosb {

    private String code;
    private List<Metric> metrics = new ArrayList<>();
    private List<DataOrganization> organization;

}
