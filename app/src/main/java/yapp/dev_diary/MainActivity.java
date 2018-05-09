package yapp.dev_diary;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.naver.speech.clientapi.SpeechRecognitionResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import yapp.dev_diary.DB.MyDBHelper;
import yapp.dev_diary.List.ListDActivity;
import yapp.dev_diary.Lock.core.BaseActivity;
import yapp.dev_diary.Setting.BroadcastD;
import yapp.dev_diary.Setting.SetActivity;
import yapp.dev_diary.Voice.VoiceActivity;
import yapp.dev_diary.utils.AudioWriterPCM;

public class MainActivity extends BaseActivity implements MediaRecorder.OnInfoListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String CLIENT_ID = "s2xquX9eCQsCD0xOxV0E";
    ArrayList<String> list_stt;
    String outputFile2;
    MediaPlayer mPlayer = null;
    MediaRecorder mRecorder = null;
    ArrayList<String> outputFileList; // 임시 파일 리스트
    ArrayList<String> outputSttList;
    String mFilePath =  null;
    ImageButton mBtnRecord; //재생&일시정지
    ImageButton mBtnStop; //저장 버튼
    ImageButton mBtnPlay; //재생 버튼
    ImageButton mBtnReset;// 초기화 버튼
    Button mBtnSave;
    ProgressBar p_gradient;
    TextView start_time, end_time;
    int t_count=0;
    private RecognitionHandler handler;
    private NaverRecognizer naverRecognizer;
    private TextView txtResult;
    private String mResult;

    private Handler handler2;
    private Handler handler3;

    private AudioWriterPCM writer;
    int count=0;
    ArrayList<String> pic_path = new ArrayList<>();
    public static ArrayList<String> ok_path = new ArrayList<>();
    P_Thread p_thread;
    MyDBHelper     DBHelper;
    SQLiteDatabase db;

    // Handle speech recognition Messages.
    private void handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.clientReady:
                String tv3 = "";
                if(list_stt.size()==0){
                    tv3 ="";
                }else{
                    for(int i=0; i<list_stt.size(); i++){
                        tv3 += list_stt.get(i)+ " ";
                    }
                }
                // Now an user can speak.
                txtResult.setText(tv3);
                writer = new AudioWriterPCM(
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaverSpeechTest");
                writer.open("Test");
                break;

            case R.id.audioRecording:
                writer.write((short[]) msg.obj);
                break;

            case R.id.partialResult:
                // Extract obj property typed with String.
                String tv ="";
                if(list_stt.size() == 0){
                    tv = "";
                }else{
                    for(int i=0; i<list_stt.size(); i++){
                        tv +=list_stt.get(i)+" ";
                    }
                }
                mResult = (String) (msg.obj);
                txtResult.setText(tv+mResult);
                break;

            case R.id.finalResult:
                // Extract obj property typed with String array.
                // The first element is recognition result for speech.

                SpeechRecognitionResult speechRecognitionResult = (SpeechRecognitionResult) msg.obj;
                List<String> results = speechRecognitionResult.getResults();
                String a = speechRecognitionResult.getResults().get(0);
                StringBuilder strBuf = new StringBuilder();
                for(String result : results) {
                    strBuf.append(result);
                    strBuf.append("\n");
                }
                String tv2 ="";
                mResult = a.toString();
                if(list_stt.size() ==0){
                    txtResult.setText(mResult);
                }else{
                    for(int i=0; i<list_stt.size(); i++){
                        tv2 +=list_stt.get(i)+" ";
                    }
                    txtResult.setText(tv2+mResult);
                }
                // 10초 동안 아무말 없으면 자동으로 꺼지므로 밑에 코드 추가시킴(일시정지)
                list_stt.add(mResult);
                mBtnRecord.setEnabled(true);
                mBtnStop.setEnabled(false);
                mBtnRecord.setVisibility(View.VISIBLE);
                mBtnStop.setVisibility(View.INVISIBLE);
                p_thread.work = false;
                break;

            case R.id.recognitionError:
                if (writer != null) {
                    writer.close();
                }

                mResult = "Error code : " + msg.obj.toString();
                txtResult.setText(mResult);
                break;

            case R.id.clientInactive:
                if (writer != null) {
                    writer.close();
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBHelper = new MyDBHelper(MainActivity.this);
        db        = DBHelper.getWritableDatabase();

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                getImageNameToUri();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "권한이 거부되었습니다. 권한거부시 앱기능 일부분을 사용하실수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        };
        new TedPermission(MainActivity.this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_CALENDAR)
                .check();

        outputSttList = new ArrayList<String>();
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddhhmmss");
        final String getTime = sdf.format(date);
        final String today_time = sdf2.format(date);

        outputFile2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +today_time +".mp4";
        Log.e("today_time", outputFile2);
        handler2 = new Handler();
        list_stt = new ArrayList<String>();
        initToolbar();

        //버튼 레코드는 원래 녹음 시작
        mBtnRecord = (ImageButton)findViewById(R.id.btnRecord);
        mBtnStop = (ImageButton)findViewById(R.id.btnPause);
        mBtnPlay = (ImageButton)findViewById(R.id.btnPlay);
        mBtnReset = (ImageButton)findViewById(R.id.btnReset);
        mBtnSave = (Button)findViewById(R.id.btnSave);
        mBtnRecord.setVisibility(View.VISIBLE);
        mBtnStop.setVisibility(View.INVISIBLE);

        //초기화 버튼 녹음 시작 시에만 활성화
        mBtnReset.setEnabled(false);

        txtResult = (TextView) findViewById(R.id.txt_result);
        handler = new RecognitionHandler(this);
        naverRecognizer = new NaverRecognizer(this, handler, CLIENT_ID);

        p_gradient = (ProgressBar) findViewById(R.id.p_gradient);
        start_time = (TextView) findViewById(R.id.start_time);
        end_time = (TextView) findViewById(R.id.end_time);
        p_gradient.setMax(60);

        try {
            //사진 찍은 날짜 정보 가져오기
            ExifInterface exif = new ExifInterface(pic_path.get(0));
            ExifInterface exif2 = new ExifInterface(pic_path.get(1));
            ExifInterface exif3 = new ExifInterface(pic_path.get(2));
            ExifInterface exif4 = new ExifInterface(pic_path.get(3));
            ExifInterface exif5 = new ExifInterface(pic_path.get(4));
            //Uri에서 이미지 이름을 얻어온다.
            if(showExif(exif).equals(getTime)) ok_path.add(pic_path.get(0));
            if(showExif(exif2).equals(getTime)) ok_path.add(pic_path.get(1));
            if(showExif(exif3).equals(getTime)) ok_path.add(pic_path.get(2));
            if(showExif(exif4).equals(getTime)) ok_path.add(pic_path.get(3));
            if(showExif(exif5).equals(getTime)) ok_path.add(pic_path.get(4));
        }catch (Exception e) {
            e.printStackTrace();
//            Log.e("pic_path_info", " " + pic_path.get(0));
        }
        Log.e("testtest",Integer.toString(ok_path.size()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.menu_start :
                Intent i = new Intent(this, VoiceActivity.class);
                startActivity(i);
                return true;
            case R.id.menu_list :
                Intent i2 = new Intent(this, ListDActivity.class);
                startActivity(i2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBtnPlay(){
        Log.e("seoheeing", "Record Prepare error");
        if(mPlayer != null){
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        Toast.makeText(getApplicationContext(), "녹음된 파일을 재생합니다.", Toast.LENGTH_LONG).show();
        try {
            // 오디오를 플레이 하기위해 MediaPlayer 객체 player를 생성한다.
            mPlayer = new MediaPlayer ();

            // 재생할 오디오 파일 저장위치를 설정
            mPlayer.setDataSource(outputFile2);
            // 웹상에 있는 오디오 파일을 재생할때
            // player.setDataSource(Audio_Url);

            // 오디오 재생준비,시작
            mPlayer.prepare();
            mPlayer.start();
        }catch (Exception e) {
            Log.d("SampleAudioRecorder", "Audio play failed.");
        }

    }
    public void onBtnReset(){
        outputSttList.clear();
        count =0;
        txtResult.setText("");
        list_stt.clear();
        p_gradient.setProgress(0);
    }

    public void startMerge2(ArrayList<String> outputFileList)throws IOException
    {
        Movie[] inMovies = new Movie[outputFileList.size()];
        try
        {
            Log.e("file_size", ""+ Integer.toString(outputFileList.size()));
            for(int a = 0; a < outputFileList.size(); a++)
            {
                inMovies[a] = MovieCreator.build(outputFileList.get(a));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        List<Track> audioTracks = new LinkedList<Track>();
        for (Movie m : inMovies)
        {
            for (Track t : m.getTracks())
            {
                if (t.getHandler().equals("soun"))
                {
                    audioTracks.add(t);
                }
            }
        }

        Movie output = new Movie();
        if (audioTracks.size() > 0)
        {
            try
            {
                output.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        Container out = new DefaultMp4Builder().build(output);
        FileChannel fc = null;
        try
        {
            fc = new FileOutputStream(new File(outputFile2)).getChannel();
            Log.e("output",outputFile2);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        try
        {
            out.writeContainer(fc);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        try
        {
            fc.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public void onClick(View v) {
        switch( v.getId() ) {
            case R.id.btnRecord :
                if(t_count==0){
                    //시작시마다 스레드 객체 생성(재사용이 안되서 매번 생성해야함)
                    handler3 = new Handler();
                    p_thread = new P_Thread();
                    p_thread.start();
                    p_thread.stop = false;
                    p_thread.work =true;
                    Log.e("btnRecord 처음시작"," "+p_thread.getState().toString());
                }else if(t_count ==1){
                    p_thread.stop = false;
                    p_thread.work =true;
                    Log.e("btnRecord 재시작"," "+p_thread.getState().toString());
                }
                t_count=1;
                Log.e("thread_status: " ,""+p_thread.getState()+ Integer.toString(t_count));

                if(!naverRecognizer.getSpeechRecognizer().isRunning()) {
                    // Start button is pushed when SpeechRecognizer's state is inactive.
                    // Run SpeechRecongizer by calling recognize().
                    mResult = "";
                    //txtResult.setText("Connecting..."); //불필요한 구문(사용자에게 노출되면 안됨)
                    naverRecognizer.recognize();
                } else {
                    //Log.e(TAG, "stop and wait Final Result");
                    naverRecognizer.getSpeechRecognizer().stop();
                }
                mBtnRecord.setEnabled(false);
                mBtnStop.setEnabled(true);

                // mBtnPlay.setEnabled(false);
                mBtnRecord.setVisibility(View.INVISIBLE);
                mBtnStop.setVisibility(View.VISIBLE);

                // 초기화버튼 활성화
                if (!mBtnReset.isEnabled())
                    mBtnReset.setEnabled(true);

                // 다른 앱 음악 일시정지
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.requestAudioFocus(focusChangeListener,
                        AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN); // 이건 focusChangeListener를 보면 알 수 있다.
                break;
            case R.id.btnPause :
                Log.e("btnPause 일시정지"," "+p_thread.getState().toString());
                p_thread.work =false;
                naverRecognizer.getSpeechRecognizer().stop();
                try {
                    count +=1;
                    mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/sttrecording";
                    String nowFile = mFilePath  + count + ".mp4";
                    encodeSingleFile(nowFile);
                    outputSttList.add(nowFile);
                    Log.e("TAG",""+Integer.toString(outputSttList.size()));
                } catch (Exception e) {
                    Log.e(TAG, "Exception while creating tmp file1", e);
                }

                //이 부분 모듈화(인자에 따른 셋팅) 할 것.
                mBtnRecord.setEnabled(true);
                mBtnStop.setEnabled(false);
                mBtnRecord.setVisibility(View.VISIBLE);
                mBtnStop.setVisibility(View.INVISIBLE);
                break;
            case R.id.btnReset :
                if (p_thread != null) {
                    p_thread.work = false;
                    naverRecognizer.getSpeechRecognizer().stop();
                    onBtnReset();
                    p_thread.stop = true;
                    t_count = 0;

                    mBtnRecord.setEnabled(true);
                    mBtnStop.setEnabled(false);
                    mBtnRecord.setVisibility(View.VISIBLE);
                    mBtnStop.setVisibility(View.INVISIBLE);
                    Log.e("btnReset 리셋", " " + p_thread.getState().toString());
                }
                else{
                    Toast.makeText(MainActivity.this, "녹음 시작 시 초기화 가능", Toast.LENGTH_SHORT).show();
                }
                if (mBtnReset.isEnabled())
                    mBtnReset.setEnabled(false);

                break;
            case R.id.btnPlay:
                onBtnPlay();
                break;
            case R.id.btnSave:
                try {
                    startMerge2(outputSttList);
                    Intent i = new Intent(MainActivity.this, SaveActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    i.putExtra("r_path", outputFile2);
                    i.putExtra("content", txtResult.getText());
                    t_count =0;
                    p_thread.work = false;
                    p_thread.stop = true;
                    Log.e("btnsave 완료"," "+p_thread.getState().toString());
                    startActivity(i);
                } catch (Exception e) {
                    Log.e(TAG, "Exception while creating tmp file", e);
                }finally{
                    onBtnReset();
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // NOTE : initialize() must be called on start time.
        naverRecognizer.getSpeechRecognizer().initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mResult = "";
        txtResult.setText("");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // NOTE : release() must be called on stop time.
        naverRecognizer.getSpeechRecognizer().release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // Declare handler for handling SpeechRecognizer thread's Messages.
    static class RecognitionHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        RecognitionHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }

    public void onInfo(MediaRecorder mr, int what, int extra) {
        switch( what ) {
            case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED :
            case MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED :
                break;
        }
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(null);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SetActivity.class);
                startActivity(i);
            }
        });
    }
    //URL 에서 파일명 추출
    public void getImageNameToUri() {
        String[] proj = { MediaStore.Images.Media.DATA };
        //사진 최신순으로 정렬해서 가져오기
        Cursor cursor = getApplicationContext().getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj, null, null, MediaStore.Images.Media.DATE_TAKEN + " DESC");
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        Log.e("Camera_test2", " " +cursor.getString(column_index));
        for(int i=0; i<5; i++){
            pic_path.add(cursor.getString(column_index));
            cursor.moveToNext(); //다음 사진으로
        }
        String imgPath = cursor.getString(column_index);
        //String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);
        Log.e("Camera_test", " " + imgPath);
//        return imgPath;
    }

    // 사진정보에서 찍은날짜 가져오기
    private String showExif(ExifInterface exif) {
        String a = exif.getAttribute(ExifInterface.TAG_DATETIME); //사진 정보 가져오기
        String pic_date;
        /*
        날짜가 0이거나 null이면 0으로 받고 아니면 날짜 출력(다운로드한 사진은 null값임)
         */
        if (a == null || a.equals("") == true){
            return "0";
        }else{
            pic_date = a.replaceAll(":","");
            return pic_date.substring(0,8);
        }
    }

    private void encodeSingleFile(final String outputPath) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(encodeTask(1, outputPath));
    }

    private Runnable encodeTask(final int numFiles, final String outputPath) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    final PCMEncoder pcmEncoder = new PCMEncoder(48000, 16000, 1);
                    pcmEncoder.setOutputPath(outputPath);
                    pcmEncoder.prepare();
                    File directory = new File("storage/emulated/0/NaverSpeechTest/Test.pcm");
                    for (int i = 0; i < numFiles; i++) {
                        Log.d(TAG, "Encoding: " + i);
                        //InputStream inputStream = getAssets().open("test.wav");
                        InputStream inputStream = new FileInputStream(directory);
                        inputStream.skip(44);
                        pcmEncoder.encode(inputStream, 16000);
                    }
                    pcmEncoder.stop();
                    handler2.post(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.makeText(getApplicationContext(), "Encoded file to: " + outputPath, Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, "Cannot create FileInputStream", e);
                }
            }
        };
    }
    private class P_Thread extends Thread{
        private int progressStatus = 0;
        public boolean stop = false;
        public boolean work = true;
        public void run() {
            while(progressStatus <60 && !Thread.currentThread().isInterrupted()) {
                if (work) {
                    try {
                        progressStatus++;
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // Update the progress bar
                    handler3.post(new Runnable() {
                        public void run() {
                            p_gradient.setProgress(progressStatus);
                            start_time.setText("00:" + String.format("%02d", progressStatus));
                            end_time.setText("00:" + String.format("%02d", 60 - progressStatus));
                        }
                    });
                } else {
                    //Log.e("여기 뭐나오지???"," "+p_thread.getState().toString());
                    if (Thread.currentThread().getState().equals(State.RUNNABLE)) {
                        try {
                            Thread.sleep(800);
                        } catch (Exception e) {
                        }
                    }
                    if(stop){
                        Log.e("hello", " " + Thread.currentThread().getState());
                        progressStatus =0;
                        handler3.post(new Runnable() {
                            public void run() {
                                //p_gradient.setProgress(progressStatus);
                                //start_time.setText("00:" + String.format("%02d", progressStatus));
                                //end_time.setText("00:" + String.format("%02d", 60 - progressStatus));
                                // 중지 시 아무것도 표시 안함
                                p_gradient.setProgress(0);
                                start_time.setText(null);
                                end_time.setText(null);
                            }
                        });
                        break;
                    }
                }
            }

            progressStatus = 0;
            stop = false;
            work = true;
        }
    }
    private AudioManager.OnAudioFocusChangeListener focusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int focusChange) {
                    switch (focusChange) {
                        case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) :
                            // Lower the volume while ducking.
                            break;
                        case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) :
                            mPlayer.pause();
                            break;
                        case (AudioManager.AUDIOFOCUS_GAIN) :
                            break;
                        default: break;
                    }
                }
            };
    public class AlarmHATT {
        private Context context;
        public AlarmHATT(Context context) {
            this.context=context;
        }
        public void Alarm() {
            AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(MainActivity.this, BroadcastD.class);

            PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

            Calendar calendar = Calendar.getInstance();
            //알람시간 calendar에 set해주기
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 14, 49, 0);
            //알람 예약
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);

            Log.e("알림 시간:", " " +calendar.getTime().toString());
        }
    }
}