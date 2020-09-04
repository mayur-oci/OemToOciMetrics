package com.oss.oem.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Schema {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("fields")
    @Expose
    private List<Field> fields = null;
    @SerializedName("optional")
    @Expose
    private Boolean optional;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public Boolean getOptional() {
        return optional;
    }

    public void setOptional(Boolean optional) {
        this.optional = optional;
    }

}
