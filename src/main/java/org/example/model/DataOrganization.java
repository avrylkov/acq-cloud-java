package org.example.model;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class DataOrganization {

    private String code;
    private Map<String, Long> metrics = new HashMap<>();
    private List<DataContract> contract;
}
