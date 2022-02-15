package com.example.chestnovv.myapplication.service;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class BasicAuthInterceptor implements Interceptor{
    private String credentials;

    public BasicAuthInterceptor(String user, String password) {
        this.credentials = Credentials.basic(user, password);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        Request.Builder builder = originalRequest.newBuilder()
                .header("Authorization", credentials);

        Request newRequest = builder.build();
        return chain.proceed(newRequest);
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }
}
