package com.dbms.project.model;

import java.util.Comparator;
import java.util.Map;

public class ColumnComparator implements Comparator<Map<String, String>> {
    private String joinColumn;

    public ColumnComparator(String joinColumn) {
        this.joinColumn = joinColumn;
    }

    @Override
    public int compare(Map<String, String> o1, Map<String, String> o2) {
        String val1 = o1.get(joinColumn);
        String val2 = o2.get(joinColumn);

        return val1.compareTo(val2);
    }
}
