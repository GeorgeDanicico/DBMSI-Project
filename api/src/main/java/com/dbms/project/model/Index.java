package com.dbms.project.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class Index {
    private String indexName;
    private int isUnique;
    private List<String> indexAttributes;

    public JSONObject toJSON() {
        JSONArray indexAttributesArray = new JSONArray();
        indexAttributes.forEach(attr -> indexAttributesArray.add(attr));

        JSONObject obj = new JSONObject();
        obj.put("indexName", indexName);
        obj.put("isUnique", isUnique);
        obj.put("indexAttributes", indexAttributesArray);

        return obj;
    }
}
