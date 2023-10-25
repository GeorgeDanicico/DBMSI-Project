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
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ForeignKey {
    private List<String> foreignKeyAttributes;
    private String referencedTable;
    private List<String> referencedAttributes;

    public static ForeignKey fromJSON(JSONObject key) {
        ForeignKey foreignKey = new ForeignKey();
        List<String> foreignKeyAttributesList = ((JSONArray) key.get("foreignKeyAttributes")).stream()
                .map(k -> (String) k)
                .toList();
        List<String> referencedAttributesList = ((JSONArray) key.get("referencedAttributes")).stream()
                .map(k -> (String) k)
                .toList();

        foreignKey.setReferencedTable((String) key.get("referencedTable"));
        foreignKey.setForeignKeyAttributes(foreignKeyAttributesList);
        foreignKey.setReferencedAttributes(referencedAttributesList);
        return foreignKey;
    }

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
