package yapp.dev_diary;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.database.Cursor;
import android.database.CursorJoiner;
import android.media.ExifInterface;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import yapp.dev_diary.List.ListDActivity;
import yapp.dev_diary.Setting.SetActivity;
import yapp.dev_diary.Voice.VoiceActivity;

public class MainActivity extends AppCompatActivity
        implements MediaRecorder.OnInfoListener {
    public final static int STATE_PREV = 0;     //녹음 시작 전
    public final static int STATE_RECORDING = 1;    //녹음 중
    public final static int STATE_PAUSE = 2;        // 일시 정지 중
    String outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/output.mp4";
    MediaPlayer mPlayer = null;

    MediaRecorder mRecorder = null;

    ArrayList<String> outputFileList; // 임시 파일 리스트
    String mFilePath =  null;
    ImageButton mBtnRecord; //재생&일시정지

    ImageButton mBtnStop; //저장 버튼

    ImageButton mBtnPlay; //재생 버튼

    ImageButton mBtnReset;// 초기화 버튼
    Button mBtnSave;

    int count=0;

    private int state = STATE_PREV;

    ArrayList<String> pic_path = new ArrayList<>();
    public static ArrayList<String> ok_path = new ArrayList<>();
    Button btn_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        outputFileList = new ArrayList<String>();
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String getTime = sdf.format(date);
        getImageNameToUri();


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
    public void onBtnRecord() {
        Log.d("seoheeing", "Record Prepare error");


        count +=1;
        mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myrecording";
        String nowFile = mFilePath  + count + ".mp4";
        outputFileList.add(nowFile);

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setAudioEncodingBitRate(320000);
        mRecorder.setAudioSamplingRate(44100);
        mRecorder.setOutputFile(nowFile);

        try {

            mRecorder.prepare();
            mRecorder.start();

        } catch(IOException e) {

            Log.d("tag", "Record Prepare error");

        }
        state = STATE_RECORDING; //녹음 중 상태로 바꿈




        // 버튼 활성/비활성 설정

        mBtnRecord.setEnabled(false);

        mBtnStop.setEnabled(true);

       // mBtnPlay.setEnabled(false);
        mBtnRecord.setVisibility(View.INVISIBLE);
        mBtnStop.setVisibility(View.VISIBLE);
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




//    public void onBtnOk(){
//        Intent intent=new Intent(MainActivity.this,InputActivity.class);
//        startActivity(intent);
//    }
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
                    mPlayer.setDataSource(outputFile);
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
        outputFileList.clear();

        for(int i=1; i<=count ; i++){
            removeDir(Integer.toString(count));
        }
        count =0;
    }
    //파일 & 폴더 삭제

    public static void removeDir(String countNum) {

        String mRootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myrecording" + countNum + ".mp4";
        File file = new File(mRootPath);
        File[] childFileList = file.listFiles();
        for(File childFile : childFileList)
        {
            if(childFile.isDirectory()) {
                removeDir(childFile.getAbsolutePath());    //하위 디렉토리
            }
            else {
                childFile.delete();    //하위 파일
            }
        }
        file.delete();    //root 삭제

    }
    public void onBtnSave(){
        Log.d("seoheeing", "Record Prepare error");

//        count=0;
//        startMerge(outputFileList);
        if (state == STATE_PREV) {     //
            //녹음 시작안한 상태에서 정지 버튼 누르기
            return;
        } else if (state == STATE_PAUSE) {
            //일시 정지 상태일 때,

        } else {
            try {
                mPlayer.stop();

            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;

        }
        count = 0;
        try {
            startMerge(outputFileList);
            Intent i = new Intent(MainActivity.this,SaveActivity.class);
            startActivity(i);
        } catch (IOException e) {
            e.printStackTrace();

        }

    }
    public void startMerge(ArrayList<String> outputFileList)throws IOException
    {
        Movie[] inMovies = new Movie[outputFileList.size()];
        try
        {
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
            fc = new FileOutputStream(new File(outputFile)).getChannel();
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


//    public void append(List<String> list) throws IOException {
//
//
//        Movie[] inMovies;
//        inMovies = new Movie[list.size()];
//        for (int i = 0; i < list.size(); i++) {
//            inMovies[i] = MovieCreator.build(list.get(i));
//        }
//
//        List<Track> videoTracks = new LinkedList<Track>();
//        List<Track> audioTracks = new LinkedList<Track>();
//
//        for (Movie m : inMovies) {
//            for (Track t : m.getTracks()) {
//                if (t.getHandler().equals("soun")) {
//                    audioTracks.add(t);
//                }
//                if (t.getHandler().equals("vide")) {
//                    videoTracks.add(t);
//                }
//            }
//        }
//
//        Movie result = new Movie();
//
//        if (audioTracks.size() > 0) {
//            result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
//        }
//        if (videoTracks.size() > 0) {
//            result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
//        }
//
//        Container out = new DefaultMp4Builder().build(result);
//
//        RandomAccessFile ram = new RandomAccessFile(String.format(Environment.getExternalStorageDirectory().getAbsolutePath()  + "/output.mp4"), "rw");
//        //최종적으로 output.mp4 라는 파일로 다 합친 파일을 저장하게 됨
//        FileChannel fc = ram.getChannel();
//        out.writeContainer(fc);
//        ram.close();
//        fc.close();
//    }




    public void onClick(View v) {

        switch( v.getId() ) {

            case R.id.btnRecord :

                onBtnRecord();

                break;

            case R.id.btnPause :

                onBtnStop();

                break;


            case R.id.btnReset :
                onBtnReset();

                break;

            case R.id.btnPlay:
                onBtnPlay();
                break;

            case R.id.btnSave:
                onBtnSave();
                break;
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
}
