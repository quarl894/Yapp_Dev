package yapp.dev_diary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.io.IOException;

import yapp.dev_diary.Mark.MarkActivity;

/**
 * Created by HANSUNG on 2017-11-10.
 */

public class SplashActivity extends AppCompatActivity {
    public SharedPreferences prefs;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        prefs = getSharedPreferences("Pref", MODE_PRIVATE);
        ImageView img = (ImageView) findViewById(R.id.gif_image);
        img.setImageResource(R.drawable.splash_img);
//        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(img);
//        Glide.with(this).load(R.raw.ttt).into(gifImage);
        checkFirstRun();
    }
    public void checkFirstRun(){
        boolean isFirstRun = prefs.getBoolean("isFirstRun",true);
        if(isFirstRun)
        {
            Log.e("Is first Time?", "first");
            prefs.edit().putBoolean("isFirstRun",false).apply();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashActivity.this, MarkActivity.class);
                    startActivity(i);
                    finish();
                }
            }, 3000);
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }, 3000);
            Log.e("Is first Time?", "not first");
        }
    }
}
