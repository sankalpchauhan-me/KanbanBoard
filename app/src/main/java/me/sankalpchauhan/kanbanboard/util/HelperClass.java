package me.sankalpchauhan.kanbanboard.util;

import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

import static me.sankalpchauhan.kanbanboard.util.Constants.TAG;

public class HelperClass {
    public static void logErrorMessage(String errorMessage) {
        Log.e(TAG, errorMessage);
    }

    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();
        Log.i("NetworkState", "isConnected checking network state.......");
        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting()))
                return true;
            else return false;
        } else
            return false;
    }

    public static androidx.appcompat.app.AlertDialog.Builder buildNetworkDialog(Context c) {
        Log.i("NetworkState", "Network Failed......... Building Dialog");
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile Data or WIFI to access this app. Press Enable to access network settings");
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent in = new Intent(android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS);
                c.startActivity(in);
            }
        });

        return builder;
    }

    //Notification Builder
    public void addNotification(Context mContext, String title, String subject, int Imageid, int ID) {
        Notification noti = new Notification.Builder(mContext)
                .setContentTitle(title)
                .setContentText(subject)
                .setSmallIcon(Imageid)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setCategory(Notification.CATEGORY_REMINDER)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(ID, noti);
    }

    public static ProgressDialog mProgressDialog;

    public static void showProgressDialog(Context context, String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage(message);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }


    public static void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}