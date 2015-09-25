package com.mobileia.audionews.library;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.widget.Toast;

/**
 * Created by matiascamiletti on 24/9/15.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
public class MCUtteranceListener extends UtteranceProgressListener implements TextToSpeech.OnUtteranceCompletedListener {

    private MCSpeechListener mListener = null;

    public void onEnd(String utteranceId){
        //if (utteranceId == "END_ID") {
            // Callback ya termino de hablar el texto ingresado.
        //}
        //Toast.makeText(mContext, "Ya termino de reproducir.", Toast.LENGTH_SHORT).show();
        if(mListener != null){
            mListener.onComplete(utteranceId);
        }
    }

    @Override
    public void onStart(String utteranceId) {

    }

    @Override
    public void onDone(String utteranceId) {
        onEnd(utteranceId);
    }

    @Override
    public void onError(String utteranceId) {

    }

    @Override
    public void onUtteranceCompleted(String utteranceId) {
        onEnd(utteranceId);
    }

    public void setListener(MCSpeechListener listener){
        mListener = listener;
    }
}
