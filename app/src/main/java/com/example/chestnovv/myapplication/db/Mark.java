package com.example.chestnovv.myapplication.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(indices = {@Index("document_number"), @Index("tovar_id")},
        foreignKeys = {
        @ForeignKey(entity = Document.class,
                parentColumns = "number",
                childColumns = "document_number"),

        @ForeignKey(entity = Tovar.class,
                parentColumns = "id",
                childColumns = "tovar_id")})
public class Mark {

    @PrimaryKey
    @NonNull
    public String stamp;

    @ColumnInfo(name = "document_number")
    public String documentId;

    @ColumnInfo(name = "tovar_id")
    public String tovarId;

    public int count;

    public Mark(String documentId, String tovarId, String stamp, int count) {
        this.documentId = documentId;
        this.tovarId = tovarId;
        this.stamp = stamp;
        this.count = count;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getTovarId() {
        return tovarId;
    }

    public String getStamp() {
        return stamp;
    }

    public int getCount() {
        return count;
    }
}
