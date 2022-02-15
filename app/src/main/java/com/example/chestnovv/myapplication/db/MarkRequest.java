package com.example.chestnovv.myapplication.db;

import java.util.Date;

public class MarkRequest {

    public String number;

    public Date date;

    public String stamp;

    public String getNumber() {
        return number;
    }

    public Date getDate() {
        return date;
    }

    public String getStamp() {
        return stamp;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setStamp(String stamp) {
        this.stamp = stamp;
    }
}
