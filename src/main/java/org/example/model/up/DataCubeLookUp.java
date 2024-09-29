package org.example.model.up;

import lombok.Data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class DataCubeLookUp {

    private Set<DataCubeLookUpTb> tbs = new HashSet<>();

    public DataCubeLookUpTb findTb(String code) {
        return tbs.stream()
                .filter(f -> f.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

}
