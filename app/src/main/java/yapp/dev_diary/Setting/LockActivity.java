package yapp.dev_diary.Setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;

import yapp.dev_diary.R;

/**
 * Created by Administrator on 2017-09-17.
 */

public class LockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_lock);

        Switch switch_lock = (Switch) findViewById(R.id.setting_lock_onoff_switch);

        switch_lock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                if (isChecked)
                { //체크상태
                    Intent intent = new Intent(LockActivity.this, LockInputActivity.class);
                    startActivity(intent);
                }
                else
                { //해제상태
                    //해제하면 지우기
                }
            }
        });
    }
}
