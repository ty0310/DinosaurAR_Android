package com.kyouryu.dinosaurar_android.model;

import io.realm.RealmObject;

/**
 * Created by ty on 2017/07/13.
 */

public class ItemData extends RealmObject {
    // ID
    private String id;

    // フィルター画像名
    private String filterImageName;

    // マーカー画像名
    private String markerImageName;

    // アイコン画像名
    private String iconImageName;

    // カバー画像名
    private String coverImageName;

    // 開放フラグ
    private boolean isOpen;

    // AR対象フラグ
    private boolean isMarker;

    private boolean isPositionUp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilterImageName() {
        return filterImageName;
    }

    public void setFilterImageName(String filterImageName) {
        this.filterImageName = filterImageName;
    }

    public String getMarkerImageName() {
        return markerImageName;
    }

    public void setMarkerImageName(String markerImageName) {
        this.markerImageName = markerImageName;
    }

    public String getIconImageName() {
        return iconImageName;
    }

    public void setIconImageName(String iconImageName) {
        this.iconImageName = iconImageName;
    }

    public String getCoverImageName() {
        return coverImageName;
    }

    public void setCoverImageName(String coverImageName) {
        this.coverImageName = coverImageName;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public boolean isMarker() {
        return isMarker;
    }

    public void setMarker(boolean marker) {
        isMarker = marker;
    }

    public boolean isPositionUp() {
        return isPositionUp;
    }

    public void setPositionUp(boolean positionUp) {
        isPositionUp = positionUp;
    }
}
