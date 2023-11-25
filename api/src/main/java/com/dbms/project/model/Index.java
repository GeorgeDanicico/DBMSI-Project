package com.dbms.project.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

@SuppressWarnings("unchecked")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Index {
    private String indexName;
    private long isUnique;
    private List<String> indexAttributes;

    public static Index fromJSON(JSONObject idx) {
        Index index = new Index();
        List<String> indexAttributesList = ((JSONArray) idx.get("indexAttributes")).stream()
                .map(k -> (String) k)
                .toList();

        index.setIndexName((String) idx.get("indexName"));
        index.setIsUnique((Long) idx.get("isUnique"));
        index.setIndexAttributes(indexAttributesList);
        return index;
    }

    public JSONObject toJSON() {
        JSONArray indexAttributesArray = new JSONArray();
        indexAttributes.forEach(attr -> indexAttributesArray.add(attr));

        JSONObject obj = new JSONObject();
        obj.put("indexName", indexName);
        obj.put("isUnique", isUnique);
        obj.put("indexAttributes", indexAttributesArray);

        return obj;
    }

    public String getIndexTableName(String tableName) {
        String prefix = isUnique == 1 ? "UK" : "NUK";
        return prefix + "_" + tableName + "_" + indexName;
    }
}
