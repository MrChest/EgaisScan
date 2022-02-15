package com.example.chestnovv.myapplication.utils;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class DialogMessage extends DialogFragment {

    public static DialogMessage newInstance(String title) {
        DialogMessage frag = new DialogMessage();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String title = getArguments().getString("title");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Сообщение")
                .setMessage(title)
                .setNeutralButton("OK", null);

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
