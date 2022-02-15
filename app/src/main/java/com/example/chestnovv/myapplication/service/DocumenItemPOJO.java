package com.example.chestnovv.myapplication.service;

import com.google.gson.annotations.SerializedName;

public class DocumenItemPOJO {
    @SerializedName("Code")
    private String code;
    @SerializedName("Name")
    private String name;
    @SerializedName("Count")
    private Integer count;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
