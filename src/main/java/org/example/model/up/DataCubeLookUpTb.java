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
public class DataCubeLookUpTb {

    private final String code;
    private Set<DataCubeLookUpGosb> gosbs = new HashSet<>();

    public DataCubeLookUpGosb findGosb(String code) {
        return gosbs.stream().filter(f -> f.getCode().equals(code)).findFirst().orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataCubeLookUpTb that = (DataCubeLookUpTb) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
