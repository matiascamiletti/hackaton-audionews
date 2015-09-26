package com.mobileia.audionews.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.mobileia.audionews.Keys;
import com.mobileia.audionews.R;
import com.mobileia.audionews.dialog.AuthorDialog;
import com.mobileia.audionews.dialog.RecorderDialog;
import com.mobileia.audionews.library.MCSpeech;
import com.mobileia.audionews.library.MCSpeechListener;
import com.mobileia.audionews.library.MCUtteranceListener;
import com.mobileia.audionews.model.LNNews;
import com.mobileia.audionews.service.LaNacion;
import com.mobileia.audionews.service.ServiceListener;
import com.mobileia.audionews.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class DetailActivity extends BaseSpeechActivity implements MCSpeechListener {

    private static final String ARG_IDENTIFIER = "detail_identifier";
    private static final String ARG_TITLE = "detail_title";
    private static final String ARG_IMAGE = "detail_image";

    private static final String UID_TITLE = "title";
    private static final String UID_BAJADA = "bajada";

    public static void createInstance(Activity activity, LNNews news) {
        Intent intent = new Intent(activity, DetailActivity.class);
        intent.putExtra(ARG_IDENTIFIER, news.identifier);
        intent.putExtra(ARG_TITLE, news.title);
        intent.putExtra(ARG_IMAGE, news.image);
        activity.startActivity(intent);
    }

    protected LNNews mNews;

    protected FloatingActionButton mFloatingButton;

    protected TextView mTitleView;
    protected TextView mBajadaView;

    protected List<TextView> mListTexts = new ArrayList<TextView>();
    protected String mLastUid = UID_TITLE;
    protected boolean isRestart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initToolbar();
        initFloatingButton();
        loadNews();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isRestart = true;
        mSpeech.stop();
    }

    private void playSpeech(){
        // Cambiar Floating Button al Pause
        mFloatingButton.setImageResource(R.drawable.ic_pause_circle_outline_white);

        isPause = false;
        mSpeech.stop();
        stickTitle(true);
        stickBajada(false);
        stickContentClean();
        mSpeech.speak(mNews.title, UID_TITLE, this);
    }

    private void resumeSpeech(){
        // Cambiar Floating Button al Pause
        mFloatingButton.setImageResource(R.drawable.ic_pause_circle_outline_white);

        isPause = false;
        completeSpeech(mLastUid);
    }

    private void pauseSpeech(){
        // Cambiar Floating Button al Pause
        mFloatingButton.setImageResource(R.drawable.ic_play_circle_outline_white);

        mSpeech.stop();
        isPause = true;

        stickTitle(false);
        stickBajada(false);
        stickContentClean();
    }

    private void completeSpeech(String utteranceId){
        System.out.println("Speech Complete");
        if(utteranceId.compareTo(UID_TITLE) == 0){
            mSpeech.speak(mNews.bajada, UID_BAJADA, DetailActivity.this);
            stickTitle(false);
            stickBajada(true);
            stickContentClean();
        }else if(utteranceId.compareTo(UID_BAJADA) == 0){
            stickBajada(false);
            stickContent(0, 0);
        }else{
            String[] data = utteranceId.split("-");
            stickContent(Integer.valueOf(data[0]), Integer.valueOf(data[1]));
        }
    }

    @Override
    public void onComplete(final String utteranceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isPause || isRestart) {
                    mLastUid = UID_TITLE;
                    isRestart = false;
                    return;
                }
                mLastUid = utteranceId;
                completeSpeech(utteranceId);
            }
        });


    }

    private void stickTitle(boolean select){
        if(select){
            mTitleView.setBackgroundResource(R.color.colorAccent);
            mTitleView.setTextColor(Color.WHITE);
        }else{
            mTitleView.setBackgroundColor(Color.TRANSPARENT);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                mTitleView.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
            }else{
                mTitleView.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        }

    }

    private void stickBajada(boolean select){
        if(select){
            mBajadaView.setBackgroundResource(R.color.colorAccent);
        }else{
            mBajadaView.setBackgroundColor(Color.TRANSPARENT);
        }
        mBajadaView.invalidate();
    }

    private void stickContentClean(){
        for (int i = 0; i < mListTexts.size(); i++) {
            TextView textPrev = mListTexts.get(i);
            textPrev.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private void stickContent(int element, int offset){
        if(element > 0){
            TextView textPrev = mListTexts.get(element-1);
            textPrev.setBackgroundColor(Color.TRANSPARENT);
        }

        if(element >= mListTexts.size()){
            isSpeeching = false;
            isPause = false;
            return;
        }

        TextView text = mListTexts.get(element);
        text.setBackgroundResource(R.color.colorAccent);
        /*String s = mNews.content.get(element);

        System.out.println("Speech String: " + s);

        Spannable spanna = new SpannableString(s);
        int color;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            color = ContextCompat.getColor(this, R.color.colorAccent);
        }else{
            color = getResources().getColor(R.color.colorAccent);
        }
        int endOffset = StringUtil.countWordsOffset(s, offset);
        spanna.setSpan(new BackgroundColorSpan(color), offset, endOffset, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        text.setText(spanna);*/

        mSpeech.speak(mNews.content.get(element), (element+1) + "-0", DetailActivity.this);
    }

    private void loadNews(){
        if(getIntent().getExtras() == null||getIntent().getExtras().isEmpty()){
            finish();
            return;
        }

        mNews = new LNNews();
        mNews.identifier = getIntent().getExtras().getInt(ARG_IDENTIFIER);
        mNews.title = getIntent().getExtras().getString(ARG_TITLE);
        mNews.image = getIntent().getExtras().getString(ARG_IMAGE);

        showPreview();
        downloadNews();
    }

    private void showPreview(){
        mTitleView = (TextView)findViewById(R.id.info_title);
        mTitleView.setText(mNews.title);

        ImageView imageView = (ImageView)findViewById(R.id.info_image);
        if(mNews.image != null && mNews.image.length() > 0){
            Ion.with(imageView)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .load(Keys.BASE_URL_IMAGES + mNews.image);
        }else{
            imageView.setImageResource(R.drawable.placeholder);
        }
    }

    private void showFullNews(){
        LinearLayout content = (LinearLayout)findViewById(R.id.info_content);
        for (int i = 0; i < mNews.content.size(); i++) {
            String parrafo = mNews.content.get(i);

            if(parrafo.length() == 0){
                continue;
            }

            content.addView(createTextView(parrafo));
        }

        mBajadaView = (TextView)findViewById(R.id.info_bajada);

        if(mNews.bajada.length() == 0){
            mBajadaView.setVisibility(View.GONE);
        }else{
            mBajadaView.setText(mNews.bajada);
        }

    }

    private TextView createTextView(String words){
        TextView text = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
        text.setLayoutParams(params);
        text.setText(words);
        text.setTextSize(19);
        mListTexts.add(text);
        return text;
    }

    private void downloadNews(){
        LaNacion.getNews(this, mNews.identifier, new ServiceListener() {
            @Override
            public void onComplete(List<LNNews> list) {
                mNews = list.get(0);
                showFullNews();
            }
        });
    }

    private void initToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Hide title
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getSupportActionBar().setLogo(R.drawable.navigationbar_logo);
        // Set button menu
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace_black);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initFloatingButton(){
        mFloatingButton = (FloatingActionButton) findViewById(R.id.fab);
        mFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isSpeeching && !isPause) {
                    playSpeech();
                } else if (isPause) {
                    resumeSpeech();
                    isPause = false;
                } else {
                    pauseSpeech();
                    isPause = true;
                }
                isSpeeching = !isSpeeching;
            }
        });

        FloatingActionButton btnRestart = (FloatingActionButton)findViewById(R.id.btnRestart);
        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSpeeching) {
                    isRestart = true;
                }
                playSpeech();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { getMenuInflater().inflate(R.menu.menu_detail, menu); return true; }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_headphone:
                if(AuthorDialog.verifyLogin(this)){
                    new RecorderDialog(this, mNews.identifier).show();
                }else{
                    new AuthorDialog(this, mNews.identifier).show();
                }
                return true;
            case R.id.menu_share:
                share();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void share(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "http://www.lanacion.com.ar/" + mNews.identifier);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

}
