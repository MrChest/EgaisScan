package com.example.chestnovv.myapplication.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;

import java.util.List;

@Dao
@TypeConverters(DateConverter.class)
public interface BodyDao {
    @Query("SELECT Body.id, Body.tovar_id, Tovar.description, Body.count, Document.number , sum(Mark.count) as countStamp From Body  "+
            "INNER JOIN Tovar ON Body.tovar_id = Tovar.id " +
            "INNER JOIN Document ON Body.document_number = Document.number " +
            "LEFT JOIN Mark ON Body.document_number = Mark.document_number AND Body.tovar_id = Mark.tovar_id " +
            "WHERE Document.number = :number " +
            "GROUP BY Document.number, Body.tovar_id")
    LiveData<List<BodyWithTovar>> findBodyByNumberDoc(String number);



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBody(Body body);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBodyAll(List<Body> bodys);

    @Query("DELETE FROM Body")
    void deleteAll();
}

