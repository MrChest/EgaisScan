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
                childColumns = "document_number"
        , onDelete = 5),

        @ForeignKey(entity = Tovar.class,
                parentColumns = "id",
                childColumns = "tovar_id")})
public class Body {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int id;
    @ColumnInfo(name = "document_number")
    public String documentId;
    @ColumnInfo(name = "tovar_id")
    public String tovarId;
    @ColumnInfo(name = "egais_id")
    public String egaisId;

    public int count;


    public Body(String documentId, String tovarId, String egaisId, int count) {
        this.documentId = documentId;
        this.tovarId = tovarId;
        this.egaisId = egaisId;
        this.count = count;
    }
}
