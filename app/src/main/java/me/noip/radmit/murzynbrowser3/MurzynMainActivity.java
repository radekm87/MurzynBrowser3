package me.noip.radmit.murzynbrowser3;

import android.annotation.SuppressLint;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Calendar;

public class MurzynMainActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;

    //    private View mContentView;
    private View mControlsView;
    private boolean mVisible;

    private Handler currentTimeHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
// Hide both the navigation bar and the status bar.
// SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
// a general rule, you should design your app to hide the status bar whenever you
// hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_murzyn_main);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);

        runHandlerCurrentTime();
//        runHandlerReloadPage();
//        mContentView = findViewById(R.id.fullscreen_content);
//
//        // Set up the user interaction to manually show or hide the system UI.
//        mContentView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                toggle();
//            }
//        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
//        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        WebView browser = (WebView) findViewById(R.id.webView);
        Log.i("Murzyn","onCreate Aplikacji MurzynBrowser3" );
        loadUrlToBrowser(browser);
    }

    private void loadUrlToBrowser(WebView browser){
        WebSettings webSettings = browser.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);

        browser.loadUrl("http://192.168.1.24:8088/Murzynek2/");

        browser.setWebViewClient(new WebViewClient(){
           public void onReceivedError(WebView view, int errorCode, String desc, String url){
               Log.e("Murzyn","Blad browsera (ladowania strony) " + errorCode + ", " + desc);
               view.loadUrl("file:///android_asset/error.html");
               runHandlerReloadPage();
           }
        }
        );
    }
    private void reloadUrlToBrowser(WebView browser){
        Log.i("Murzyn","Handler mnie wywolal i odswiezam strone ");

        browser.stopLoading();
        // Make sure you remove the WebView from its parent view before doing anything.
        browser.removeAllViews();

        browser.clearHistory();

        // NOTE: clears RAM cache, if you pass true, it will also clear the disk cache.
        // Probably not a great idea to pass true if you have other WebViews still alive.
        browser.clearCache(true);

        // Loading a blank page is optional, but will ensure that the WebView isn't doing anything when you destroy it.
        browser.loadUrl("about:blank");
        browser.reload();
//        browser.onPause();
//        browser.removeAllViews();
//        browser.destroyDrawingCache();

        // NOTE: This pauses JavaScript execution for ALL WebViews,
        // do not use if you have other WebViews still alive.
        // If you create another WebView after calling this,
        // make sure to call mWebView.resumeTimers().
//        browser.pauseTimers();

        // NOTE: This can occasionally cause a segfault below API 17 (4.2)


        browser.loadUrl("http://192.168.1.24:8088/Murzynek2/");
        browser.setWebViewClient(new WebViewClient(){
                                     public void onReceivedError(WebView view, int errorCode, String desc, String url){
                                         Log.e("Murzyn","Blad browsera (ladowania strony) " + errorCode + ", " + desc);
                                         view.loadUrl("file:///android_asset/error.html");
                                         runHandlerReloadPage();
                                     }
                                 });
//        browser.reload();
//        browser.loadUrl( "javascript:window.location.reload( true )" );

    }

    private void runHandlerCurrentTime() {
        currentTimeHandler.postDelayed(CurrentTimeCharger, 300000); //5min
    }

    private void runHandlerReloadPage() {
        Log.i("Murzyn", "Wchodze w wywolanie handlera odswiezenia strony za minute");
        currentTimeHandler.postDelayed(ReloadPageTimer, 60000); //1minuta
    }

    /**
     * Od godziny 24 wlaczamy systemowe uspienie ekranu do godziny okolo 5 (wlacznie czyli ok 5:59)
     * + - 5 minut bo co tyle wywolywany jest handler
     */
    private Runnable CurrentTimeCharger = new Runnable() {
        @Override
        public void run() {
            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            if ( (hour > 0 && hour < 6) || hour == 24) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
            runHandlerCurrentTime();
        }
    };

    /**
     * Co ok. 5h ma wykonac przeladowanie strony - na wypadek jesli tomcat padl
     * + - 5 minut bo co tyle wywolywany jest handler
     */
    private Runnable ReloadPageTimer = new Runnable() {
        @Override
        public void run() {
            WebView browser = (WebView) findViewById(R.id.webView);
            reloadUrlToBrowser(browser);
//            runHandlerReloadPage();
        }
    };

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

//    private void toggle() {
//        if (mVisible) {
//            hide();
//        } else {
//            show();
//        }
//    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);

        //---------
        View decorView = getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        //---------


        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mHideHandler.postDelayed(mHideRunnable, 5000);
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
//            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
//        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };

    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
