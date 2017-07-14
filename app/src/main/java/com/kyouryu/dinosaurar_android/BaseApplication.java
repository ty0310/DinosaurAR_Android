package com.kyouryu.dinosaurar_android;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by ty on 2017/07/13.
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Realm設定
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(config);
    }
}
