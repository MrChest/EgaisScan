package com.example.chestnovv.myapplication.db;

import android.app.Application;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.chestnovv.myapplication.service.DocumenItemPOJO;
import com.example.chestnovv.myapplication.service.DocumentPOJO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Database(entities = {Document.class, Body.class, Tovar.class, Mark.class, MarkBalance.class}, version = 13, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase{

    private static AppDatabase INSTANCE;
    private static final String DATEBASE_NAME = "Marks";

    private static AssetManager assetManager;

    public abstract DocumentDao documentDao();
    public abstract BodyDao bodyDao();
    public abstract TovarDao tovarDao();
    public abstract MarkDao markDao();
    public abstract MarkBalanceDao markBalanceDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    assetManager = context.getAssets();
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DATEBASE_NAME)
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }

            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private static Callback sRoomDatabaseCallback = new Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            new PopulateDb(INSTANCE).execute();
        }


    };

    private static class PopulateDb extends AsyncTask<Void, Void, Void>{

        private DocumentDao documentDao;
        private BodyDao bodyDao;
        private TovarDao tovarDao;
        private MarkDao markDao;

        public PopulateDb(AppDatabase db) {
            documentDao = db.documentDao();
            bodyDao = db.bodyDao();
            tovarDao = db.tovarDao();
            markDao = db.markDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            int entityCount = documentDao.getTasksCount();
            boolean isDatabaseNotEmpty = entityCount > 0;

            if (isDatabaseNotEmpty) return null;

            markDao.deleteAll();
            documentDao.deleteAll();
            tovarDao.deleteAll();
            bodyDao.deleteAll();

//            String myJson= "";
//            try {
//                myJson = inputStreamToString(assetManager.open("getdocument.json"));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            List<DocumentPOJO> item = new Gson().fromJson(myJson, new TypeToken<List<DocumentPOJO>>(){}.getType());
//
//            List<Document> documents = new ArrayList<>();
//            List<Tovar> tovars = new ArrayList<>();
//            List<Body> bodies = new ArrayList<>();
//
//            SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//
//            for (DocumentPOJO doc : item) {
//                try {
//                    Document document = new Document(doc.getNumber(), dateFormatLocal.parse(doc.getDate()),
//                            doc.getTitle(), doc.getSumm(), doc.isSendToEgais(), doc.isSendTo1c(),
//                            doc.getNumberInovice(), doc.getBarcode());
//                    documents.add(document);
//
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                for (DocumenItemPOJO body : doc.getItems()) {
//                    tovars.add(new Tovar(body.getCode(), body.getName()));
//                    bodies.add(new Body(doc.getNumber(), body.getCode(), body.getCode(), body.getCount()));
//                }
//            }
//
//            documentDao.insertDocumentAll(documents);
//            tovarDao.insertTovarAll(tovars);
//            bodyDao.insertBodyAll(bodies);

            return null;
        }
    }

    private static String inputStreamToString(InputStream inputStream) {
        try {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, bytes.length);
            String json = new String(bytes);
            return json;
        } catch (IOException e) {
            return null;
        }
    }
}
