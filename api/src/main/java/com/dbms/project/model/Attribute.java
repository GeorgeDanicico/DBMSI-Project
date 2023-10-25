package com.dbms.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;
import org.junit.Ignore;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Attribute {
    private String attributeType;
    private String attributeName;
    @JsonIgnore
    private Boolean isPrimaryKey;
    @JsonIgnore
    private Boolean isUniqueKey;
    private Long isNull;

    public static Attribute fromJSON(JSONObject attribute, PrimaryKey pk, List<String> uniqueKeys) {
        Attribute attr = new Attribute();
        attr.setAttributeName((String) attribute.get("attributeName"));
        attr.setAttributeType((String) attribute.get("attributeType"));
        attr.setIsNull((Long) attribute.get("isNull"));
        if (pk.getPkAttributes().contains(attr.getAttributeName())) {
            attr.setIsPrimaryKey(true);
        } else attr.setIsPrimaryKey(false);
        if (uniqueKeys.contains(attr.getAttributeName())) {
            attr.setIsUniqueKey(true);
        } else attr.setIsUniqueKey(false);
        return attr;
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("attributeName", attributeName);
        obj.put("attributeType", attributeType);
        obj.put("isNull", isNull);

        return obj;
    }
}
