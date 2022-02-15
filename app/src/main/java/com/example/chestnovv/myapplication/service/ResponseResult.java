package com.example.chestnovv.myapplication.service;

public class ResponseResult
{
    private String text;

    private boolean error;

    public ResponseResult(boolean error, String text) {
        this.text = text;
        this.error = error;
    }

    public String getText ()
    {
        return text;
    }

    public void setText (String text)
    {
        this.text = text;
    }

    public boolean getError ()
    {
        return error;
    }

    public void setError (boolean error)
    {
        this.error = error;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [text = "+text+", error = "+error+"]";
    }
}
