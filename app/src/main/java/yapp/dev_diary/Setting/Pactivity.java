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
 * Created by HANSUNG on 2017-10-15.
 */

public class Pactivity extends AppCompatActivity {
    ProgressBar p_gradient;
    ProgressBar mProgressBar;
    ProgressBar  mp;
    Handler handler = new Handler();
    Context context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p);
        context = this.getApplicationContext();
        p_gradient = (ProgressBar) findViewById(R.id.p_gradient);
        mProgressBar = (ProgressBar) findViewById(R.id.mProgressBar);
        mp = (ProgressBar) findViewById(R.id.progressBar2);
//        mProgressBar.setIndeterminateDrawable(new SmoothProgressDrawable.Builder(context)
//                .colors(getResources().getIntArray(R.array.p_colors))
//                .interpolator(new DecelerateInterpolator())
//                .sectionsCount(1)
//                .separatorLength(8)         //You should use Resources#getDimensionPixelSize
//                .strokeWidth(8f)            //You should use Resources#getDimension
//                .speed(2f)                 //2 times faster
//                .progressiveStartSpeed(2)
//                .progressiveStopSpeed(3.4f)
//                .reversed(false)
//                .mirrorMode(false)
//                .gradients(true)
//                .build());
        final String FORMAT = "%02d:%02d:%02d";
        // 현재 시간을 받아옴

        p_gradient.setProgress(0);
        mProgressBar.setProgress(0);
        mp.setProgress(0);
        new Thread(new Runnable() {
            int i = 0;
            int progressStatus = 0;
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += doWork();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // Update the progress bar
                    handler.post(new Runnable() {
                        public void run() {
                            p_gradient.setProgress(progressStatus);
                            mProgressBar.setProgress(progressStatus);
                            mp.setProgress(progressStatus);
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
