package com.signatures;

import android.app.Application;
import android.app.DownloadManager;
import android.preference.PreferenceManager;

public class App extends Application {

    private static AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerAppComponent.create();

        component.getRestService().setSharedPreferences(
                PreferenceManager.getDefaultSharedPreferences(this)
        );

        component.getRestService().setDownloadManager(
                (DownloadManager) getSystemService(DOWNLOAD_SERVICE)
        );
    }

    public static AppComponent getComponent() {
        return component;
    }

}
