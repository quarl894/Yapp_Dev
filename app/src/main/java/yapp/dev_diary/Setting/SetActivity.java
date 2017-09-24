package yapp.dev_diary.Setting;
import java.io.IOException;
import java.util.List;

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


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

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
            default:
                break;
        }
    }


}