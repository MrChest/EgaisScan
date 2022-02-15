package com.example.chestnovv.myapplication.service;

import com.example.chestnovv.myapplication.utils.LiveDataCallAdapterFactory;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DataServiceGenerator {

    private static OkHttpClient httpClient = new OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    private static Retrofit.Builder builder
            = new Retrofit.Builder()
            .baseUrl(EgaisService.HTTP_API_EGAIS)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(new LiveDataCallAdapterFactory());

    public static Retrofit retrofit = builder.build();

    public static <S> S createService(Class<S> serviceClass) {

        return retrofit.create(serviceClass);
    }

    public static <S> S createService(Class<S> serviceClass, HostSelectionInterceptor host, BasicAuthInterceptor interceptor) {

//        if (TextUtils.isEmpty(baseUrl)){
//            baseUrl = EgaisService.HTTP_API_EGAIS;
//        }

        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        OkHttpClient basicClient = httpClient.newBuilder()
                .addInterceptor(interceptor)
                .addInterceptor(host)
                .addInterceptor(logInterceptor)
                .build();

        return builder
                .client(basicClient)
                //.baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(serviceClass);
    }
}
