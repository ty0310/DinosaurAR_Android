package com.kyouryu.dinosaurar_android.model;

/**
 * Created by ty on 2017/07/13.
 */

public class ListViewItemModel {

    private int imageId;
    private boolean isSelected;

    public ListViewItemModel(int imageId, boolean isSelected) {
        this.imageId = imageId;
        this.isSelected = isSelected;
    }

    public int getImageId() {
        return imageId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
