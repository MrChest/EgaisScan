package com.example.chestnovv.myapplication;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.chestnovv.myapplication.db.AppDatabase;

public class BodyViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDb;
    private final String mNumber;

    public BodyViewModelFactory(AppDatabase database, String number) {
        mDb = database;
        mNumber = number;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new BodyViewModel(mDb, mNumber);
    }
}
