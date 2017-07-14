package com.kyouryu.dinosaurar_android.marker;

import android.os.Bundle;

import com.kyouryu.dinosaurar_android.R;

import eu.kudan.kudan.ARAPIKey;
import eu.kudan.kudan.ARActivity;

/**
 * Created by ty on 2017/07/14.
 */

public class ARMarkerActivity extends ARActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARAPIKey key = ARAPIKey.getInstance();
        key.setAPIKey(getString(R.string.kudan_api_key));
    }

    @Override
    public void setup() {
        super.setup();
    }
}
