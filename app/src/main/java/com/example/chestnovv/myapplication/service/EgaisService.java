package com.example.chestnovv.myapplication.service;

import android.arch.lifecycle.LiveData;

import com.example.chestnovv.myapplication.db.Mark;
import com.example.chestnovv.myapplication.db.MarkBalance;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EgaisService {
    String HTTP_API_EGAIS = "http://192.168.210.138/egais3/hs/";

    @GET("egais-service/getdocument")
    LiveData<ApiResponse<List<DocumentPOJO>>> getPojo();

    @POST("egais-service/setmark")
    LiveData<ApiResponse<ResponseServer>> setMark(@Body List<Mark> body);

    @GET("egais-service/getbalance")
    LiveData<ApiResponse<List<MarkBalance>>> getBalance();

    @GET("egais-service/getMarkInfo")
    LiveData<ApiResponse<MarkBalance>>  getMarkInfo(@Query("number") String number, @Query("tovarId") String tovarId, @Query("mark") String mark);

    @GET("egais-service/getMarkInfo")
    LiveData<ApiResponse<MarkBalance>>  getMarkInfo(@Query("number") String number,  @Query("mark") String mark);

    @GET("egais-service/deleteMark")
    LiveData<ApiResponse<ResponseBody>> deleteMark(@Query("number") String number, @Query("mark") String mark);
}
