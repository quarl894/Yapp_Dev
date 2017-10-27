package yapp.dev_diary.Setting;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;
import yapp.dev_diary.R;

/**
 * Created by YOUNGJUNG on 2017-10-15.
 * Reference : https://github.com/CardinalNow/Android-CircleProgressIndicator
 * circle progressbar_gradient
 */

public class Pactivity extends AppCompatActivity {
    ProgressBar mProgressBar;
    Handler handler = new Handler();
    Context context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p);
        context = this.getApplicationContext();
        mProgressBar = (ProgressBar) findViewById(R.id.circle_progress_bar);
        final String FORMAT = "%02d:%02d:%02d";
        // 현재 시간을 받아옴
        mProgressBar.setProgress(100);
//        mp.setProgress(0);
        new Thread(new Runnable() {
            int i = 0;
            int progressStatus = 100;
            public void run() {
                while (progressStatus > 0) {
                    progressStatus -= doWork();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // Update the progress bar
                    handler.post(new Runnable() {
                        public void run() {
                            mProgressBar.setProgress(progressStatus);
                            Log.e("test",Integer.toString(mProgressBar.getProgress()));
                            i++;
                        }
                    });
                }
            }
            private int doWork() {
                return i;
            }
        }).start();
    }
}
