package com.example.chestnovv.myapplication.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity(indices = {@Index("number")})//indices = {@Index(value = "number", unique = true)}
@TypeConverters(DateConverter.class)
public class Document {

    @PrimaryKey
    @NonNull
    public String number;
    public String numberInovice;
    public String barcode;
    public Date date;
    public String title;
    public Double summ;
    public boolean isSendToEgais;
    public boolean isSendTo1c;

    public Document(@NonNull String number, Date date, String title, Double summ, boolean isSendToEgais, boolean isSendTo1c, String numberInovice, String barcode) {
        this.number = number;
        this.date = date;
        this.title = title;
        this.summ = summ;
        this.isSendToEgais = isSendToEgais;
        this.isSendTo1c = isSendTo1c;
        this.numberInovice = numberInovice;
        this.barcode = barcode;
    }

    public Date getDate() {
        return date;
    }

    public String getNumber() {
        return number;
    }

    public String getTitle() {
        return title;
    }

    public Double getSumm() {
        return summ;
    }

    public boolean isSendToEgais() {
        return isSendToEgais;
    }

    public boolean isSendTo1c() {
        return isSendTo1c;
    }

    public String getNumberInovice() {
        return numberInovice;
    }

    public String getBarcode() {
        return barcode;
    }
}
