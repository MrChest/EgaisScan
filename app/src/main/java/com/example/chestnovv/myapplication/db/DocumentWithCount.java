package com.example.chestnovv.myapplication.db;

import java.util.Date;

public class DocumentWithCount {

    private String number;
    private String numberInovice;
    private Date date;
    private String title;
    private int count;
    private int countStamp;
    private Double summ;
    public boolean isSendToEgais;
    public boolean isSendTo1c;

    public Date getDate() {
        return date;
    }

    public String getNumber() {
        return number;
    }

    public String getTitle() {
        return title;
    }

    public int getCount() {
        return count;
    }

    public int getCountStamp() {
        return countStamp;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setCountStamp(int countStamp) {
        this.countStamp = countStamp;
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
}
