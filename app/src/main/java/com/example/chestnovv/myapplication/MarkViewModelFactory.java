package com.example.chestnovv.myapplication;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.chestnovv.myapplication.db.AppDatabase;
import com.example.chestnovv.myapplication.db.Tovar;

public class MarkViewModelFactory extends ViewModelProvider.NewInstanceFactory  {

    private final AppDatabase mDb;
    private final String mNumber;
    private final String mTovarId;

    public MarkViewModelFactory(AppDatabase database, String number, String tovarId) {
        mDb = database;
        mNumber = number;
        mTovarId = tovarId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MarkViewModel(mDb, mNumber, mTovarId);
    }
}
