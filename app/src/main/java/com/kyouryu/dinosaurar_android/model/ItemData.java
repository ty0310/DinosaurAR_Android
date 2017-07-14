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

}
