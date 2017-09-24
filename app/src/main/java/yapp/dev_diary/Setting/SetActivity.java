package yapp.dev_diary.Setting;

import java.io.IOException;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import yapp.dev_diary.DB.MyDBHelper;
import yapp.dev_diary.DB.MyItem;
import yapp.dev_diary.R;
import yapp.dev_diary.Setting.SunUtil;

public class SetActivity extends Activity
{
    MyDBHelper myDBHelper;

public class SetActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
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
    private void onClickEvent(View view) {
        switch (view.getId()) {
            case R.id.setting_layout_alarm:
                Intent intent1 = new Intent(this, AlarmActivity.class);
                startActivity(intent1);
                break;
            case R.id.setting_layout_lock:
                Intent intent2 = new Intent(this, LockActivity.class);
                startActivity(intent2);
                break;
            case R.id.setting_layout_allbackup:
                Toast.makeText(this, "전체백업은 백업 되면은...", Toast.LENGTH_SHORT).show();
        myDBHelper = new MyDBHelper(this);
    }


    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.btnStartRec://삽입
                Log.d("Insert: ", "Inserting ..");
                myDBHelper.insert(new MyItem("12","22","내용", 1,3,"헤헤",3,2));
                myDBHelper.insert(new MyItem("33","44","내용", 1,3,"헤헤",3,2));
                myDBHelper.insert(new MyItem("55","66/","내용", 1,3,"헤헤",3,2));
            case R.id.btnStartPlay:// 모두출력
//                Log.d("Reading: ", "Reading all contacts..");
//                List<MyItem> myItemList = myDBHelper.allSelect();
//
//                for(MyItem mi : myItemList){
//                    String log = "첫번째 결과는: "+mi.getP_path()+" , 두번째 결과는 : " + mi.getR_path();
//                    Log.d("Name: ", log);
//                }
                //위에 코드는 모든 디비 출력하는 코드

                //myDBHelper.update(myDBHelper.oneSelect(7).setTitle("헤헤 아닌데?"));//?> 수정 이상해..
                break;
            case R.id.btnStopPlay://삭제
                String  loglog = myDBHelper.oneSelect(7).getTitle();
                Log.d("하나만: ", loglog);
                break;
            case R.id.setting_layout_allclear:
                Toast.makeText(this, "전체초기화는 DB다 되면...", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}