package com.example.chestnovv.myapplication.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MarkBalanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMarkBalanceAll(List<MarkBalance> mark);

    @Query("SELECT * From MarkBalance " +
            "WHERE stamp = :stamp")
    LiveData<MarkBalance> findStamp(String stamp);
}
