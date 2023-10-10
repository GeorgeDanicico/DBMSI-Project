package com.dbms.project.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

@Data
@AllArgsConstructor
public class ForeignKey {
    private List<String> foreignKeyAttributes;
    private String referencedTable;
    private List<String> referencedAttributes;

    public JSONObject toJSON() {
        JSONArray foreignKeyAttributesArray = new JSONArray();
        foreignKeyAttributes.forEach(attr -> foreignKeyAttributesArray.add(attr));
        JSONArray referencedAttributesArray = new JSONArray();
        referencedAttributes.forEach(attr -> referencedAttributesArray.add(attr));

        JSONObject obj = new JSONObject();
        obj.put("foreignKeyAttributes", foreignKeyAttributesArray);
        obj.put("referencedTable", referencedTable);
        obj.put("referencedAttributes", referencedAttributesArray);

        return obj;
    }
}
