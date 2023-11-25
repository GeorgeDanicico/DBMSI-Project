package com.dbms.project.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Operation {
    EQUAL("EQUAL"),
    LESS_THAN("LESS_THAN"),
    GREATER_THAN("GREATER_THAN"),
    LIKE("LIKE");

    private final String operation;

}
