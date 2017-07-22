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
        if (getOpenedItems().size() == 0) {
            return saveDefaultinitialData();
        }
        return getOpenedItems();

    }

    private RealmResults<ItemData> getOpenedItems() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<ItemData> result = realm.where(ItemData.class).equalTo("isOpen", true).findAll();
        return result;
    }

    private RealmResults<ItemData> saveDefaultinitialData() {
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
            itemData.setPositionUp(item.isPositionUp);
        }
        realm.commitTransaction();
        return getOpenedItems();
    }

    public static RealmResults<ItemData> getARMarkers() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<ItemData> result = realm.where(ItemData.class).equalTo("isMarker", true).findAll();
        return result;
    }

    public static void openFilter(String id) {
        Realm realm = Realm.getDefaultInstance();
        ItemData result = realm.where(ItemData.class).equalTo("id", id).findFirst();
        realm.beginTransaction();
        result.setOpen(true);
        realm.commitTransaction();
    }

    public static boolean isOpenItem(String id) {
        Realm realm = Realm.getDefaultInstance();
        ItemData result = realm.where(ItemData.class).equalTo("id", id).findFirst();
        if (result.isOpen()) {
            return true;
        }
        return false;
    }

    public static boolean isPositionUp(String id) {
        Realm realm = Realm.getDefaultInstance();
        ItemData result = realm.where(ItemData.class).equalTo("id", id).findFirst();
        if (result.isPositionUp()) {
            return true;
        }
        return false;
    }

    public RealmResults<ItemData> getItemDatas() {
        return itemDatas;
    }
}
