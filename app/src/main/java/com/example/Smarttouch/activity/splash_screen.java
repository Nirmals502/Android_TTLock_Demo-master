package com.example.Smarttouch.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.example.Smarttouch.R;
import com.example.Smarttouch.app.Config;

public class splash_screen extends Activity {
    private static int SPLASH_TIME_OUT = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                SharedPreferences pref = getSharedPreferences("Smart_touch", MODE_PRIVATE);
                String accesstocken = pref.getString("Acess_tocken", "nodata");
                if(accesstocken.contentEquals("nodata")){
                    Intent i = new Intent(splash_screen.this, AuthActivity.class);
                    startActivity(i);
                }else{
                    Intent i = new Intent(splash_screen.this, MainActivity.class);
                    startActivity(i);
                }


                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}
