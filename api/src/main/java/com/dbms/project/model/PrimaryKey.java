package com.dbms.project.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrimaryKey {
    List<String> pkAttributes;

    public JSONObject toJSON() {
        JSONArray pkAttributesArray = new JSONArray();
        pkAttributes.forEach(attr -> pkAttributesArray.add(attr));

        JSONObject obj = new JSONObject();
        obj.put("pkAttributes", pkAttributesArray);

        return obj;
    }
}
