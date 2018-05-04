package yapp.dev_diary.Setting;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.ArraySet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import yapp.dev_diary.R;

/**
 * Created by AnGwangHo on 2017-09-20.
 */

public class AlarmActivity extends AppCompatActivity {
    private Switch alarm_switch_onoff;
    private View alarm_include_alarmsetting;
    private Button setting_alarm_btn_cancel;
    private Button setting_alarm_btn_complete;
    private int hour, min;

    private TimePicker item_alarmsetting_timepicker;
    private ToggleButton alarm_day_Monday, alarm_day_Tuesday, alarm_day_Wednesday, alarm_day_Thursday, alarm_day_Friday, alarm_day_Saturday, alarm_day_Sunday;
    private ToggleButton[] week;

    private AlarmManager alarmManager;
    private Intent alarmIntent;
    private PendingIntent alarmPendingIntent;

    private boolean userDaysCheck = false;

    protected final static int ALARM_ID = 10; //alarm 고유 ID

    public AlarmActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_alarm);

        init();

        if (isUserAlarm()) {
            setUserAlarm();
        }

        alarm_switch_onoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                if (isChecked)
                {
                    alarm_include_alarmsetting.setVisibility(View.VISIBLE);
                    setting_alarm_btn_complete.setEnabled(true);
                }
                else
                {
                    alarm_include_alarmsetting.setVisibility(View.GONE);
                    setting_alarm_btn_complete.setEnabled(false);
                    if (isUserAlarm()){
                        showMessage("알람을 해제 하였습니다.");
                        cancelAlarm();
                        alarmDestroy();
                    }
                }
            }
        });

        /**
         * 상단 취소 버튼 click event
         * 설정 화면으로 돌림
         * */
        setting_alarm_btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlarmActivity.this, SetActivity.class);
                startActivity(intent);
                finish();
            }
        });

        /**
         * 상단 완료 버튼 click event
         * 사용자가 선택한 알람에 대해서 완료 시킨다.
         * **/
        setting_alarm_btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean[] check_days = is_findCheckDays();

                if (!userDaysCheck)
                {
                    showMessage("요일을 1개 이상 선택해 주세요!");
                    return;
                }

                if (alarmManager == null || alarmIntent == null)
                    alarmInit();

                //timepicker로 부터 시간 취득
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    hour = item_alarmsetting_timepicker.getHour();
                    min = item_alarmsetting_timepicker.getMinute();
                }
                else {
                    hour = item_alarmsetting_timepicker.getCurrentHour();
                    min = item_alarmsetting_timepicker.getCurrentMinute();
                }
                Calendar calendar = Calendar.getInstance();
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), hour, min, 0);

                alarmIntent.putExtra("week", check_days); //Receiver에서 notify 여부를 결정하기 위해 intent에 담아서 넘김

                //안드로이드 5.1 이후로 반복시간 최소 1분 제한
                alarmPendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, ALARM_ID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmPendingIntent);
                showMessage("알람이 설정 되었습니다.");
                saveSharedPreferences();
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
        week = new ToggleButton[]{alarm_day_Sunday, alarm_day_Monday, alarm_day_Tuesday, alarm_day_Wednesday, alarm_day_Thursday, alarm_day_Friday, alarm_day_Saturday};
        alarmInit();
    }

    /**
     * 사용자 설정 시간, 분, 날짜 SharedPreferences에 저장
     * **/
    private void saveSharedPreferences(){
        SharedPreferences pref = getSharedPreferences("alarm", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("hour", hour);
        editor.putInt("min", min);

        boolean[] check_days = is_findCheckDays();
        String tmpStr = "";
        for (int i = 0; i < check_days.length; i++) {
            tmpStr += check_days[i];
            if (i != check_days.length - 1)
            {
                tmpStr += ",";
            }
        }
        editor.putString("week",tmpStr);

        editor.commit();
    }

    /**
     * 알람 기능 관련 변수 셋팅
     * **/
    private void alarmInit(){
        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmIntent = new Intent(AlarmActivity.this, AlramReceiver.class);
    }

    /**
     * 알람 기능 관련 변수 초기화
     * **/
    private void alarmDestroy(){
        alarmManager = null;
        alarmIntent = null;
        alarmPendingIntent = null;
    }

    /**
     * on_created시점에 사용자가 설정한 알람을 셋팅하기 위한 메소드
     * **/
    private void setUserAlarm() {
        SharedPreferences pref = getSharedPreferences("alarm", MODE_PRIVATE);
        String tmpWeek = pref.getString("week", "");
        int tmpHour = pref.getInt("hour", 0);
        int tmpMin = pref.getInt("min", 0);

        alarm_switch_onoff.setChecked(true);
        alarm_include_alarmsetting.setVisibility(View.VISIBLE);
        setting_alarm_btn_complete.setEnabled(true);

        //요일 별 button에 셋팅
        if (tmpWeek != "") {
            String[] sArray = tmpWeek.split(",");
            boolean ret;
            for (int i = 0; i < sArray.length; i ++) {
                ret = Boolean.valueOf(sArray[i]);
                if(ret) {
                    week[i].setChecked(ret);
                }
            }
        }

        //timepicker에 시간 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            item_alarmsetting_timepicker.setHour(tmpHour);
        }
        else {
            item_alarmsetting_timepicker.setCurrentHour(tmpHour);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            item_alarmsetting_timepicker.setMinute(tmpMin);
        }
        else {
            item_alarmsetting_timepicker.setCurrentMinute(tmpMin);
        }
    }

    /**
     * 사용자가 알람 설정했는지 여부 판단
     * @return true : 알람설정 false : 미설정
     * **/
    private boolean isUserAlarm(){
        boolean ret = false;
        PendingIntent tmpPendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, ALARM_ID, alarmIntent, PendingIntent.FLAG_NO_CREATE);

        if (tmpPendingIntent != null) {
            ret = true;
        }

        return ret;
    }

    /***
     * @return 일요일~토요일까지 배열로 true/false 반환
     * 사용자가 선택한 요일을 판단하여 length 7인 배열로 반환
     * */
    private boolean[] is_findCheckDays(){
        boolean[] days = new boolean[7];
        int len = days.length;

        for(int i = 0; i < len; i++){
            if (week[i].isChecked()){
                days[i] = true;
                userDaysCheck = true;
            }
            else
                days[i] = false;
        }
        return days;
    }

    /**
     * 사전에 변수 null 여부에 대해서 체크 해줘야 함
     * 설정 된 알람 취소
     * **/
    private  void cancelAlarm(){
        PendingIntent tmpPendingIntent = null;
        if (alarmPendingIntent != null) {
            tmpPendingIntent = alarmPendingIntent;
        }
        else
            tmpPendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, ALARM_ID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(tmpPendingIntent);
        tmpPendingIntent.cancel();
    }

    /**
     * @param contents 사용자에게 보여줄 text
     * 사용자에게 Toast message 보여준다
     * **/
    private void showMessage(String contents){
        Toast.makeText(AlarmActivity.this, contents, Toast.LENGTH_SHORT).show();
    }
}
