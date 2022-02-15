package com.example.chestnovv.myapplication;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.chestnovv.myapplication.db.AppDatabase;
import com.example.chestnovv.myapplication.db.Body;
import com.example.chestnovv.myapplication.db.Document;
import com.example.chestnovv.myapplication.db.DocumentWithCount;
import com.example.chestnovv.myapplication.db.Mark;
import com.example.chestnovv.myapplication.db.MarkBalance;
import com.example.chestnovv.myapplication.repository.Repository;
import com.example.chestnovv.myapplication.db.Tovar;
import com.example.chestnovv.myapplication.repository.Resource;
import com.example.chestnovv.myapplication.service.BasicAuthInterceptor;
import com.example.chestnovv.myapplication.service.DocumentPOJO;
import com.example.chestnovv.myapplication.utils.AbsentLiveData;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import okhttp3.Credentials;


public class MainViewModel extends AndroidViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();
    private Repository mRepository;
    private LiveData<List<DocumentWithCount>> documents;
    private final LiveData<Resource<List<DocumentPOJO>>> respApi;
    private final LiveData<Resource<List<Mark>>> respApiSend;
    private final MutableLiveData<Boolean> refresh = new MutableLiveData<>();
    private final MutableLiveData<String> number = new MutableLiveData<>();
    private final LiveData<Document> document;
    private final MutableLiveData<String> barcode = new MutableLiveData<>();
    private final MutableLiveData<Boolean> refreshBalance = new MutableLiveData<>();
    private final LiveData<Resource<List<MarkBalance>>> respApiBalance;

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplication());

        String baseUrl = preferences.getString(application.getString(R.string.prefHttpService), "");
        String login = preferences.getString(application.getString(R.string.prefLogin), "");
        String password = preferences.getString(application.getString(R.string.prefPassword), "");

        BasicAuthInterceptor auth = new BasicAuthInterceptor(login, password);

        //EgaisService egaisService = DataServiceGenerator.createService(EgaisService.class, baseUrl, auth);

        mRepository = Repository.getInstance(database, auth);

        Log.d(TAG, "Actively retrieving the tasks from the DataBase");

        Calendar cal = GregorianCalendar.getInstance();
        cal.add( Calendar.DAY_OF_YEAR, -15);
        Date oneDaysAgo = cal.getTime();
        documents = mRepository.findAllDocumentsWithCount(oneDaysAgo);

        respApi = Transformations.switchMap(refresh, new Function<Boolean, LiveData<Resource<List<DocumentPOJO>>>>() {
            @Override
            public LiveData<Resource<List<DocumentPOJO>>> apply(Boolean input) {
                if (input==null){
                    return AbsentLiveData.create();
                }
                else {
                    return mRepository.loadDocument();
                }
            }
        });

        respApiSend = Transformations.switchMap(number, new Function<String, LiveData<Resource<List<Mark>>>>() {
            @Override
            public LiveData<Resource<List<Mark>>> apply(String input) {
                if (input==null){
                    return AbsentLiveData.create();
                }
                else{
                    return mRepository.sendMarks(input);
                }
            }
        });

        document = Transformations.switchMap(barcode, new Function<String, LiveData<Document>>() {
            @Override
            public LiveData<Document> apply(String input) {
                if (input==null){
                    return AbsentLiveData.create();
                }
                else {
                    return mRepository.findDocumentByBarcode(input);
                }
            }
        });


        respApiBalance = Transformations.switchMap(refreshBalance, new Function<Boolean, LiveData<Resource<List<MarkBalance>>>>() {
            @Override
            public LiveData<Resource<List<MarkBalance>>> apply(Boolean input) {
                if (input==null){
                    return AbsentLiveData.create();
                }
                else {
                    return mRepository.loadMarkBalance();
                }
            }
        });
    }

    void refresh() {
        this.refresh.setValue(true);
    }

    void refreshBalance(){
        this.refreshBalance.setValue(true);
    }

    void send(String number) {
        this.number.setValue(number);
    }

    void setBarcode(String barcode) {
        this.barcode.setValue(barcode);
    }

    public LiveData<Document> getDocumentByBarcode(){
        return document;
    }

    public LiveData<Resource<List<DocumentPOJO>>> getRespApi() {
        return respApi;
    }

    public LiveData<Resource<List<Mark>>> getRespApiSend() {
        return respApiSend;
    }

    public LiveData<Resource<List<MarkBalance>>> getRespApiBalance() {
        return respApiBalance;
    }

    public LiveData<List<DocumentWithCount>> getDocuments() {
        return documents;
    }

    public void loadDocuments(){
        mRepository.loadDocument();
    }

    public void insertDocument(Document document){
        mRepository.insertDocument(document);
    }

    public void insertDocumentAll(List<Document> documents){
        mRepository.insertDocumentAll(documents);
    }

    public void insertTovarAll(List<Tovar> tovars){
        mRepository.insertTovarAll(tovars);
    }

    public void insertBodyAll(List<Body> bodies){
        mRepository.insertBodyAll(bodies);
    }

    public LiveData<List<Mark>> findMarkByNumberDoc(String number){
        return mRepository.findMarkByNumberDoc(number);
    }

    public void updateIsSendTo1c(String number, boolean isSendTo1c){
        mRepository.updateIsSendTo1c(number, isSendTo1c);
    }

    public void login(String login, String password){
        mRepository.getAuth().setCredentials(Credentials.basic(login, password));
    }

    public void setHostInterceptor(String url){
        mRepository.getHostSelectionInterceptor().setInterceptor(url);
    }

}
