package com.dbms.project.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Table {
    private String tableName;
    private List<Attribute> attributes;
    private PrimaryKey primaryKey;
    private List<ForeignKey> foreignKeys;
    private List<String> uniqueKeys;
    private List<Index> indexes;

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("tableName", tableName);
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

    public static Table fromJSON(JSONObject obj) {
        Table table = new Table();
        PrimaryKey pk = PrimaryKey.fromJSON((JSONObject) obj.get("primaryKey"));
        List<String> uniqueKeysList = ((JSONArray) obj.get("uniqueKeys")).stream()
                        .map(key -> (String) key)
                        .toList();
        List<ForeignKey> foreignKeyList = ((JSONArray) obj.get("foreignKeys")).stream()
                .map(key -> ForeignKey.fromJSON((JSONObject) key))
                .toList();
        List<Attribute> attributesList = ((JSONArray) obj.get("attributes")).stream()
                .map(key -> Attribute.fromJSON((JSONObject) key, pk, uniqueKeysList))
                .toList();
        List<Index> indexesList = ((JSONArray) obj.get("indexes")).stream()
                .map(key -> Index.fromJSON((JSONObject) key))
                .toList();

        table.setTableName((String) obj.get("tableName"));
        table.setPrimaryKey(pk);
        table.setUniqueKeys(uniqueKeysList);
        table.setAttributes(attributesList);
        table.setForeignKeys(foreignKeyList);
        table.setIndexes(indexesList);
        return table;
    }
}
