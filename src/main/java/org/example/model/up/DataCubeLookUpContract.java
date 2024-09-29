package org.example.model.up;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Objects;


@Getter
@Setter
@RequiredArgsConstructor
public class DataCubeLookUpContract {

    private final String code;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataCubeLookUpContract that = (DataCubeLookUpContract) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
