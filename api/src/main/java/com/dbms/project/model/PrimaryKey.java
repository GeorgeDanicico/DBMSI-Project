package com.dbms.project.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrimaryKey {
    List<String> pkAttributes;

    public static PrimaryKey fromJSON(JSONObject primaryKey) {
        PrimaryKey pk = new PrimaryKey();
        JSONArray pkAttributes = (JSONArray) primaryKey.get("pkAttributes");
        List<String> pkAttributesList = pkAttributes.stream()
                .map(key -> (String)key)
                .toList();
        pk.setPkAttributes(pkAttributesList);

        return pk;
    }

    public void addAttribute(String attribute) {
        pkAttributes.add(attribute);
    }

    public String getPk() {
        if (pkAttributes.size() == 1) {
            return pkAttributes.get(0);
        }
        return String.join(",", pkAttributes);
    }

    public JSONObject toJSON() {
        JSONArray pkAttributesArray = new JSONArray();
        pkAttributes.forEach(attr -> pkAttributesArray.add(attr));

        JSONObject obj = new JSONObject();
        obj.put("pkAttributes", pkAttributesArray);

        return obj;
    }
}
