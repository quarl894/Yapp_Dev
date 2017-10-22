package yapp.dev_diary.Setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

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
                Intent i3 = new Intent(this, Pactivity.class);
                startActivity(i3);
                //Toast.makeText(this, "전체백업은 백업 되면은...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.setting_layout_allclear :
                Toast.makeText(this, "전체초기화는 DB다 되면...", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}