package com.mobileia.audionews.library;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by matiascamiletti on 24/9/15.
 */
public class MCSpeech implements TextToSpeech.OnInitListener {

    private int SPEECH_CHECK_CODE = 1989;

    private TextToSpeech mTTS;

    private Activity mContext;

    protected HashMap<String, String> mProperties = new HashMap();

    protected MCUtteranceListener mUtteranceListener;

    public void init(Activity context){
        mContext = context;
        checkTTS();
        mUtteranceListener = new MCUtteranceListener();
    }

    private void checkTTS(){
        Intent check = new Intent();
        check.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        mContext.startActivityForResult(check, SPEECH_CHECK_CODE);
    }

    private void configureParameters(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1){
            mTTS.setOnUtteranceProgressListener(mUtteranceListener);
        }else{
            mTTS.setOnUtteranceCompletedListener(mUtteranceListener);
        }

        mProperties.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_ALARM));
        mProperties.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "END_ID");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode != SPEECH_CHECK_CODE){
            return;
        }

        if(resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS){
            mTTS = new TextToSpeech(mContext, this);
            configureParameters();
        }else{
            Intent install = new Intent();
            install.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
            mContext.startActivity(install);
        }
    }

    @Override
    public void onInit(int status) {
        if(status != TextToSpeech.SUCCESS){
            Toast.makeText(mContext, "Sorry! Text to Speech failed...", Toast.LENGTH_SHORT).show();
            return;
        }

        Locale spanish = new Locale("spa", "ESP");
        if(mTTS.isLanguageAvailable(spanish) == TextToSpeech.LANG_AVAILABLE){
            mTTS.setLanguage(spanish);
        }else if(mTTS.isLanguageAvailable(Locale.US) == TextToSpeech.LANG_AVAILABLE){
            mTTS.setLanguage(Locale.US);
        }
    }

    public void speak(String text){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, new Bundle(), "END_ID");
        }else{
            mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, mProperties);
        }

    }

}
