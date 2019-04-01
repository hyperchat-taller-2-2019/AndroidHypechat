package com.example.hypechat;

import android.app.Application;


import com.facebook.appevents.AppEventsLogger;

public class HypechatApp extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        AppEventsLogger.activateApp(this);
    }

}
