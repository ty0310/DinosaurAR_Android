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

    public static Bitmap loadBitmapFromView(View v) {
        Bitmap bitmap;
        v.setDrawingCacheEnabled(true);
        bitmap = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        return bitmap;
    }

    //重ね順はcurrentBitmapが下、blendBitmapが上です
    public static Bitmap blendBitmap(Bitmap currentBitmap, Bitmap blendBitmap) {
        int width = currentBitmap.getWidth();
        int height = currentBitmap.getHeight();
        Bitmap new_bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(new_bitmap);
        canvas.drawBitmap(currentBitmap, 0, 0, (Paint)null); // image, x座標, y座標, Paintイタンス
        int disWidth = (width - blendBitmap.getWidth()) / 2;
        int disHeight = (height - blendBitmap.getHeight()) / 2;
        canvas.drawBitmap(blendBitmap, disWidth, disHeight, (Paint)null); // 画像合成

        return new_bitmap;
    }

    public static Bitmap resizeBitmap(Bitmap currentBitmap, int width, int height, int angle) {
        int currentWidth = currentBitmap.getWidth();
        int currentHeight = currentBitmap.getHeight();
        int newWidth = width;
        int newHeight = height;

        // calculate the scale - in this case = 0.4f
        float scaleWidth = ((float) newWidth) / currentWidth;
        float scaleHeight = ((float) newHeight) / currentHeight;

        // createa matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postRotate(angle);
        matrix.postScale(scaleWidth, scaleHeight);

        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(currentBitmap, 0, 0,
                width, height, matrix, true);

        return resizedBitmap;
    }
}
