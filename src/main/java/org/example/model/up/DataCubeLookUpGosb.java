package org.example.model.up;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
public class DataCubeLookUpGosb {

    private final String code;
    private Set<DataCubeLookUpOrganization> organizations = new HashSet<>();;

    public DataCubeLookUpOrganization findOrganization(String code) {
        return organizations.stream().filter(f -> f.getCode().equals(code)).findFirst().orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataCubeLookUpGosb that = (DataCubeLookUpGosb) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
