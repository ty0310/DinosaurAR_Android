package com.kyouryu.dinosaurar_android.marker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.kyouryu.dinosaurar_android.R;
import com.kyouryu.dinosaurar_android.Session;
import com.kyouryu.dinosaurar_android.model.ItemData;
import com.kyouryu.dinosaurar_android.model.UserItemData;

import eu.kudan.kudan.ARAPIKey;
import eu.kudan.kudan.ARActivity;
import eu.kudan.kudan.ARImageNode;
import eu.kudan.kudan.ARImageTrackable;
import eu.kudan.kudan.ARImageTrackableListener;
import eu.kudan.kudan.ARImageTracker;

/**
 * Created by ty on 2017/07/14.
 */

public class ARMarkerActivity extends ARActivity {

    private boolean canTapScreen = false;

    private String trackableId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARAPIKey key = ARAPIKey.getInstance();
        key.setAPIKey(getString(R.string.kudan_api_key));

        Session.getInstance().setActivity(this);
        Session.getInstance().setContext(this);

        setContentView(R.layout.ar_markar);

        ImageView cameraFrame = (ImageView)findViewById(R.id.camera_frame);
        cameraFrame.setOnClickListener(new OnCameraFrameClickListener());
    }

    @Override
    public void setup() {
        super.setup();

        ARImageTracker imageTracker = ARImageTracker.getInstance();
        imageTracker.initialise();

        for (int i = 0; i < UserItemData.getARMarkers().size(); i++) {
            ItemData itemData = UserItemData.getARMarkers().get(i);

            ARImageTrackable trackable = new ARImageTrackable("" + itemData.getId());
            trackable.loadFromAsset(itemData.getMarkerImageName() + ".png");

            trackable.addListener(new OnImageDetectListener());

            imageTracker.addTrackable(trackable);

            ARImageNode imageNode = new ARImageNode(itemData.getCoverImageName() + ".png");
            trackable.getWorld().addChild(imageNode);
        }
    }

    private class OnImageDetectListener implements ARImageTrackableListener {
        @Override
        public void didDetect(ARImageTrackable arImageTrackable) {
            canTapScreen = true;
            trackableId = arImageTrackable.getName();
        }

        @Override
        public void didTrack(ARImageTrackable arImageTrackable) {
        }

        @Override
        public void didLose(ARImageTrackable arImageTrackable) {
            canTapScreen = false;
        }
    }

    private class OnCameraFrameClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (!canTapScreen) {
                return;
            }

            new AlertDialog.Builder(Session.getInstance().getActivity())
                    .setTitle(getString(R.string.ar_dialog_title))
                    .setMessage(getString(R.string.ar_dialog_message, trackableId))
                    .setPositiveButton(getString(R.string.ar_dialog_ok), okListener)
                    .setNegativeButton(getString(R.string.ar_dialog_cancel), null)
                    .show();


        }
    }

    private DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
            if (UserItemData.isOpenItem(trackableId)){
                // 解放済みの場合
                Log.d("aaaaaaaaa", "aaaaaaaaa 解放済み");
            } else {
                Log.d("aaaaaaaaa", "aaaaaaaaa み解放");
                UserItemData.openFilter(trackableId);
            }
        }
    };
}
