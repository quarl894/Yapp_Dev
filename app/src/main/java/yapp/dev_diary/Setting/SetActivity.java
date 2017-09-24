package yapp.dev_diary.Setting;
<<<<<<< HEAD

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
=======
import java.io.IOException;
import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
>>>>>>> marge
import android.widget.Toast;

import yapp.dev_diary.R;
import yapp.dev_diary.Setting.SunUtil;

public class SetActivity extends Activity implements View.OnClickListener, OnCompletionListener
{
    private static final int REC_STOP = 0;
    private static final int RECORDING = 1;
    private static final int PLAY_STOP = 0;
    private static final int PLAYING = 1;
    private static final int PLAY_PAUSE = 2;

    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    private int mRecState = REC_STOP;
    private int mPlayerState = PLAY_STOP;
    private SeekBar mRecProgressBar, mPlayProgressBar;
    private Button mBtnStartRec, mBtnStartPlay, mBtnStopPlay;
    private String mFilePath, mFileName = "test.amr";
    private TextView mTvPlayMaxPoint;

    private int mCurRecTimeMs = 0;
    private int mCurProgressTimeDisplay = 0;

    // 녹음시 SeekBar처리
    Handler mProgressHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            mCurRecTimeMs = mCurRecTimeMs + 100;
            mCurProgressTimeDisplay = mCurProgressTimeDisplay + 100;

            // 녹음시간이 음수이면 정지버튼을 눌러 정지시켰음을 의미하므로
            // SeekBar는 그대로 정지시키고 레코더를 정지시킨다.
            if (mCurRecTimeMs < 0)
            {}
            // 녹음시간이 아직 최대녹음제한시간보다 작으면 녹음중이라는 의미이므로
            // SeekBar의 위치를 옮겨주고 0.1초 후에 다시 체크하도록 한다.
            else if (mCurRecTimeMs < 60000)
            {
                mRecProgressBar.setProgress(mCurProgressTimeDisplay);
                mProgressHandler.sendEmptyMessageDelayed(0, 100);
            }
            // 녹음시간이 최대 녹음제한 시간보다 크면 녹음을 정지 시킨다.
            else
            {
                mBtnStartRecOnClick();
            }
        }
    };

    // 재생시 SeekBar 처리
    Handler mProgressHandler2 = new Handler()
    {
        public void handleMessage(Message msg)
        {
            if (mPlayer == null) return;

            try
            {
                if (mPlayer.isPlaying())
                {
                    mPlayProgressBar.setProgress(mPlayer.getCurrentPosition());
                    mProgressHandler2.sendEmptyMessageDelayed(0, 100);
                }
            }
            catch (IllegalStateException e)
            {}
            catch (Exception e)
            {}
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

<<<<<<< HEAD
        LinearLayout layout_alarm = (LinearLayout) findViewById(R.id.setting_layout_alarm);
        LinearLayout layout_lock = (LinearLayout) findViewById(R.id.setting_layout_lock);
        LinearLayout layout_allbackup = (LinearLayout) findViewById(R.id.setting_layout_allbackup);
        LinearLayout layout_allclear = (LinearLayout) findViewById(R.id.setting_layout_allclear);

        //onClick Event inner class(추후 메소드로 빼기)
        layout_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickEvent(view);
            }
        });
        layout_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickEvent(view);
            }
        });
        layout_allbackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickEvent(view);
            }
        });
        layout_allclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickEvent(view);
            }
        });
    }

    /**
     * onClick시 Event 분기 메소드
     * @param view Event가 발생한 View를 인자 그대로 넘기면 된다.
     * */
    private void onClickEvent(View view){
        switch (view.getId()) {
            case R.id.setting_layout_alarm :
                Intent intent1 = new Intent(this, AlarmActivity.class);
                startActivity(intent1);
                break;
            case R.id.setting_layout_lock :
//                Intent intent2 = new Intent(this, LockActivity.class);
//                startActivity(intent2);
                Intent intent2 = new Intent(this, LockActivity.class);
                startActivity(intent2);
                break;
            case R.id.setting_layout_allbackup :
                Toast.makeText(this, "전체백업은 백업 되면은...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.setting_layout_allclear :
                Toast.makeText(this, "전체초기화는 DB다 되면...", Toast.LENGTH_SHORT).show();
                break;
        }
=======
        // SD카드에 디렉토리를 만든다.
        mFilePath = SunUtil.makeDir("progress_recorder");

        mBtnStartRec = (Button) findViewById(R.id.btnStartRec);
        mBtnStartPlay = (Button) findViewById(R.id.btnStartPlay);
        mBtnStopPlay = (Button) findViewById(R.id.btnStopPlay);
        mRecProgressBar = (SeekBar) findViewById(R.id.recProgressBar);
        mPlayProgressBar = (SeekBar) findViewById(R.id.playProgressBar);
        mTvPlayMaxPoint = (TextView) findViewById(R.id.tvPlayMaxPoint);

        mBtnStartRec.setOnClickListener(this);
        mBtnStartPlay.setOnClickListener(this);
        mBtnStopPlay.setOnClickListener(this);
    }

    // 버튼의 OnClick 이벤트 리스너
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.btnStartRec:
                mBtnStartRecOnClick();
                break;
            case R.id.btnStartPlay:
                mBtnStartPlayOnClick();
                break;
            case R.id.btnStopPlay:
                mBtnStopPlayOnClick();
                break;
            default:
                break;
        }
    }

    private void mBtnStartRecOnClick()
    {
        if (mRecState == REC_STOP)
        {
            mRecState = RECORDING;
            startRec();
            updateUI();
        }
        else if (mRecState == RECORDING)
        {
            mRecState = REC_STOP;
            stopRec();
            updateUI();
        }
    }

    // 녹음시작
    private void startRec()
    {
        mCurRecTimeMs = 0;
        mCurProgressTimeDisplay = 0;

        // SeekBar의 상태를 0.1초후 체크 시작
        mProgressHandler.sendEmptyMessageDelayed(0, 100);

        if (mRecorder == null)
        {
            mRecorder = new MediaRecorder();
            mRecorder.reset();
        }
        else
        {
            mRecorder.reset();
        }

        try
        {
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            mRecorder.setOutputFile(mFilePath + mFileName);
            mRecorder.prepare();
            mRecorder.start();
        }
        catch (IllegalStateException e)
        {
            Toast.makeText(this, "IllegalStateException", 1).show();
        }
        catch (IOException e)
        {
            Toast.makeText(this, "IOException", 1).show();
        }
    }

    // 녹음정지
    private void stopRec()
    {
        try
        {
            mRecorder.stop();
        }
        catch(Exception e)
        {}
        finally
        {
            mRecorder.release();
            mRecorder = null;
        }

        mCurRecTimeMs = -999;
        // SeekBar의 상태를 즉시 체크
        mProgressHandler.sendEmptyMessageDelayed(0, 0);
    }

    private void mBtnStartPlayOnClick()
    {
        if (mPlayerState == PLAY_STOP)
        {
            mPlayerState = PLAYING;
            initMediaPlayer();
            startPlay();
            updateUI();
        }
        else if (mPlayerState == PLAYING)
        {
            mPlayerState = PLAY_PAUSE;
            pausePlay();
            updateUI();
        }
        else if (mPlayerState == PLAY_PAUSE)
        {
            mPlayerState = PLAYING;
            startPlay();
            updateUI();
        }
    }

    private void mBtnStopPlayOnClick()
    {
        if (mPlayerState == PLAYING || mPlayerState == PLAY_PAUSE)
        {
            mPlayerState = PLAY_STOP;
            stopPlay();
            releaseMediaPlayer();
            updateUI();
        }
    }

    private void initMediaPlayer()
    {
        // 미디어 플레이어 생성
        if (mPlayer == null)
            mPlayer = new MediaPlayer();
        else
            mPlayer.reset();

        mPlayer.setOnCompletionListener(this);
        String fullFilePath = mFilePath + mFileName;

        try
        {
            mPlayer.setDataSource(fullFilePath);
            mPlayer.prepare();
            int point = mPlayer.getDuration();
            mPlayProgressBar.setMax(point);

            int maxMinPoint = point / 1000 / 60;
            int maxSecPoint = (point / 1000) % 60;
            String maxMinPointStr = "";
            String maxSecPointStr = "";

            if (maxMinPoint < 10)
                maxMinPointStr = "0" + maxMinPoint + ":";
            else
                maxMinPointStr = maxMinPoint + ":";

            if (maxSecPoint < 10)
                maxSecPointStr = "0" + maxSecPoint;
            else
                maxSecPointStr = String.valueOf(maxSecPoint);

            mTvPlayMaxPoint.setText(maxMinPointStr + maxSecPointStr);

            mPlayProgressBar.setProgress(0);
        }
        catch(Exception e)
        {
            Log.v("ProgressRecorder", "미디어 플레이어 Prepare Error ==========> " + e);
        }
    }

    // 재생 시작
    private void startPlay()
    {
        Log.v("ProgressRecorder", "startPlay().....");

        try
        {
            mPlayer.start();

            // SeekBar의 상태를 0.1초마다 체크
            mProgressHandler2.sendEmptyMessageDelayed(0, 100);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(this, "error : " + e.getMessage(), 0).show();
        }
    }

    private void pausePlay()
    {
        Log.v("ProgressRecorder", "pausePlay().....");

        // 재생을 일시 정지하고
        mPlayer.pause();

        // 재생이 일시정지되면 즉시 SeekBar 메세지 핸들러를 호출한다.
        mProgressHandler2.sendEmptyMessageDelayed(0, 0);
    }

    private void stopPlay()
    {
        Log.v("ProgressRecorder", "stopPlay().....");

        // 재생을 중지하고
        mPlayer.stop();

        // 즉시 SeekBar 메세지 핸들러를 호출한다.
        mProgressHandler2.sendEmptyMessageDelayed(0, 0);
    }

    private void releaseMediaPlayer()
    {
        Log.v("ProgressRecorder", "releaseMediaPlayer().....");
        mPlayer.release();
        mPlayer = null;
        mPlayProgressBar.setProgress(0);
    }

    public void onCompletion(MediaPlayer mp)
    {
        mPlayerState = PLAY_STOP; // 재생이 종료됨
        // 재생이 종료되면 즉시 SeekBar 메세지 핸들러를 호출한다.
        mProgressHandler2.sendEmptyMessageDelayed(0, 0);

        updateUI();
    }

    private void updateUI()
    {
        if (mRecState == REC_STOP)
        {
            mBtnStartRec.setText("Rec");
            mRecProgressBar.setProgress(0);
        }
        else if (mRecState == RECORDING)
            mBtnStartRec.setText("Stop");

        if (mPlayerState == PLAY_STOP)
        {
            mBtnStartPlay.setText("Play");
            mPlayProgressBar.setProgress(0);
        }
        else if (mPlayerState == PLAYING)
            mBtnStartPlay.setText("Pause");
        else if (mPlayerState == PLAY_PAUSE)
            mBtnStartPlay.setText("Start");
>>>>>>> marge
    }
}