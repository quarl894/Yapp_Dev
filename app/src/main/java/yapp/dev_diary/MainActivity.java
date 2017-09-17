package yapp.dev_diary;

/**
 * 바로 나오는 화면(sst, 녹음 추가시키면 됨)
 * 최신사진 5개 자동 불러와서 pic_path 배열에 집어넣음.
 */
import android.content.Intent;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import yapp.dev_diary.List.ListDActivity;
import yapp.dev_diary.Setting.SetActivity;
import yapp.dev_diary.Voice.VoiceActivity;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> pic_path = new ArrayList<>();
    public static ArrayList<String> ok_path = new ArrayList<>();
    Button btn_ok, btn_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String getTime = sdf.format(date);
        getImageNameToUri();
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
}
