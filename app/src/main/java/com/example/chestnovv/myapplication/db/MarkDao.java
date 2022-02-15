package com.example.chestnovv.myapplication.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MarkDao {

    @Query("SELECT * FROM mark "+
            "WHERE document_number = :number and tovar_id = :tovarId")
    LiveData<List<Mark>> findMarkByNumberDocAndTovarId(String number, String tovarId);

    @Query("SELECT * FROM mark "+
            "WHERE document_number = :number")
    LiveData<List<Mark>> findMarkByNumberDoc(String number);

    @Query("SELECT stamp, count From mark " +
            "WHERE document_number = :number AND tovar_id = :tovarId AND stamp = :stamp")
    LiveData<MarkBalance> findStampByNumberAndTovarId(String number, String tovarId, String stamp);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMark(Mark mark);

    @Query("DELETE FROM Mark")
    void deleteAll();

    @Query("DELETE FROM Mark "+
            "WHERE stamp = :stamp")
    void deleteMarkById(String stamp);
}
