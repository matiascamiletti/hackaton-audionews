package com.mobileia.audionews.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.mobileia.audionews.R;
import com.mobileia.audionews.library.MCRecorder;

/**
 * Created by matiascamiletti on 26/9/15.
 */
public class AuthorDialog extends Dialog {

    protected int mIdentifier = 0;

    public AuthorDialog(Context context, int id) {
        super(context);
        mIdentifier = id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_author);
        initViews();
    }

    private void initViews() {
        Button btnLogin = (Button)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save login
                SharedPreferences prefs = getContext().getSharedPreferences("mc_login", Context.MODE_PRIVATE);
                SharedPreferences.Editor edi = prefs.edit();
                edi.putInt("check_login", 1);
                edi.commit();

                dismiss();
                new RecorderDialog(getContext(), mIdentifier).show();
            }
        });
    }

    public static boolean verifyLogin(Context context){
        SharedPreferences prefs = context.getSharedPreferences("mc_login", Context.MODE_PRIVATE);
        if(prefs.getInt("check_login", 0) != 0){
            return true;
        }

        return false;
    }
}
