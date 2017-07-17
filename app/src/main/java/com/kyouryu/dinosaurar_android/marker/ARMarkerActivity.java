package com.kyouryu.dinosaurar_android.marker;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.kyouryu.dinosaurar_android.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARAPIKey key = ARAPIKey.getInstance();
        key.setAPIKey(getString(R.string.kudan_api_key));

        setContentView(R.layout.ar_markar);


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
            Log.d("aaaaa", "aaaaaaa" + arImageTrackable.getName());
            Log.d("aaaaa", "aaaaa" + arImageTrackable.getWorld().getPosition());

            canTapScreen = true;
        }

        @Override
        public void didTrack(ARImageTrackable arImageTrackable) {
        }

        @Override
        public void didLose(ARImageTrackable arImageTrackable) {
            canTapScreen = false;
        }
    }
}
