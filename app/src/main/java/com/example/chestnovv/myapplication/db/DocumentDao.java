package com.example.chestnovv.myapplication.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;

import java.util.Date;
import java.util.List;

@Dao
@TypeConverters(DateConverter.class)
public interface DocumentDao {
    @Query("SELECT * From Document")
    LiveData<List<Document>> findAllDocuments();

    @Query("SELECT * From Document " +
            "WHERE barcode = :barcode")
    LiveData<Document> findDocumentByBarcode(String barcode);

    @Query("SELECT * From Document " +
            "WHERE number = :number")
    LiveData<Document> findDocumentByNumber(String number);

    @Query("SELECT date, number, numberInovice, title, summ, isSendTo1c, isSendToEgais, TBody.count as count , TMark.countStamp as countStamp From Document " +
            "LEFT JOIN (SELECT Body.document_number, sum(Body.count) as count From Body GROUP BY Body.document_number) as TBody ON Document.number = TBody.document_number " +
            "LEFT JOIN (SELECT Mark.document_number, sum(Mark.count) as countStamp From Mark GROUP BY Mark.document_number) as TMark ON Document.number = TMark.document_number " +
            "WHERE date >= :date AND isSendToEgais = 0 " +
            "GROUP BY Document.number")
    LiveData<List<DocumentWithCount>> findAllDocumentsWithCount(Date date);

    @Query("SELECT COUNT(number) FROM document")
    int getTasksCount();

    @Query("UPDATE Document SET isSendTo1c = :flag WHERE number = :number")
    void updateIsSendTo1c(String number, boolean flag);

    @Query("UPDATE Document SET isSendToEgais = :flag WHERE number = :number")
    void updateIsSendToEgais(String number, boolean flag);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDocument(Document document);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDocumentAll(List<Document> document);

    @Query("DELETE FROM Document")
    void deleteAll();
}
