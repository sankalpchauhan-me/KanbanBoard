package me.sankalpchauhan.kanbanboard;

import android.app.Application;
import android.content.Context;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class KanbanApp extends Application {
    private static Context context;
    private static FirebaseCrashlytics crashlytics;
    public void onCreate() {
        super.onCreate();
        KanbanApp.context = getApplicationContext();
        KanbanApp.crashlytics=FirebaseCrashlytics.getInstance();
    }

    public static Context getAppContext() {
        return KanbanApp.context;
    }

    public static FirebaseCrashlytics getCrashlytics(){
        return KanbanApp.crashlytics;
    }
}
