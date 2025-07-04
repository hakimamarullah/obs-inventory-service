package com.sg.obs.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Optional;

@Getter
public enum InventoryType {
    T("Top-Up"),
    W("Withdrawal");

    private final String label;

    InventoryType(String label) {
        this.label = label;
    }

    @JsonCreator
    public static InventoryType fromString(String value) {
        String val = Optional.of(value).map(String::trim).orElseThrow(() -> new IllegalArgumentException("Invalid value: " + value));
        return InventoryType.valueOf(val.toUpperCase());
    }
}
