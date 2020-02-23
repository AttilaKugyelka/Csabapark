package hu.weblapp.csabapark;


import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static Context conti;
    private WebView webview=null;
    private int backCounter=0;

    String[][] gombok_linkek=new String[][]{
            {"Hírek","/hirek/"},
            {"Heti menü","/etterem/heti-menu/"},
            {"Kapcsolat", "/kapcsolat/"}};

    LinearLayout gombok=null;

    ProgressDialog progressDialog = null;

    String savedWEbString="http://www.csabapark.com/";

    protected void onSaveInstanceState(Bundle outSate){
        super.onSaveInstanceState(outSate);
        outSate.putString("webUrl", webview.getUrl());
    }


    private BroadcastReceiver wifiReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int WifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
            switch (WifiState){
                case WifiManager.WIFI_STATE_ENABLING: {
                    try {

                        webview.loadUrl(savedWEbString);
                    }catch (Exception fg){

                    }
                }
                case WifiManager.WIFI_STATE_ENABLED: {
                    try {

                        webview.loadUrl(savedWEbString);
                    }catch (Exception fg){

                    }
                }
                break;
                case WifiManager.WIFI_STATE_DISABLING: {

                }
                break;
                case WifiManager.WIFI_STATE_DISABLED: {

                }
                break;
            }
        }

    };

    protected void onStop(){
        super.onStop();
        savedWEbString=webview.getUrl();

    }

    protected void onResume() {
        super.onResume();
        if(isNetworkAvailable()){
            webview.reload();
        }
        if(getIntent().getExtras()!=null){
            savedWEbString=getIntent().getStringExtra("kedvencLink");
            webview.loadUrl(savedWEbString);
        }else  {
            webview.loadUrl(savedWEbString);
        }
        try {
            registerReceiver(wifiReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
        }catch (Exception h){}
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        File configDirectory=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "csabaparkTemp");
        if(!configDirectory.exists()){
            configDirectory.mkdirs();
        }

        final File config=new File(configDirectory, "config.txt");
        if(!config.isFile()){
            try {
                config.createNewFile();
                FileOutputStream fosNew=new FileOutputStream(config);
                String cim="[Kedvencek]"+"\n";
                fosNew.write(cim.getBytes());
                fosNew.flush();
                fosNew.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ImageButton kedvencGomb=findViewById(R.id.kedvenc);
        kedvencGomb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempUrl=webview.getUrl()+"\n";
                        try {
                            FileOutputStream fos = new FileOutputStream(config, true);

                            byte[] kedvenc = tempUrl.getBytes();
                            fos.write(kedvenc);
                            fos.flush();
                            fos.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
            }
        });

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        gombok=(LinearLayout) findViewById(R.id.gomb_array_lin_lay);
        gombok.setMinimumWidth(width);

        gombokatGeneral(gombok_linkek, gombok);
        webview=(WebView)findViewById(R.id.htmlTextView);

        webview.getSettings().setJavaScriptEnabled(true);
        if(webview!=null) {
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("Betöltés");
            progressDialog.setTitle("Betöltés alatt...");
            progressDialog.setCancelable(false);
            progressDialog.setButton("Mégsem", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    progressDialog.dismiss();
                }
            });
            progressDialog.show();
        }

        webview.setWebViewClient(new WebViewClient() {

            public void onPageStarted(WebView view, String url, Bitmap favicon){
                try {
                    webview.setVisibility(View.INVISIBLE);
                    progressDialog.show();
                    if(!isNetworkAvailable()){
                        try {
                            new CountDownTimer(3000, 100) {

                                public void onTick(long millisUntilFinished) {
                                    if(isNetworkAvailable()){
                                        try {
                                            webview.loadUrl(savedWEbString);

                                        }catch (Exception j){

                                        }
                                    }
                                }

                                public void onFinish() {
                                    progressDialog.dismiss();
                                }
                            }.start();
                        }catch (Exception g){

                        }
                    }
                }catch (Exception h){

                }
            }

            public void onPageFinished(WebView view, String url)
            {
                super.onPageFinished(view, url);
                try {
                    webview.loadUrl("javascript:(function() { " +
                            "document.getElementsByClassName('et_pb_fullwidth_menu et_pb_module et_pb_bg_layout_dark et_pb_text_align_left et_dropdown_animation_fade  et_pb_fullwidth_menu_0')[0].remove(); " +
                            "document.getElementsByClassName('et_pb_section  et_pb_section_0 et_pb_with_background et_section_regular')[0].remove();" +
                            "document.getElementsByClassName('et_pb_section et_pb_fullwidth_section  et_pb_section_3 et_pb_with_background et_section_regular')[0].remove();" +
                            "})()");
                    if (webview.getProgress() == 100) {
                        progressDialog.dismiss();
                        webview.setVisibility(View.VISIBLE);
                    }
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }catch (Exception g){

                }
            }
        });
        try {
            webview.getSettings().setDomStorageEnabled(true);
            webview.getSettings().setAppCacheEnabled(true);
            webview.getSettings().setAllowFileAccess(true);
            webview.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
            if(isNetworkAvailable()){
                webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            }else{
                webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            }
            savedWEbString = savedInstanceState.getString("webUrl");
            webview.loadUrl(savedWEbString);

        }catch (Exception g){

        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void gombokatGeneral(String[][] elso, final LinearLayout gombok){
        for(final String[] temp: elso){
            final Button tempGomb=new Button(this);
            tempGomb.setText(temp[0]);
            tempGomb.setBackgroundColor(Color.parseColor("#28746e"));
            tempGomb.setTextColor(Color.WHITE);
            tempGomb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if(isNetworkAvailable()){
                            webview.reload();
                        }
                        webview.loadUrl("http://www.csabapark.com"+temp[1]);

                    }catch (Exception hh){}
                }
            });
            gombok.addView(tempGomb);
        }
    }

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.kedvencek:
                Intent kedvencekInt=new Intent(this, Kedvencek.class);
                startActivity(kedvencekInt);
                return true;
            case R.id.megosztas:
                String urlToShare=webview.getUrl();
                try {
                    Intent mIntentFacebook = new Intent();
                    mIntentFacebook.setClassName("com.facebook.katana", "com.facebook.composer.shareintent.ImplicitShareIntentHandlerDefaultAlias");
                    mIntentFacebook.setAction("android.intent.action.SEND");
                    mIntentFacebook.setType("text/plain");
                    mIntentFacebook.putExtra("android.intent.extra.TEXT", urlToShare);
                    startActivity(mIntentFacebook);
                } catch (Exception e) {
                    e.printStackTrace();
                    Intent mIntentFacebookBrowser = new Intent(Intent.ACTION_SEND);
                    String mStringURL = "https://www.facebook.com/sharer/sharer.php?u=" + urlToShare;
                    mIntentFacebookBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(mStringURL));
                    startActivity(mIntentFacebookBrowser);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.kezdolap:
                if(isNetworkAvailable()){
                    webview.reload();
                }
                webview.loadUrl("http://www.csabapark.com/");
                return true;
            case R.id.hetimenu:
                if(isNetworkAvailable()){
                    webview.reload();
                }
                webview.loadUrl("http://www.csabapark.com/etterem/heti-menu/");
                return true;
            case R.id.hetvegimenu:
                if(isNetworkAvailable()){
                    webview.reload();
                }
                webview.loadUrl("http://www.csabapark.com/etterem/hetvegi-szabadszedeses-menu/");
                return true;
            case R.id.galeria:
                if(isNetworkAvailable()){
                    webview.reload();
                }
                webview.loadUrl("http://www.csabapark.com/galeria/");
                return true;
            case R.id.csabai_kolbaszrol:
                if(isNetworkAvailable()){
                    webview.reload();
                }
                webview.loadUrl("http://www.csabapark.com/csabai-kolbaszrol/");
                return true;
            case R.id.kolbasz_tortenete:
                if(isNetworkAvailable()){
                    webview.reload();
                }
                webview.loadUrl("http://www.csabapark.com/csabai-kolbasz/tortenet/");
                return true;
            case R.id.szolgaltatasok:
                if(isNetworkAvailable()){
                    webview.reload();
                }
                webview.loadUrl("http://www.csabapark.com/szolgaltatasok/");
                return true;
            case R.id.uzlet:
                if(isNetworkAvailable()){
                    webview.reload();
                }
                webview.loadUrl("http://www.csabapark.com/uzlet/");
                return true;
            case R.id.rendezvenyek:
                if(isNetworkAvailable()){
                    webview.reload();
                }
                webview.loadUrl("http://www.csabapark.com/etterem/rendezvenyek/");
                return true;
            case R.id.oriasok_konyhaja:
                webview.loadUrl("http://www.csabapark.com/park/oriasok-konyhaja/");
                return true;
            case R.id.kolbasz_muzeum:
                if(isNetworkAvailable()){
                    webview.reload();
                }
                webview.loadUrl("http://www.csabapark.com/park/kolbaszmuzeum/");
                return true;
            case R.id.rendezvenycsarnok:
                if(isNetworkAvailable()){
                    webview.reload();
                }
                webview.loadUrl("http://www.csabapark.com/park/rendezveny-csarnok/");
                return true;
            case R.id.kolbaszmuhely:
                if(isNetworkAvailable()){
                    webview.reload();
                }
                webview.loadUrl("http://www.csabapark.com/park/kolbaszmuhely/");
                return true;
            case R.id.allatsimogato:
                if(isNetworkAvailable()){
                    webview.reload();
                }
                webview.loadUrl("http://www.csabapark.com/park/allatsimogato/");
                return true;
            case R.id.kilep:
                finish();
                return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
