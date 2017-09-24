package yapp.dev_diary.Setting;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

import yapp.dev_diary.R;

/**
 * Created by AnGwangHo on 2017-09-20.
 */

public class AlarmActivity extends AppCompatActivity {
    private Switch alarm_switch_onoff;
    private View alarm_include_alarmsetting;
    private Button setting_alarm_btn_cancel;
    private Button setting_alarm_btn_complete;

    private TimePicker item_alarmsetting_timepicker;
    private ToggleButton alarm_day_Monday, alarm_day_Tuesday, alarm_day_Wednesday, alarm_day_Thursday, alarm_day_Friday, alarm_day_Saturday, alarm_day_Sunday;

    public AlarmActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_alarm);

        init();

        alarm_switch_onoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                if (isChecked)
                {
                    alarm_include_alarmsetting.setVisibility(View.VISIBLE);
                }
                else
                {
                    alarm_include_alarmsetting.setVisibility(View.GONE);
                }
            }
        });

        setting_alarm_btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlarmActivity.this, SetActivity.class);
                startActivity(intent);
                finish();
            }
        });

        setting_alarm_btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour, min;
                ArrayList<Integer> check_days;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    hour = item_alarmsetting_timepicker.getHour();
                    min = item_alarmsetting_timepicker.getMinute();
                }
                else {
                    hour = item_alarmsetting_timepicker.getCurrentHour();
                    min = item_alarmsetting_timepicker.getCurrentMinute();
                }

                check_days = is_findCheckDays();
                String masage = "시간 : "+hour+"분 : "+min+" 선택한 날짜들 : "+check_days.toString();
                Toast.makeText(AlarmActivity.this, masage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init(){
        alarm_switch_onoff = (Switch) findViewById(R.id.alarm_switch_onoff);
        alarm_include_alarmsetting = (View) findViewById(R.id.alarm_include_alarmsetting);
        setting_alarm_btn_cancel = (Button) findViewById(R.id.setting_alarm_btn_cancel);
        setting_alarm_btn_complete = (Button) findViewById(R.id.setting_alarm_btn_complete);
        item_alarmsetting_timepicker = (TimePicker) findViewById(R.id.item_alarmsetting_timepicker);
        alarm_day_Monday = (ToggleButton) findViewById(R.id.alarm_day_Monday);
        alarm_day_Tuesday = (ToggleButton) findViewById(R.id.alarm_day_Tuesday);
        alarm_day_Wednesday = (ToggleButton) findViewById(R.id.alarm_day_Wednesday);
        alarm_day_Thursday = (ToggleButton) findViewById(R.id.alarm_day_Thursday);
        alarm_day_Friday = (ToggleButton) findViewById(R.id.alarm_day_Friday);
        alarm_day_Saturday = (ToggleButton) findViewById(R.id.alarm_day_Saturday);
        alarm_day_Sunday = (ToggleButton) findViewById(R.id.alarm_day_Sunday);
    }

    private ArrayList<Integer> is_findCheckDays(){
        ArrayList<Integer> days = new ArrayList<Integer>();
        ToggleButton[] weeks = {alarm_day_Monday, alarm_day_Tuesday, alarm_day_Wednesday, alarm_day_Thursday, alarm_day_Friday, alarm_day_Saturday, alarm_day_Sunday};

        for(int i = 0; i < weeks.length; i++)
        {
            if (weeks[i].isChecked())
                days.add(i);
        }

        return days;
    }
}
