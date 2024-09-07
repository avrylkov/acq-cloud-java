package org.example.model;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class DataGosb {

    private String code;
    private Map<String, Long> metrics = new HashMap<>();
    private List<DataOrganization> organization;

}
