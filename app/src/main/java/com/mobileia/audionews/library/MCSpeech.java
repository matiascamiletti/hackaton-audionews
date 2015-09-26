package com.mobileia.audionews.library;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

    private void initTTS(){
        mTTS = new TextToSpeech(mContext, this);
        configureParameters();
    }

    private void checkTTS(){
        SharedPreferences prefs = mContext.getSharedPreferences("mc_speech", Context.MODE_PRIVATE);
        if(prefs.getInt("check_tts", 0) != 0){
            initTTS();
            return;
        }

        Intent check = new Intent();
        check.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        mContext.startActivityForResult(check, SPEECH_CHECK_CODE);

        SharedPreferences.Editor edi = prefs.edit();
        edi.putInt("check_tts", 1);
        edi.commit();
    }

    private void configureParameters(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1){
            mTTS.setOnUtteranceProgressListener(mUtteranceListener);
        }else{
            mTTS.setOnUtteranceCompletedListener(mUtteranceListener);
        }

        mProperties.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_ALARM));
        //mProperties.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "END_ID");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode != SPEECH_CHECK_CODE){
            return;
        }

        if(resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS){
            initTTS();
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

    public void speak(String text, String uid, MCSpeechListener listener){
        _speak(text, uid, listener, TextToSpeech.QUEUE_FLUSH);
    }

    public void speakAdd(String text, String uid, MCSpeechListener listener){
        _speak(text, uid, listener, TextToSpeech.QUEUE_ADD);
    }

    private void _speak(String text, String uid, MCSpeechListener listener, int queue){
        if(listener != null){
            mUtteranceListener.setListener(listener);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            mTTS.setOnUtteranceProgressListener(mUtteranceListener);
            mTTS.speak(text, queue, new Bundle(), uid);
        } else {
            mTTS.setOnUtteranceCompletedListener(mUtteranceListener);
            mProperties.remove(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);
            mProperties.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, uid);
            mTTS.speak(text, queue, mProperties);
        }
    }

    public void stop(){
        if(mTTS != null){
            mTTS.stop();
        }
    }
}
