package com.kyouryu.dinosaurar_android.common;

import com.google.android.gms.common.server.converter.StringToIntConverter;
import com.google.android.gms.vision.Frame;

/**
 * Created by ty on 2017/07/17.
 */

public class ItemComponent {

    public enum Frame {
        // DEFAULT
        FRAME_0,
        FRAME_1,
        FRAME_2,
        FRAME_3,
        FRAME_4,
        FRAME_5,
        FRAME_6,
        FRAME_7,
        FRAME_8,

        // EXTENSION
        FRAME_9,
        FRAME_10,
        FRAME_11,
        FRAME_12,
        FRAME_13,
        FRAME_14,
        FRAME_15,
        FRAME_16,
        FRAME_17,
        FRAME_18,
        FRAME_19,
        FRAME_20,
        FRAME_21,
        FRAME_22;

        public String id = "" + this.ordinal();

        public String filterImageName = "frame_" + this.ordinal();

        public String markerImageName = this.ordinal() > 8 ? "marker_" + this.ordinal() : "";

        public String coverImageName = this.ordinal() > 8 ? "cover_" + this.ordinal() : "";

        public String iconImageName = "icon_" + this.ordinal();

        public boolean isDefaultOpen = this.ordinal() > 8 ? false : true;

        public boolean isMarker = this.ordinal() > 8 ? true : false;

        public boolean isPositionUp = this.ordinal() == 3 || this.ordinal() == 4 || this.ordinal() == 9 || this.ordinal() == 10 ? true : false;

    }

    public static Frame[] allValues = Frame.values();

}
