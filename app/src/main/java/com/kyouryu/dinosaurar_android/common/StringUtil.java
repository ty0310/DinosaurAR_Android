package com.kyouryu.dinosaurar_android.common;

import android.content.Context;

/**
 * Created by ty on 2017/07/13.
 */

public class StringUtil {

    public static int getImageIdFrom(String imageName, Context context) {
        return context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
    }
}
