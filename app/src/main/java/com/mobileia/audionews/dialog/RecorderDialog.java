package com.mobileia.audionews.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.mobileia.audionews.R;
import com.mobileia.audionews.library.MCRecorder;

/**
 * Created by matiascamiletti on 26/9/15.
 */
public class RecorderDialog extends Dialog {

    protected MCRecorder mRecorder;

    protected ImageView mImgLoader;
    protected AnimationDrawable mAnimation;

    protected int mIdentifier = 0;

    protected Button mBtnSave;
    protected Button mBtnStop;
    protected Button mBtnPlay;
    protected Button mBtnFile;

    public RecorderDialog(Context context, int id) {
        super(context);
        mIdentifier = id;
    }

    protected RecorderDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_recorder);

        mRecorder = new MCRecorder(mIdentifier);

        initViews();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mRecorder.deleteFile();
    }

    private void initViews(){
        mImgLoader = (ImageView)findViewById(R.id.recorderAnimation);
        mImgLoader.setBackgroundResource(R.drawable.loader_recorder);
        mAnimation = (AnimationDrawable)mImgLoader.getDrawable();
        mAnimation.setOneShot(false);

        mBtnSave = (Button)findViewById(R.id.btnSave);
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnSave.setVisibility(View.GONE);
                mBtnStop.setVisibility(View.VISIBLE);

                mAnimation.start();
                mRecorder.startRecording();
            }
        });

        mBtnStop = (Button)findViewById(R.id.btnDetener);
        mBtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnStop.setVisibility(View.GONE);
                mBtnPlay.setVisibility(View.VISIBLE);
                mBtnFile.setVisibility(View.VISIBLE);

                mAnimation.stop();
                mRecorder.stopRecording();
            }
        });

        mBtnPlay = (Button)findViewById(R.id.btnPlay);
        mBtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecorder.startPlaying();
            }
        });

        if(mRecorder.existFile()){
            mBtnPlay.setVisibility(View.VISIBLE);
            mBtnSave.setText("Grabar y Reemplazar");
        }

        mBtnFile = (Button)findViewById(R.id.btnFile);
        mBtnFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

}
