package com.example.chestnovv.myapplication;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.example.chestnovv.myapplication.db.AppDatabase;
import com.example.chestnovv.myapplication.db.BodyWithTovar;
import com.example.chestnovv.myapplication.db.Document;
import com.example.chestnovv.myapplication.db.Mark;
import com.example.chestnovv.myapplication.db.MarkBalance;
import com.example.chestnovv.myapplication.repository.Repository;
import com.example.chestnovv.myapplication.repository.Resource;
import com.example.chestnovv.myapplication.utils.AbsentLiveData;

import java.util.HashMap;
import java.util.List;

public class BodyViewModel extends ViewModel {

    private Repository mRepository;
    private LiveData<List<BodyWithTovar>> bodyWithTovars;
    private LiveData<Document> mDocument;
    private MutableLiveData<String> mDocumentId = new MutableLiveData<>();
    private final LiveData<Resource<List<Mark>>> respApiSend;
    private MutableLiveData<String> number = new MutableLiveData<>();
    private MutableLiveData<HashMap<String, String>> requestParametrs = new MutableLiveData<>();
    private LiveData<Resource<MarkBalance>> mMarkBalance;

    public BodyViewModel(AppDatabase database, String number) {
        mRepository = Repository.getInstance(database, null);
        bodyWithTovars = mRepository.findBodyByNumberDoc(number);

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

        respApiSend = Transformations.switchMap(this.number, new Function<String, LiveData<Resource<List<Mark>>>>() {
            @Override
            public LiveData<Resource<List<Mark>>> apply(String input) {
                if (input == null) {
                    return AbsentLiveData.create();
                } else {
                    return mRepository.sendMarks(input);
                }
            }
        });

        mMarkBalance = Transformations.switchMap(requestParametrs, new Function<HashMap<String, String>, LiveData<Resource<MarkBalance>>>() {
            @Override
            public LiveData<Resource<MarkBalance>> apply(HashMap<String, String> input) {
                if (input == null) {
                    return AbsentLiveData.create();
                } else {
                    return mRepository.saveMark(input.get("number"), input.get("stamp"));
                }
            }
        });

    }

    public LiveData<List<BodyWithTovar>> getBodyWithTovars() {
        return bodyWithTovars;
    }

    public LiveData<Document> getmDocument() {
        return mDocument;
    }

    public void setDocumentId(String mDocumentId) {
        this.mDocumentId.setValue(mDocumentId);
    }

    void send(String number) {
        this.number.setValue(number);
    }

    public LiveData<Resource<List<Mark>>> getRespApiSend() {
        return respApiSend;
    }

    public LiveData<Resource<MarkBalance>> getmMarkBalance() {
        return mMarkBalance;
    }

    public void saveMark(final String number, final String stamp){
        HashMap<String, String> parametrs = new HashMap<>();
        parametrs.put("number", number);
        parametrs.put("stamp", stamp);
        requestParametrs.setValue(parametrs);
    }
}
