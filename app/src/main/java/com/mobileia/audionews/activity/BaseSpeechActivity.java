package com.mobileia.audionews.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import com.mobileia.audionews.library.MCSpeech;

/**
 * Created by matiascamiletti on 26/9/15.
 */
public class BaseSpeechActivity extends AppCompatActivity {

    protected MCSpeech mSpeech;

    protected boolean isSpeeching = false;

    protected boolean isPause = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSpeech = new MCSpeech();
        mSpeech.init(this);
    }

    @Override
    protected void onPause() {
        mSpeech.stop();
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(mSpeech != null){
            mSpeech.onActivityResult(requestCode, resultCode, data);
        }
    }
}
