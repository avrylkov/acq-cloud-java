package org.example.model;

import lombok.Data;

@Data
public class Metric {

    public Metric() {
        this("", 0L);
    }

    public Metric(String name, Long value) {
        this.name = name;
        this.value = value;
    }

    private String name;
    private Long value;
}
