package org.example.model.deep;

import lombok.Data;
import org.example.model.Metric;

import java.util.ArrayList;
import java.util.List;

@Data
public class DataGosb {

    private String code;
    private List<Metric> metrics = new ArrayList<>();
    private List<DataOrganization> organization;

}
