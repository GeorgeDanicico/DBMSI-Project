package com.dbms.project.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class Table {
    private String tableName;
    private String fileName;
    private List<Attribute> attributes;
    private PrimaryKey primaryKey;
    private List<ForeignKey> foreignKeys;
    private List<String> uniqueKeys;
    private List<Index> indexes;

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("tableName", tableName);
        obj.put("fileName", fileName);
        JSONArray attributesArray = new JSONArray();
        JSONArray foreignKeysArray = new JSONArray();
        JSONArray indexesArray = new JSONArray();
        JSONArray uniqueKeysArray = new JSONArray();

        attributes.forEach(attr -> attributesArray.add(attr.toJSON()));
        foreignKeys.forEach((fk -> foreignKeysArray.add(fk.toJSON())));
        indexes.forEach(index -> indexesArray.add(index.toJSON()));
        uniqueKeys.forEach(key -> uniqueKeysArray.add(key));

        obj.put("primaryKey", primaryKey.toJSON());
        obj.put("attributes", attributesArray);
        obj.put("foreignKeys", foreignKeysArray);
        obj.put("uniqueKeys", uniqueKeysArray);
        obj.put("indexes", indexesArray);

        return obj;
    }
}
