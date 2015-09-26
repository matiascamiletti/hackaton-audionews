package com.mobileia.audionews.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.mobileia.audionews.R;
import com.mobileia.audionews.fragment.NewsFragment;
import com.mobileia.audionews.library.MCSpeech;

public class MainActivity extends AppCompatActivity {

    public static void createInstance(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }

    protected DrawerLayout mDrawerLayout;

    protected NavigationView mNavigationView;

    protected MCSpeech mSpeech;

    protected RelativeLayout mAudioBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFragment(savedInstanceState);
        initNavigationView();
        initToolbar();
        initFloatingButton();
        initTextToSpeech();
        initAudioBar();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSpeech.stop();
    }

    private void initNavigationView(){
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mNavigationView = (NavigationView) findViewById(R.id.navigationView);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_home:
                        //HomeActivity.createInstance(this);
                        break;
                    //case R.id.menu_stats:

                    //break;
                }

                return false;
            }
        });
        //mNavigationView.setPadding(0, 0, 0, 0);
    }

    private void initToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Hide title
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setLogo(R.drawable.navigationbar_logo);
        // Set button menu
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_dehaze_black);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initFloatingButton(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAllSpeech();
                //mSpeech.speak("Probando la aplicacion, por favor funciona loca!");
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
    }

    private void initFragment(Bundle savedInstanceState){
        if (savedInstanceState == null) {
            NewsFragment f = NewsFragment.newInstance("", "");
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, f)
                    .commit();
        }
    }

    private void initAudioBar(){
        mAudioBar = (RelativeLayout)findViewById(R.id.containerAudioBar);
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        mAudioBar.setX(size.x);

        ProgressBar progressBar = (ProgressBar)findViewById(R.id.btnPlayPause);
        //progressBar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
    }

    public void startAllSpeech(){
        mAudioBar.animate().x(0);

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.container);
        if(NewsFragment.class.isInstance(f)){
            ((NewsFragment)f).startSpeech(mSpeech);
        }
    }

    private void initTextToSpeech(){
        mSpeech = new MCSpeech();
        mSpeech.init(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(mSpeech != null){
            mSpeech.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { return true; }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
