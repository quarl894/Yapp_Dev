package yapp.dev_diary;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.io.IOException;

/**
 * Created by HANSUNG on 2017-11-10.
 */

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        ImageView img = (ImageView) findViewById(R.id.gif_image);
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(img);
        Glide.with(this).load(R.raw.ttt).into(gifImage);
//        try{
//            Thread.sleep(2000);
//            Intent i = new Intent(SplashActivity.this,MainActivity.class);
//            startActivity(i);
//            finish();
//        }catch (Exception e){ e.printStackTrace();}

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, 3000);
    }
}
