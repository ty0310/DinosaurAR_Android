package com.kyouryu.dinosaurar_android;

import android.app.Activity;
import android.content.Context;

import com.kyouryu.dinosaurar_android.model.ItemData;
import com.kyouryu.dinosaurar_android.model.ListViewItemModel;
import com.kyouryu.dinosaurar_android.model.UserItemData;

/**
 * Created by ty on 2017/07/13.
 */

public class Session {
    private static final Session ourInstance = new Session();

    private Activity activity;

    private Context context;

    private ListViewItemModel selectedItemData;

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

    public ListViewItemModel getSelectedItemData() {
        return selectedItemData;
    }

    public void setSelectedItemData(ListViewItemModel selectedItemData) {
        this.selectedItemData = selectedItemData;
    }
}
