package org.example.model.up;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
public class DataCubeLookUpOrganization {

    private final String code;
    private Set<DataCubeLookUpContract> contracts = new HashSet<>();

    public DataCubeLookUpContract findContract(String code) {
        return contracts.stream().filter(f -> f.getCode().equals(code)).findFirst().orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataCubeLookUpOrganization that = (DataCubeLookUpOrganization) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

}
