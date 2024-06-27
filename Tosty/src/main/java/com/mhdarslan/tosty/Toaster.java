package com.mhdarslan.tosty;

import android.content.Context;
import android.widget.Toast;

public class Toaster {

    public static Toast showLongToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.show();
        return toast;
    }
    public static Toast showShortToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
        return toast;
    }
}
