package com.kyouryu.dinosaurar_android.common;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ty on 2017/07/14.
 */

public class ImageUtil {

    public static Bitmap getBitmapFromAssets(String fileName, Context context) {
        try {
            InputStream istream = context.getResources().getAssets().open(fileName + ".png");
            Bitmap bitmap = BitmapFactory.decodeStream(istream);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
