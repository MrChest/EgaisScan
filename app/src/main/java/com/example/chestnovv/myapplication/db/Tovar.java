package com.example.chestnovv.myapplication.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Tovar {

    @PrimaryKey
    @NonNull
    public String id;
    public String description;

    public Tovar(@NonNull String id, String description) {
        this.id = id;
        this.description = description;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}
