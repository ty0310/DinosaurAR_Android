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
        realm.beginTransaction();
        for (ItemComponent.Frame item : ItemComponent.allValues) {
            ItemData itemData = realm.createObject(ItemData.class);
            itemData.setId(item.id);
            itemData.setFilterImageName(item.filterImageName);
            itemData.setMarkerImageName(item.markerImageName);
            itemData.setCoverImageName(item.coverImageName);
            itemData.setIconImageName(item.iconImageName);
            itemData.setOpen(item.isDefaultOpen);
            itemData.setMarker(item.isMarker);
        }
        realm.commitTransaction();
        return getOpenedItems();
    }

    public RealmResults<ItemData> getItemDatas() {
        return itemDatas;
    }
}
