package com.mobileia.audionews.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.mobileia.audionews.Keys;
import com.mobileia.audionews.R;
import com.mobileia.audionews.library.MCSpeech;
import com.mobileia.audionews.model.LNNews;
import com.mobileia.audionews.service.LaNacion;
import com.mobileia.audionews.service.ServiceListener;

import java.util.List;

public class DetailActivity extends BaseSpeechActivity {

    private static final String ARG_IDENTIFIER = "detail_identifier";
    private static final String ARG_TITLE = "detail_title";
    private static final String ARG_IMAGE = "detail_image";

    public static void createInstance(Activity activity, LNNews news) {
        Intent intent = new Intent(activity, DetailActivity.class);
        intent.putExtra(ARG_IDENTIFIER, news.identifier);
        intent.putExtra(ARG_TITLE, news.title);
        intent.putExtra(ARG_IMAGE, news.image);
        activity.startActivity(intent);
    }

    protected LNNews mNews;

    protected FloatingActionButton mFloatingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initToolbar();
        initFloatingButton();
        loadNews();
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
        TextView titleView = (TextView)findViewById(R.id.info_title);
        titleView.setText(mNews.title);

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

            TextView text = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
            text.setLayoutParams(params);
            text.setText(parrafo);
            text.setTextSize(19);
            content.addView(text);
        }

        TextView txtBajada = (TextView)findViewById(R.id.info_bajada);

        if(mNews.bajada.length() == 0){
            txtBajada.setVisibility(View.GONE);
        }else{
            txtBajada.setText(mNews.bajada);
        }

    }

    private void downloadNews(){
        LaNacion.getNews(this, mNews.identifier, new ServiceListener() {
            @Override
            public void onComplete(List<LNNews> list) {
                mNews = list.get(0);
                showFullNews();
                //System.out.println("News Info: " + news.bajada);
                //System.out.println("News Info Content: " + news.content);
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
                    //startAllSpeech();
                } else if (isPause) {
                    //resumeSpeech();
                    isPause = false;
                } else {
                    //pauseSpeech();
                    isPause = true;
                }
                isSpeeching = !isSpeeching;

                //mSpeech.speak("Probando la aplicacion, por favor funciona loca!");
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { return true; }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
