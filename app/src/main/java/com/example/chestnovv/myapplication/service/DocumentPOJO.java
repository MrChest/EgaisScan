package com.example.chestnovv.myapplication.service;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DocumentPOJO {
    @SerializedName("Number")
    private String number;
    @SerializedName("NumberInovice")
    private String numberInovice;
    @SerializedName("Barcode")
    private String barcode;
    @SerializedName("Date")
    private String date;
    @SerializedName("Title")
    private String title;
    @SerializedName("Summ")
    private Double summ;
    @SerializedName("isSendToEgais")
    private boolean isSendToEgais;
    @SerializedName("isSendTo1c")
    private boolean isSendTo1c;

    @SerializedName("items")
    private List<DocumenItemPOJO> items = null;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<DocumenItemPOJO> getItems() {
        return items;
    }

    public void setItems(List<DocumenItemPOJO> items) {
        this.items = items;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getSumm() {
        return summ;
    }

    public void setSumm(Double summ) {
        this.summ = summ;
    }

    public boolean isSendToEgais() {
        return isSendToEgais;
    }

    public void setSendToEgais(boolean sendToEgais) {
        isSendToEgais = sendToEgais;
    }

    public boolean isSendTo1c() {
        return isSendTo1c;
    }

    public void setSendTo1c(boolean sendTo1c) {
        isSendTo1c = sendTo1c;
    }

    public String getNumberInovice() {
        return numberInovice;
    }

    public void setNumberInovice(String numberInovice) {
        this.numberInovice = numberInovice;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}
