package com.kyouryu.dinosaurar_android;

import android.app.Activity;
import android.content.Context;

/**
 * Created by ty on 2017/07/13.
 */

public class Session {
    private static final Session ourInstance = new Session();

    private Activity activity;

    private Context context;

    public static Session getInstance() {
        return ourInstance;
    }

    private Session() {
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
