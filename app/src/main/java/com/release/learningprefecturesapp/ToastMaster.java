package com.release.learningprefecturesapp;

import android.content.Context;
import android.widget.Toast;

public class ToastMaster extends Toast {

    private static Toast sToast = null;

    public ToastMaster(Context context){
        super(context);
    }

    @Override
    public void show() {
        super.show();
    }

    public static void setToast(Toast toast) {
        if (sToast != null){
            sToast.cancel();
            sToast = toast;
            sToast.show();
        }
        else{
            sToast = toast;
            sToast.show();
        }
    }
    public static void cancelToast() {
        if (sToast != null){
            sToast.cancel();
            sToast = null;
        }
    }
}
