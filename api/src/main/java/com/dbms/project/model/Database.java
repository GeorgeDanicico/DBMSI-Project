package com.dbms.project.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Database {
    private String databaseName;
    private List<Table> tables;

    public static Database createEmptyDatabase(String databaseName) {
        return new Database(databaseName, new ArrayList<>());
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        JSONArray tablesArray = new JSONArray();

        tables.forEach(attr -> tablesArray.add(attr.toJSON()));

        obj.put("databaseName", databaseName);
        obj.put("tables", tablesArray);

        return obj;
    }
}
