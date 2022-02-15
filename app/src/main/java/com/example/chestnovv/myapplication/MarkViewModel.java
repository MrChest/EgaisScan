package com.example.chestnovv.myapplication;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;

import com.example.chestnovv.myapplication.db.AppDatabase;
import com.example.chestnovv.myapplication.db.Document;
import com.example.chestnovv.myapplication.db.Mark;
import com.example.chestnovv.myapplication.db.MarkBalance;
import com.example.chestnovv.myapplication.repository.Repository;
import com.example.chestnovv.myapplication.db.Tovar;
import com.example.chestnovv.myapplication.repository.Resource;
import com.example.chestnovv.myapplication.utils.AbsentLiveData;

import java.util.HashMap;
import java.util.List;

public class MarkViewModel extends ViewModel {

    private Repository mRepository;
    private LiveData<List<Mark>> marks;
    private LiveData<Tovar> tovar;
    private LiveData<Document> mDocument;
    private MutableLiveData<String> mDocumentId = new MutableLiveData<>();

    private MutableLiveData<HashMap<String, String>> requestParametrs = new MutableLiveData<>();
    private MutableLiveData<HashMap<String, String>> deleteParametrs = new MutableLiveData<>();

    private LiveData<Resource<MarkBalance>> mMarkBalance;
    private LiveData<Resource<Boolean>> mDeleteMark;


    public MarkViewModel(AppDatabase db, String number, String tovarId){
        mRepository = Repository.getInstance(db, null);
        marks = db.markDao().findMarkByNumberDocAndTovarId(number, tovarId);
        tovar = db.tovarDao().findTovarById(tovarId);

        mDocument = Transformations.switchMap(mDocumentId, new Function<String, LiveData<Document>>() {
            @Override
            public LiveData<Document> apply(String input) {
                if (input==null || input.isEmpty()){
                    return AbsentLiveData.create();
                }
                else {
                    return mRepository.findDocumentByNumber(input);
                }
            }
        });

        mMarkBalance = Transformations.switchMap(requestParametrs, new Function<HashMap<String, String>, LiveData<Resource<MarkBalance>>>() {
            @Override
            public LiveData<Resource<MarkBalance>> apply(HashMap<String, String> input) {
                if (input==null){
                    return AbsentLiveData.create();
                }
                else{
                    return mRepository.saveMark(input.get("number"), input.get("tovarId"), input.get("stamp"));
                }
            }
        });

        mDeleteMark = Transformations.switchMap(deleteParametrs, new Function<HashMap<String, String>, LiveData<Resource<Boolean>>>() {
            @Override
            public LiveData<Resource<Boolean>> apply(HashMap<String, String> input) {
                if (input==null){
                    return AbsentLiveData.create();
                }
                else{
                    return mRepository.deleteMark(input.get("number"), input.get("stamp"));
                }
            }
        });
    }

    public LiveData<List<Mark>> getMarks() {
        return marks;
    }

    public LiveData<Tovar> getTovar() {
        return tovar;
    }

    public void insertMark(Mark mark){
        mRepository.insertMark(mark);
    }

    public void remoteMarkById(String id){
        mRepository.removeMarkById(id);
    }

    public LiveData<Document> getmDocument() {
        return mDocument;
    }

    public void setDocumentId(String mDocumentId) {
        this.mDocumentId.setValue(mDocumentId);
    }

    public void saveMark(final String number, final String tovarId, final String stamp){
        HashMap<String, String> parametrs = new HashMap<>();
        parametrs.put("number", number);
        parametrs.put("tovarId", tovarId);
        parametrs.put("stamp", stamp);
        requestParametrs.setValue(parametrs);
    }

    public void remoteMarkById(final String number, final String stamp){
        HashMap<String, String> parametrs = new HashMap<>();
        parametrs.put("number", number);
        parametrs.put("stamp", stamp);
        deleteParametrs.setValue(parametrs);
    }

    public LiveData<Resource<MarkBalance>> getmMarkBalance() {
        return mMarkBalance;
    }

    public LiveData<Resource<Boolean>> getmDeleteMark() {
        return mDeleteMark;
    }
}
