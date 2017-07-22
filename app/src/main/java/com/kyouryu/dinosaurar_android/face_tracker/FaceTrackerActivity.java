/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kyouryu.dinosaurar_android.face_tracker;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.kyouryu.dinosaurar_android.R;
import com.kyouryu.dinosaurar_android.Session;
import com.kyouryu.dinosaurar_android.advertise.AdvertiseActivity;
import com.kyouryu.dinosaurar_android.common.ImageUtil;
import com.kyouryu.dinosaurar_android.common.StringUtil;
import com.kyouryu.dinosaurar_android.model.ItemData;
import com.kyouryu.dinosaurar_android.model.ListViewItemModel;
import com.kyouryu.dinosaurar_android.model.UserItemData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Activity for the face tracker app.  This app detects faces with the rear facing camera, and draws
 * overlay graphics to indicate the position, size, and ID of each face.
 */
public final class FaceTrackerActivity extends AppCompatActivity {
    private static final String TAG = "FaceTracker";

    private UserItemData userItemData;

    private CameraSource mCameraSource = null;

    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerViewAdapter;

    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private static final int REQUEST_WRITE_PERMISSION = 3;

    //==============================================================================================
    // Activity Methods
    //==============================================================================================

    /**
     * Initializes the UI and initiates the creation of a face detector.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Session.getInstance().setActivity(this);
        Session.getInstance().setContext(this);

        setContentView(R.layout.main);

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);

        // カメラ切り替えボタン設定
        Button reloadButton = (Button)findViewById(R.id.camera_option).findViewById(R.id.reload_button);
        reloadButton.setOnClickListener(new reloadButtonClickListener());

        Button shutterButton = (Button)findViewById(R.id.camera_option).findViewById(R.id.shutter_button);
        shutterButton.setOnClickListener(new OnShutterButtonClickListener());

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(CameraSource.CAMERA_FACING_BACK);
        } else {
            requestCameraPermission();
        }
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     */
    private void createCameraSource(int cameraType) {

        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        if (!detector.isOperational()) {
            // Note: The first time that an app using face API is installed on a device, GMS will
            // download a native library to the device in order to do detection.  Usually this
            // completes before the app is run for the first time.  But if that download has not yet
            // completed, then the above call will not detect any faces.
            //
            // isOperational() can be used to check if the required native library is currently
            // available.  The detector will automatically become operational once the library
            // download completes on device.
            Log.w(TAG, "Face detector dependencies are not yet available.");
        }

        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(cameraType)
                .setRequestedFps(30.0f)
                .build();

    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();

        startCameraSource();

        userItemData = new UserItemData();

        // データの更新
        // 画像選択List設定
        mRecyclerView = (RecyclerView)findViewById(R.id.camera_option).findViewById(R.id.recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(manager);

        ArrayList<ListViewItemModel> data = new ArrayList<>();
        data.add(new ListViewItemModel("cell_plus", null, false));
        for (int i = 0; i < userItemData.getItemDatas().size(); i++) {
            ItemData itemData = userItemData.getItemDatas().get(i);
            boolean isSelected = false;
            if (i == 0) {
                isSelected = true;
            }
            data.add(new ListViewItemModel(itemData.getIconImageName(), itemData.getFilterImageName(), isSelected));
        }

        mRecyclerViewAdapter = new RecyclerViewAdapter(this, data, new RecyclerViewAdapterListener());
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    /**
     * Releases the resources associated with the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            createCameraSource(CameraSource.CAMERA_FACING_BACK);
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("カメラへのアクセス")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    //==============================================================================================
    // Camera Source Preview
    //==============================================================================================

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    //==============================================================================================
    // Graphic Face Tracker
    //==============================================================================================

    /**
     * Factory for creating a face tracker to be associated with a new face.  The multiprocessor
     * uses this factory to create face trackers as needed -- one for each individual.
     */
    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

    /**
     * Face tracker for each detected individual. This maintains a face graphic within the app's
     * associated face overlay.
     */
    private class GraphicFaceTracker extends Tracker<Face> {
        private GraphicOverlay mOverlay;
        private FaceGraphic mFaceGraphic;

        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay);
        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face);
        }

        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
        }
    }


    //==============================================================================================
    // button event
    //==============================================================================================

    private class reloadButtonClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            int cameraType;
            if (mCameraSource.getCameraFacing() == CameraSource.CAMERA_FACING_BACK) {
                cameraType = CameraSource.CAMERA_FACING_FRONT;
            } else {
                cameraType = CameraSource.CAMERA_FACING_BACK;
            }
            mCameraSource.stop();
            createCameraSource(cameraType);
            startCameraSource();
        }
    }

    // 恐竜アイコンタップ時のイベント
    private class RecyclerViewAdapterListener implements RecyclerViewAdapter.Listener {
        @Override
        public void onRecyclerClicked(View v, int position) {
            // アイテムタップで再描画
            mRecyclerViewAdapter.notifyDataSetChanged();

            if (position == 0) {
                // 広告画面へ遷移
                Intent intent = new Intent(FaceTrackerActivity.this, AdvertiseActivity.class);
                startActivity(intent);

            } else {

            }
        }
    }

    // シャッターボタン押下イベント
    private class OnShutterButtonClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {

            if (ContextCompat.checkSelfPermission(Session.getInstance().getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                // 以前に許諾して、今後表示しないとしていた場合は、ここにはこない
                String[] permissions = new String[] {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                };
                ActivityCompat.requestPermissions(Session.getInstance().getActivity(), permissions, REQUEST_WRITE_PERMISSION);
            } else {
                //  許諾されているので、やりたいことをする
                mCameraSource.takePicture(new OnShutterCallback(), new OnPictureCallback());
            }
        }
    }

    private class OnShutterCallback implements CameraSource.ShutterCallback {
        @Override
        public void onShutter() {

        }
    }

    private class OnPictureCallback implements CameraSource.PictureCallback {
        @Override
        public void onPictureTaken(byte[] bytes) {
            // カメラbitmap
            Bitmap picture = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            // 内側カメラ、外側カメラによってrotateがずれるのを修正
            int rotateAngle;
            if (mCameraSource.getCameraFacing() == CameraSource.CAMERA_FACING_BACK) {
                rotateAngle = 90;
            } else {
                rotateAngle = -90;
            }
            Matrix matrix = new Matrix();
            matrix.postRotate(rotateAngle);

            Bitmap rotatedPicture = Bitmap.createBitmap(picture , 0, 0, picture.getWidth(), picture.getHeight(), matrix, true);

            // 顔認識
            FaceDetector detector = new FaceDetector.Builder(Session.getInstance().getContext())
                    .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                    .build();
            Frame frame = new Frame.Builder().setBitmap(rotatedPicture).build();

            SparseArray<Face> faces = detector.detect(frame);
            Bitmap result = Bitmap.createBitmap(rotatedPicture.getWidth(), rotatedPicture.getHeight(), rotatedPicture.getConfig());
            Canvas canvas = new Canvas(result);
            canvas.drawBitmap(rotatedPicture, null, new RectF(0, 0, rotatedPicture.getWidth(), rotatedPicture.getHeight()), null);
            for (int i = 0; i < faces.size(); i++) {
                Face face = faces.valueAt(i);
                float x = face.getPosition().x + face.getWidth() / 2;
                // 指定された画像のみポジション設定
                float centerYRatio = 2.0f;
                if (Session.getInstance().getSelectedItemData().isPositionUp()) {
                    centerYRatio = 2.7f;
                }
                float y = face.getPosition().y + face.getHeight() / centerYRatio;

                float xOffset = face.getWidth() / 1.3f;
                float yOffset = face.getHeight() / 1.3f;
                float left = x - xOffset;
                float top = y - yOffset;
                float right = x + xOffset;
                float bottom = y + yOffset;

                RectF rect = new RectF(left, top, right,bottom);

//                Bitmap bmp = BitmapFactory.decodeResource(Session.getInstance().getContext().getResources(), R.drawable.frame_5);
                Bitmap bmp = ImageUtil.getBitmapFromAssets(Session.getInstance().getSelectedItemData().getFrameImageName(),
                        Session.getInstance().getContext());
                canvas.drawBitmap(bmp, null, rect, null);
            }

            try {
                saveBitmap(result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    public void saveBitmap(Bitmap saveImage) throws IOException {

        final String SAVE_DIR = "/DCIM/Camera/";
        File file = new File(Environment.getExternalStorageDirectory().getPath() + SAVE_DIR);
        try{
            if(!file.exists()){
                file.mkdir();
            }
        }catch(SecurityException e){
            e.printStackTrace();
        }

        Date mDate = new Date();
        SimpleDateFormat fileNameDate = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String fileName = fileNameDate.format(mDate) + ".jpg";
        String AttachName = file.getAbsolutePath() + "/" + fileName;

        try {
            FileOutputStream out = new FileOutputStream(AttachName);
            saveImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch(IOException e) {
            e.printStackTrace();
        }

        // save index
        ContentValues values = new ContentValues();
        ContentResolver contentResolver = getContentResolver();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put("_data", AttachName);
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }
}
