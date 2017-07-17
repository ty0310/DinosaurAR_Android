package com.kyouryu.dinosaurar_android.model;

import android.util.Log;

import com.kyouryu.dinosaurar_android.common.ItemComponent;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by ty on 2017/07/17.
 */

public class UserItemData {

    private RealmResults<ItemData> itemDatas = getDefaultItemData();

    public UserItemData() {

    }

    private RealmResults<ItemData> getDefaultItemData() {
        Log.d("aaaaaaa", "aaaaaaaa getDefaultItemData");
        if (getOpenedItems().size() == 0) {
            Log.d("aaaaaaa", "aaaaaaaa getOpenedItems().length == 0");
            return saveDefaultinitialData();
        }
        Log.d("aaaaaaa", "aaaaaaaa getOpenedItems().length != 0");
        return getOpenedItems();

    }

    private RealmResults<ItemData> getOpenedItems() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<ItemData> result = realm.where(ItemData.class).equalTo("isOpen", true).findAll();
        Log.d("aaaaaa", "aaaaaaa result size:" + result.size());
        return result;
    }

    private RealmResults<ItemData> saveDefaultinitialData() {
        Log.d("aaaaaa", "aaaaaaa saveDefaultinitialData()");
        Realm realm = Realm.getDefaultInstance();
        for (ItemComponent.Frame item : ItemComponent.allValues) {
            Log.d("aaaaaaa", "aaaaaaaa" + item.id);
            Log.d("aaaaaaa", "aaaaaaaa" + item.filterImageName);
            Log.d("aaaaaaa", "aaaaaaaa" + item.markerImageName);
            Log.d("aaaaaaa", "aaaaaaaa" + item.coverImageName);
            Log.d("aaaaaaa", "aaaaaaaa" + item.iconImageName);
            Log.d("aaaaaaa", "aaaaaaaa" + item.isDefaultOpen);
            Log.d("aaaaaaa", "aaaaaaaa" + item.isMarker);
        }
        return null;
    }

}
