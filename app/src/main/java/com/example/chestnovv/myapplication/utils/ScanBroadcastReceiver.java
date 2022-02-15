package com.example.chestnovv.myapplication.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScanBroadcastReceiver extends BroadcastReceiver {

    public interface ScanBroadcastListener{
        void receiverBroadcastScan(String barcode);
    }

    public static final String MESSAGEUROVO = "urovo.rcv.message";
    public static final String MESSAGESCAN = "scan.rcv.message";
    private static final String BARCODE_EXTRA = "barocode";
    private static final String LENGTH_EXTRA = "length";

    private final ScanBroadcastListener mListener;

    public ScanBroadcastReceiver(ScanBroadcastListener listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (MESSAGEUROVO.equals(intent.getAction()) || MESSAGESCAN.equals(intent.getAction())) {
            byte barcode[] = intent.getByteArrayExtra(BARCODE_EXTRA);
            int length = intent.getIntExtra(LENGTH_EXTRA, 0);
            if (length > 0){
                String sBarcode = new String(barcode, 0, length);
                mListener.receiverBroadcastScan(sBarcode);

                //Log.d(TAG, "onReceive: " + sBarcode);
            }
        }
    }
}
