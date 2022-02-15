package com.example.chestnovv.myapplication.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface TovarDao {
    @Query("SELECT * From Tovar")
    LiveData<List<Tovar>> findAllTovars();

    @Query("SELECT * From Tovar " +
            "WHERE id = :id")
    LiveData<Tovar> findTovarById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTovar(Tovar tovar);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTovarAll(List<Tovar> tovars);

    @Query("DELETE FROM Tovar")
    void deleteAll();
}
