package com.example.chestnovv.myapplication.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity
public class MarkBalance {

    @PrimaryKey
    @NonNull
    @SerializedName("Stamp")
    private String stamp;

    @ColumnInfo(name = "tovar_id")
    @SerializedName("TovarId")
    public String tovarId;

    @SerializedName("Count")
    private int count;

    public void setStamp(@NonNull String stamp) {
        this.stamp = stamp;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @NonNull
    public String getStamp() {
        return stamp;
    }

    public int getCount() {
        return count;
    }

    public String getTovarId() {
        return tovarId;
    }

    public void setTovarId(String tovarId) {
        this.tovarId = tovarId;
    }
}
