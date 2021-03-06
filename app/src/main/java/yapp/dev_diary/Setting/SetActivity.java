package yapp.dev_diary.Setting;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;

import yapp.dev_diary.DB.MyDBHelper;
import yapp.dev_diary.DB.MyItem;
import yapp.dev_diary.R;

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
    private void onClickEvent(View view){
        switch (view.getId()) {
            case R.id.setting_layout_alarm :
                Intent intent1 = new Intent(this, AlarmActivity.class);
                startActivity(intent1);
                break;
            case R.id.setting_layout_lock :
                Intent intent2 = new Intent(this, LockActivity.class);
                startActivity(intent2);
                break;
            case R.id.setting_layout_allbackup :
                Toast.makeText(this, "추후 Update 예정입니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.setting_layout_allclear :
                deleteDialog();
                break;
        }
    }

    /**
     * 전체삭제시 보여주는 다이얼로그 메소드
     * */
    private void deleteDialog(){
        String title, contents;

        title = "초기화";
        contents = "전체 초기화 하시겠습니까?";

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(title)
                .setMessage(contents)
                .setCancelable(false)
                .setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MyDBHelper myDBHelper = new MyDBHelper(SetActivity.this);
                                List<MyItem> list = myDBHelper.allSelect();

                                for (int num = 0; num < list.size(); num++)
                                {
                                    myDBHelper.delete(list.get(num).get_Index());
                                }
                                Toast.makeText(SetActivity.this, "전체 초기화 완료", Toast.LENGTH_SHORT).show();
                            }
                        })
                .setNegativeButton("아니요",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

        //생성
        AlertDialog alertDialog = alertDialogBuilder.create();

        //활성화
        alertDialog.show();
    }
}