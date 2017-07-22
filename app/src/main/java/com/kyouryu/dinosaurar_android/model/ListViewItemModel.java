package com.kyouryu.dinosaurar_android.model;

/**
 * Created by ty on 2017/07/13.
 */

public class ListViewItemModel {

    // リスト画像名
    private String iconImageName;
    // 表示画像名
    private String frameImageName;
    private boolean isSelected;
    private boolean isPositionUp;

    public ListViewItemModel(String iconImageName, String frameImageName, boolean isSelected) {
        this.iconImageName = iconImageName;
        this.frameImageName = frameImageName;
        this.isSelected = isSelected;
    }

    public String getIconImageName() {
        return iconImageName;
    }

    public String getFrameImageName() {
        return frameImageName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isPositionUp() {
        return isPositionUp;
    }

    public void setPositionUp(boolean positionUp) {
        isPositionUp = positionUp;
    }
}
