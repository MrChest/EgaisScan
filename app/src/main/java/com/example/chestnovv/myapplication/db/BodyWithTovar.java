package com.example.chestnovv.myapplication.db;

import android.arch.persistence.room.ColumnInfo;

public class BodyWithTovar {
    public int id;

    @ColumnInfo(name = "tovar_id")
    public String tovarId;

    @ColumnInfo(name = "description")
    public String tovarDescription;

    public String number;

    public int count;

    public int countStamp;

    public int getId() {
        return id;
    }

    public String getTovarId() {
        return tovarId;
    }

    public String getTovarDescription() {
        return tovarDescription;
    }

    public int getCount() {
        return count;
    }

    public String getNumber() {
        return number;
    }

    public int getCountStamp() {
        return countStamp;
    }
}
