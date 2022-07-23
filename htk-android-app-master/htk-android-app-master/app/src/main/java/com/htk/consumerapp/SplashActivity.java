package com.htk.consumerapp;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class SplashActivity extends Activity {
    Handler handler;
    String Uid = "";
    final String TAG = "Splash Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Uid = SaveUser.getUserName(SplashActivity.this);
        if(Uid.length() > 1)
        {
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e("Splash", "Uid: "+Uid);
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class).putExtra("id", Uid));
                    finish();
                }
            }, 1500);
        }
        else
        {
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }, 1500);
        }

    }


}