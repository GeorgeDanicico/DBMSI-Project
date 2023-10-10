package com.dbms.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;
import org.junit.Ignore;

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
    private Integer isNull;

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("attributeName", attributeName);
        obj.put("attributeType", attributeType);
        obj.put("isNull", isNull);

        return obj;
    }
}
