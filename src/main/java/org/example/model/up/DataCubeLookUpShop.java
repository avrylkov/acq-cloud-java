package org.example.model.up;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
public class DataCubeLookUpShop {

    private final String code;
    private Set<DataCubeLookUpTerminal> terminals = new HashSet<>();

    public DataCubeLookUpTerminal findTerminal(String code) {
        return terminals.stream().filter(f -> f.getCode().equals(code)).findFirst().orElse(null);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataCubeLookUpShop that = (DataCubeLookUpShop) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

}
