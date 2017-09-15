package yapp.dev_diary;

/**
 * 바로 나오는 화면(sst, 녹음 추가시키면 됨)
 * 최신사진 5개 자동 불러와서 pic_path 배열에 집어넣음.
 */
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorJoiner;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.naver.speech.clientapi.SpeechConfig;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import yapp.dev_diary.List.ListDActivity;
import yapp.dev_diary.Setting.SetActivity;
import yapp.dev_diary.Voice.AudioWriterPCM;
import yapp.dev_diary.Voice.NaverRecognizer;
import yapp.dev_diary.Voice.RecognitionThread;
import yapp.dev_diary.Voice.VoiceActivity;

public class MainActivity extends AppCompatActivity implements MediaRecorder.OnInfoListener {
    ArrayList<String> pic_path = new ArrayList<>();
    public static ArrayList<String> ok_path = new ArrayList<>();
    Button btn_ok, btn_detail;

    // 추가1 implenets MediaRecorder.OnInfoListener
    MediaPlayer mPlayer = null;
    MediaRecorder mRecorder = null;
    String mFilePath;
    ImageButton mBtnRecord;
    ImageButton mBtnStop;
    ImageButton mBtnPlay;
    ImageButton mBtnReset;
    int i;

    // 추가2
    RecognitionThread recognitionThread = null;
    public EditText voice_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String getTime = sdf.format(date);
        getImageNameToUri();

        voice_text = (EditText) findViewById(R.id.voice_text);
        recognitionThread = RecognitionThread.getInstance(this, getApplicationContext());

        btn_detail = (Button) findViewById(R.id.btn_test);
        btn_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ListDActivity.class);
                startActivity(i);
            }
        });
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,SaveActivity.class);
                startActivity(i);
            }
        });
        initToolbar();
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
            Log.e("pic_path_info", " " + pic_path.get(0));
        }
        Log.e("testtest",Integer.toString(ok_path.size()));


        // 추가1
        //i=0;
        //recordFilePathList.add(sdRootPath + "/seohee"+ i +".mp4");
        //버튼 레코드는 원래 녹음 시작
        mBtnRecord = (ImageButton)findViewById(R.id.btnRecord);
        mBtnStop = (ImageButton)findViewById(R.id.btnPause);
        mBtnPlay = (ImageButton)findViewById(R.id.btnPlay);
        mBtnReset = (ImageButton)findViewById(R.id.btnReset);
//        mBtnReset.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                startMerge(recordFilePathList);
//            }
//        });
        mBtnRecord.setVisibility(View.VISIBLE);
        mBtnStop.setVisibility(View.INVISIBLE);
        String  sdRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFilePath = sdRootPath + "/record.mp4";

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


    // 추가1
    public void onBtnRecord() {
        if( mRecorder != null ) {
            mRecorder.release();
            mRecorder = null;
        }
        mRecorder = new MediaRecorder();
        mRecorder.setOutputFile(mFilePath);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        mRecorder.setMaxDuration(10 * 1000);
        mRecorder.setMaxFileSize(10 * 1000 * 1000);
        mRecorder.setOnInfoListener(this);


        try {
            mRecorder.prepare();
        } catch(IOException e) {

            Log.d("tag", "Record Prepare error");
        }
        mRecorder.start();

        // 버튼 활성/비활성 설정
        mBtnRecord.setEnabled(false);
        mBtnStop.setEnabled(true);
        mBtnPlay.setEnabled(false);
        mBtnRecord.setVisibility(View.INVISIBLE);
        mBtnStop.setVisibility(View.VISIBLE);
    }

//    public void startMerge(ArrayList<String> recordFilePathList) {
//        Movie[] inMovies = new Movie[recordFilePathList.size()];
//        try {
//            for (int a = 0; a < recordFilePathList.size(); a++) {
//                inMovies[a] = MovieCreator.build(recordFilePathList.get(a));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        List<Track> audioTracks = new LinkedList<Track>();
//        for (Movie m : inMovies)
//        {
//            for (Track t : m.getTracks())
//            {
//                if (t.getHandler().equals("soun"))
//                {
//                    audioTracks.add(t);
//                }
//            }
//        }
//        Movie output = new Movie();
//        if (audioTracks.size() > 0)
//        {try
//            {output.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
//            }
//            catch (IOException e)
//            {e.printStackTrace();
//            }
//        }
//        Container out = new DefaultMp4Builder().build(output);
//        FileChannel fc = null;
//        try
//        {fc = new FileOutputStream(new File(mFilePath)).getChannel();
//        }
//        catch (FileNotFoundException e)
//        {
//            e.printStackTrace();
//        }
//        try
//        {out.writeContainer(fc);
//        }
//        catch (IOException e)
//        {e.printStackTrace();
//        }
//        try
//        {fc.close();
//        }
//        catch (IOException e)
//        {e.printStackTrace();
//        }
//    }

    public void onBtnStop() {
        mRecorder.stop();
        mRecorder.release();

        // 버튼 활성/비활성 설정
        mBtnRecord.setEnabled(true);
        mBtnStop.setEnabled(false);
        mBtnPlay.setEnabled(true);
        mBtnRecord.setVisibility(View.VISIBLE);
        mBtnStop.setVisibility(View.INVISIBLE);
        //recordFilePathList.add(sdRootPath + "/seohee"+ i +".mp4");
        //recordFilePathList.set(i,sdRootPath + "/seohee"+ i +".mp4");
        //i++;
    }



    public void onBtnPlay() {
        if( mPlayer != null ) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        mPlayer = new MediaPlayer();

        try {
            mPlayer.setDataSource(mFilePath);
            mPlayer.prepare();
        } catch(IOException e) {
            Log.d("tag", "Audio Play error");
            return;
        }
        mPlayer.start();
    }

    public void onBtnOk(){
        Intent intent=new Intent(MainActivity.this,InputActivity.class);
        startActivity(intent);
    }

    public void onClick(View v) {
        switch( v.getId() ) {
            case R.id.btnRecord :
                onBtnRecord();
                recognitionThread.start();
                break;
            case R.id.btnPause :
                onBtnStop();
                recognitionThread.interrupt();
                break;
            case R.id.btnPlay :
                onBtnPlay();
                break;
            case R.id.btnReset :
                break;
//            case R.id.ok:
//                onBtnOk();
//                break;

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
}
