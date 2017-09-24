package yapp.dev_diary;
import android.content.Intent;
import android.database.Cursor;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import yapp.dev_diary.List.ListDActivity;
import yapp.dev_diary.Setting.SetActivity;
import yapp.dev_diary.Voice.VoiceActivity;
import yapp.dev_diary.utils.AudioWriterPCM;

public class MainActivity extends AppCompatActivity implements MediaRecorder.OnInfoListener {
    public final static int STATE_PREV = 0;     //녹음 시작 전
    public final static int STATE_RECORDING = 1;    //녹음 중
    public final static int STATE_PAUSE = 2;        // 일시 정지 중
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String CLIENT_ID = "s2xquX9eCQsCD0xOxV0E";
    ArrayList<String> list_stt;
    String outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/output.mp4";
    String outputFile2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/sttput.mp4";
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

    private RecognitionHandler handler;
    private NaverRecognizer naverRecognizer;
    private TextView txtResult;
    private String mResult;

    private Handler handler2;

    private AudioWriterPCM writer;
    int count=0;
    private int state = STATE_PREV;
    ArrayList<String> pic_path = new ArrayList<>();
    public static ArrayList<String> ok_path = new ArrayList<>();

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

                list_stt.add(mResult);
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

        outputSttList = new ArrayList<String>();
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        final String getTime = sdf.format(date);
        getImageNameToUri();

        handler2 = new Handler();
        list_stt = new ArrayList<String>();
        initToolbar();
        //i=0;
        //recordFilePathList.add(sdRootPath + "/seohee"+ i +".mp4");

        //버튼 레코드는 원래 녹음 시작
        mBtnRecord = (ImageButton)findViewById(R.id.btnRecord);
        mBtnStop = (ImageButton)findViewById(R.id.btnPause);
        mBtnPlay = (ImageButton)findViewById(R.id.btnPlay);
        mBtnReset = (ImageButton)findViewById(R.id.btnReset);
        mBtnSave = (Button)findViewById(R.id.btnSave);
        mBtnRecord.setVisibility(View.VISIBLE);
        mBtnStop.setVisibility(View.INVISIBLE);

        txtResult = (TextView) findViewById(R.id.txt_result);
        handler = new RecognitionHandler(this);
        naverRecognizer = new NaverRecognizer(this, handler, CLIENT_ID);

        try {
            //사진 찍은 날짜 정보 가져오기
            ExifInterface exif = new ExifInterface(pic_path.get(0));
            ExifInterface exif2 = new ExifInterface(pic_path.get(1));
            ExifInterface exif3 = new ExifInterface(pic_path.get(2));
            ExifInterface exif4 = new ExifInterface(pic_path.get(3));
            ExifInterface exif5 = new ExifInterface(pic_path.get(4));
            //Uri에서 이미지 이름을 얻어온다.?
            if(showExif(exif).equals(getTime)) ok_path.add(pic_path.get(0));
            if(showExif(exif2).equals(getTime)) ok_path.add(pic_path.get(1));
            if(showExif(exif3).equals(getTime)) ok_path.add(pic_path.get(2));
            if(showExif(exif4).equals(getTime)) ok_path.add(pic_path.get(3));
            if(showExif(exif5).equals(getTime)) ok_path.add(pic_path.get(4));
        }catch (Exception e) {
            e.printStackTrace();
            Log.e("pic_path_info", " " + pic_path.get(0));
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

    public void onBtnStop() {//일시정지
        Log.d("seoheeing", "Record Prepare error");
        mRecorder.stop();
        mRecorder.reset();
        mRecorder.release();
        mRecorder = null;
        state = STATE_PAUSE;

        mBtnRecord.setEnabled(true);

        mBtnStop.setEnabled(false);
        mBtnRecord.setVisibility(View.VISIBLE);
        mBtnStop.setVisibility(View.INVISIBLE);

    }

    public void onBtnPlay(){
        Log.d("seoheeing", "Record Prepare error");
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
        } catch (Exception e) {
            Log.d("SampleAudioRecorder", "Audio play failed.");
        }

    }
    public void onBtnReset(){
        outputSttList.clear();
        count =0;
        txtResult.setText("");
        list_stt.clear();
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
                if(!naverRecognizer.getSpeechRecognizer().isRunning()) {
                    // Start button is pushed when SpeechRecognizer's state is inactive.
                    // Run SpeechRecongizer by calling recognize().
                    mResult = "";
                    txtResult.setText("Connecting...");
                    naverRecognizer.recognize();
                } else {
                    Log.d(TAG, "stop and wait Final Result");
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
                }
                mBtnRecord.setEnabled(false);

                mBtnStop.setEnabled(true);

                // mBtnPlay.setEnabled(false);
                mBtnRecord.setVisibility(View.INVISIBLE);
                mBtnStop.setVisibility(View.VISIBLE);
                break;
            case R.id.btnPause :
                if(!naverRecognizer.getSpeechRecognizer().isRunning()) {
                    // Start button is pushed when SpeechRecognizer's state is inactive.
                    // Run SpeechRecongizer by calling recognize().
                    mResult = "";
                    txtResult.setText("Connecting...");
                    naverRecognizer.recognize();
                } else {
                    Log.d(TAG, "stop and wait Final Result");
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
                }
                mBtnRecord.setEnabled(true);

                mBtnStop.setEnabled(false);
                mBtnRecord.setVisibility(View.VISIBLE);
                mBtnStop.setVisibility(View.INVISIBLE);
                break;
            case R.id.btnReset :
                onBtnReset();
                break;
            case R.id.btnPlay:
                onBtnPlay();
                break;
            case R.id.btnSave:
                try {
                    startMerge2(outputSttList);
//                    onBtnSave();
                    Intent i = new Intent(MainActivity.this, SaveActivity.class);
                    startActivity(i);
                } catch (Exception e) {
                    Log.e(TAG, "Exception while creating tmp file", e);
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

                onBtnStop();

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
}
