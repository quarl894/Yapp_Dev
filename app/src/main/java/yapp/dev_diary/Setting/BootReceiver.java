package yapp.dev_diary.Setting;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

/**
 * @author AnGwangHo
 * 재부팅 시 alarm 재시작하는 리시버 Class
 * **/
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals("android.intent.action.BOOT_COMPLETED")) {
            SharedPreferences pref = context.getSharedPreferences("alarm", context.MODE_PRIVATE);
            AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent myIntent = new Intent(context, AlramReceiver.class);
            int hour = pref.getInt("hour", 0);
            int min = pref.getInt("min", 0);
            String tmpWeek = pref.getString("week", "");

            boolean[] ret = new boolean[7];

            if (tmpWeek != "") {
                String[] sArray = tmpWeek.split(",");

                for (int i = 0; i < sArray.length; i ++) {
                    ret[i] = Boolean.valueOf(sArray[i]);
                }
            }
            myIntent.putExtra("week", ret);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, AlarmActivity.ALARM_ID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar calendar = Calendar.getInstance();
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), hour, min, 0);

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }
}
