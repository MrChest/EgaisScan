package com.example.chestnovv.myapplication.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.chestnovv.myapplication.db.AppDatabase;
import com.example.chestnovv.myapplication.db.Body;
import com.example.chestnovv.myapplication.db.BodyDao;
import com.example.chestnovv.myapplication.db.BodyWithTovar;
import com.example.chestnovv.myapplication.db.Document;
import com.example.chestnovv.myapplication.db.DocumentDao;
import com.example.chestnovv.myapplication.db.DocumentWithCount;
import com.example.chestnovv.myapplication.db.Mark;
import com.example.chestnovv.myapplication.db.MarkBalance;
import com.example.chestnovv.myapplication.db.MarkBalanceDao;
import com.example.chestnovv.myapplication.db.MarkDao;
import com.example.chestnovv.myapplication.db.Tovar;
import com.example.chestnovv.myapplication.db.TovarDao;
import com.example.chestnovv.myapplication.service.ApiResponse;
import com.example.chestnovv.myapplication.service.BasicAuthInterceptor;
import com.example.chestnovv.myapplication.service.DataServiceGenerator;
import com.example.chestnovv.myapplication.service.DocumenItemPOJO;
import com.example.chestnovv.myapplication.service.DocumentPOJO;
import com.example.chestnovv.myapplication.service.EgaisService;
import com.example.chestnovv.myapplication.service.HostSelectionInterceptor;
import com.example.chestnovv.myapplication.service.ResponseServer;
import com.example.chestnovv.myapplication.utils.AbsentLiveData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;


public class Repository {
    private static Repository sInstance;
    private static AppDatabase mDb;
    private static EgaisService egaisService;
    private static BasicAuthInterceptor auth;
    private static HostSelectionInterceptor hostSelectionInterceptor;

    private Repository(final AppDatabase database, EgaisService egaisService, BasicAuthInterceptor auth, HostSelectionInterceptor host) {
        mDb = database;
        this.egaisService = egaisService;
        this.auth = auth;
        this.hostSelectionInterceptor = host;
    }

    public static Repository getInstance(final AppDatabase database, BasicAuthInterceptor auth) {
        if (sInstance == null) {
            synchronized (Repository.class) {
                if (sInstance == null) {
                    HostSelectionInterceptor host = new HostSelectionInterceptor();
                    EgaisService egaisService = DataServiceGenerator.createService(EgaisService.class, host, auth);
                    sInstance = new Repository(database, egaisService, auth, host);
                }
            }
        }
        return sInstance;
    }

    public BasicAuthInterceptor getAuth() {
        return auth;
    }

    public static HostSelectionInterceptor getHostSelectionInterceptor() {
        return hostSelectionInterceptor;
    }

    public LiveData<Resource<List<DocumentPOJO>>> loadDocument() {
        return new NetworkBoundResource<List<DocumentPOJO>, List<DocumentPOJO>>() {
            @Override
            protected void saveCallResult(@NonNull List<DocumentPOJO> item) {
                List<Document> documents = new ArrayList<>();
                List<Tovar> tovars = new ArrayList<>();
                List<Body> bodies = new ArrayList<>();

                SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

                for (DocumentPOJO doc : item) {
                    try {
                        documents.add(new Document(doc.getNumber(), dateFormatLocal.parse(doc.getDate()),
                                doc.getTitle(), doc.getSumm(), doc.isSendToEgais(), doc.isSendTo1c(),
                                doc.getNumberInovice(), doc.getBarcode()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    for (DocumenItemPOJO body : doc.getItems()) {
                        tovars.add(new Tovar(body.getCode(), body.getName()));
                        bodies.add(new Body(doc.getNumber(), body.getCode(), body.getCode(), body.getCount()));
                    }
                }

                insertDocumentAll(documents);
                insertTovarAll(tovars);
                insertBodyAll(bodies);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<DocumentPOJO> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<DocumentPOJO>> loadFromDb() {
                return AbsentLiveData.create();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<DocumentPOJO>>> createCall(@Nullable List<DocumentPOJO> data) {
                return egaisService.getPojo();
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<MarkBalance>>> loadMarkBalance() {
        return new NetworkBoundResource<List<MarkBalance>, List<MarkBalance>>() {
            @Override
            protected void saveCallResult(@NonNull List<MarkBalance> item) {

                insertMarkBalanceAll(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<MarkBalance> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<MarkBalance>> loadFromDb() {
                return AbsentLiveData.create();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<MarkBalance>>> createCall(@Nullable List<MarkBalance> data) {
                return egaisService.getBalance();
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<Mark>>> sendMarks(final String number) {
        return new NetworkBoundResource<List<Mark>, ResponseServer>() {
            @Override
            protected void saveCallResult(@NonNull ResponseServer item) {
                updateIsSendTo1c(number, true);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Mark> data) {
                return data != null && !data.isEmpty();
            }

            @NonNull
            @Override
            protected LiveData<List<Mark>> loadFromDb() {
                return mDb.markDao().findMarkByNumberDoc(number);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ResponseServer>> createCall(@Nullable List<Mark> data) {
                return egaisService.setMark(data);
            }
        }.asLiveData();
    }

    public LiveData<Resource<MarkBalance>> saveMark(final String number, final String tovarId, final String stamp) {
        return new NetworkBoundResource<MarkBalance, MarkBalance>() {
            @Override
            protected void saveCallResult(@NonNull MarkBalance item) {
                if (item.getStamp().equals(stamp)) {
                    insertMark(new Mark(number, item.getTovarId(), item.getStamp(), item.getCount()));
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable MarkBalance data) {
                return data == null;
            }

            @NonNull
            @Override
            protected LiveData<MarkBalance> loadFromDb() {
                //return mDb.markBalanceDao().findStamp(stamp);
                return mDb.markDao().findStampByNumberAndTovarId(number, tovarId, stamp);
            }

            @Override
            protected LiveData<ApiResponse<MarkBalance>> createCall(MarkBalance dbSource) {
                return egaisService.getMarkInfo(number, tovarId, stamp);
            }
        }.asLiveData();
    }

    public LiveData<Resource<MarkBalance>> saveMark(final String number, final String stamp) {
        return new NetworkBoundResource<MarkBalance, MarkBalance>() {
            @Override
            protected void saveCallResult(@NonNull MarkBalance item) {
                if (item.getStamp().equals(stamp)) {
                    insertMark(new Mark(number, item.getTovarId() , item.getStamp(), item.getCount()));
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable MarkBalance data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<MarkBalance> loadFromDb() {
                //mDb.markDao().findStampByNumberAndTovarId(number, tovarId, stamp)
                return AbsentLiveData.create();
            }

            @Override
            protected LiveData<ApiResponse<MarkBalance>> createCall(MarkBalance dbSource) {
                return egaisService.getMarkInfo(number, stamp);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Boolean>> deleteMark(final String number, final String stamp){
        return new NetworkBoundResource<Boolean, ResponseBody>(){
            @Override
            protected void saveCallResult(@NonNull ResponseBody item) {
                //if (item)
                removeMarkById(stamp);
            }

            @Override
            protected boolean shouldFetch(@Nullable Boolean data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<Boolean> loadFromDb() {
                return AbsentLiveData.create();
            }

            @Override
            protected LiveData<ApiResponse<ResponseBody>> createCall(Boolean dbSource) {
                return egaisService.deleteMark(number, stamp);
            }
        }.asLiveData();
    }

    public void insertDocument(Document document) {
        new insertAsyncTask(mDb.documentDao()).execute(document);
    }

    private static class insertAsyncTask extends AsyncTask<Document, Void, Void> {
        DocumentDao mAsyncDocumentDao;

        public insertAsyncTask(DocumentDao dao) {
            this.mAsyncDocumentDao = dao;
        }

        @Override
        protected Void doInBackground(Document... voids) {
            //SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            mAsyncDocumentDao.insertDocument(voids[0]);

            return null;
        }
    }

    public void insertDocumentAll(List<Document> documents) {
        new insertAllAsyncTask(mDb.documentDao()).execute(documents);
    }

    private static class insertAllAsyncTask extends AsyncTask<List<Document>, Void, Void> {
        DocumentDao mAsyncDocumentDao;

        public insertAllAsyncTask(DocumentDao dao) {
            this.mAsyncDocumentDao = dao;
        }

        @Override
        protected Void doInBackground(List<Document>... voids) {
            mAsyncDocumentDao.insertDocumentAll(voids[0]);

            return null;
        }
    }

    public void insertTovarAll(List<Tovar> tovars) {
        new insertAllTovarAsyncTask(mDb.tovarDao()).execute(tovars);
    }

    private static class insertAllTovarAsyncTask extends AsyncTask<List<Tovar>, Void, Void> {
        TovarDao mAsyncDao;

        public insertAllTovarAsyncTask(TovarDao dao) {
            this.mAsyncDao = dao;
        }

        @Override
        protected Void doInBackground(List<Tovar>... voids) {
            mAsyncDao.insertTovarAll(voids[0]);

            return null;
        }
    }

    public void insertBodyAll(List<Body> bodies) {
        new insertAllBodyAsyncTask(mDb.bodyDao()).execute(bodies);
    }

    private static class insertAllBodyAsyncTask extends AsyncTask<List<Body>, Void, Void> {
        BodyDao mAsyncDao;

        public insertAllBodyAsyncTask(BodyDao dao) {
            this.mAsyncDao = dao;
        }

        @Override
        protected Void doInBackground(List<Body>... voids) {
            mAsyncDao.insertBodyAll(voids[0]);

            return null;
        }
    }

    public void insertMark(Mark mark) {
        new insertMarkAsyncTask(mDb.markDao()).execute(mark);
    }

    private static class insertMarkAsyncTask extends AsyncTask<Mark, Void, Void> {
        MarkDao mAsyncDao;

        public insertMarkAsyncTask(MarkDao dao) {
            this.mAsyncDao = dao;
        }

        @Override
        protected Void doInBackground(Mark... voids) {
            mAsyncDao.insertMark(voids[0]);

            return null;
        }
    }

    public void insertMarkBalanceAll(List<MarkBalance> balances) {
        new insertAllBalanceAsyncTask(mDb.markBalanceDao()).execute(balances);
    }

    private static class insertAllBalanceAsyncTask extends AsyncTask<List<MarkBalance>, Void, Void> {
        MarkBalanceDao mAsyncDao;

        public insertAllBalanceAsyncTask(MarkBalanceDao dao) {
            this.mAsyncDao = dao;
        }

        @Override
        protected Void doInBackground(List<MarkBalance>... voids) {
            mAsyncDao.insertMarkBalanceAll(voids[0]);

            return null;
        }
    }

    public void removeMarkById(String id) {
        new removeMarkByIdAsyncTask(mDb.markDao()).execute(id);
    }

    private static class removeMarkByIdAsyncTask extends AsyncTask<String, Void, Void> {
        MarkDao mAsyncDao;

        public removeMarkByIdAsyncTask(MarkDao dao) {
            this.mAsyncDao = dao;
        }

        @Override
        protected Void doInBackground(String... Voids) {
            mAsyncDao.deleteMarkById(Voids[0]);

            return null;
        }
    }

    public LiveData<List<Document>> findAllDocuments() {
        return mDb.documentDao().findAllDocuments();
    }

    public LiveData<Document> findDocumentByNumber(String number) {
        return mDb.documentDao().findDocumentByNumber(number);
    }

    public LiveData<MarkBalance> findStamp(String stamp) {
        return mDb.markBalanceDao().findStamp(stamp);
    }

    public LiveData<Document> findDocumentByBarcode(String barcode) {
        return mDb.documentDao().findDocumentByBarcode(barcode);
    }

    public LiveData<List<BodyWithTovar>> findBodyByNumberDoc(String number) {
        return mDb.bodyDao().findBodyByNumberDoc(number);
    }

    public LiveData<List<DocumentWithCount>> findAllDocumentsWithCount(Date date) {
        return mDb.documentDao().findAllDocumentsWithCount(date);
    }

    public LiveData<List<Mark>> findMarkByNumberDoc(String number) {
        return mDb.markDao().findMarkByNumberDoc(number);
    }

    public void updateIsSendTo1c(String number, boolean isSendTo1c) {
        new updateIsSendTo1cAsyncTask(mDb.documentDao(), isSendTo1c).execute(number);
    }

    private static class updateIsSendTo1cAsyncTask extends AsyncTask<String, Void, Void> {
        DocumentDao mAsyncDao;
        boolean isSendTo1c;

        public updateIsSendTo1cAsyncTask(DocumentDao dao, boolean isSendTo1c) {
            this.mAsyncDao = dao;
            this.isSendTo1c = isSendTo1c;
        }

        @Override
        protected Void doInBackground(String... Voids) {
            mAsyncDao.updateIsSendTo1c(Voids[0], isSendTo1c);

            return null;
        }
    }


}
