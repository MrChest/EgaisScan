package com.example.chestnovv.myapplication.service;

public class ResponseServer
{
    private ResponseResult result;

    private String data;

    public ResponseResult getResult ()
    {
        return result;
    }

    public ResponseServer(ResponseResult result) {
        this.result = result;
    }

    public void setResult (ResponseResult result)
    {
        this.result = result;
    }

    public String getData ()
{
    return data;
}

    public void setData (String data)
    {
        this.data = data;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [result = "+result+", data = "+data+"]";
    }
}
