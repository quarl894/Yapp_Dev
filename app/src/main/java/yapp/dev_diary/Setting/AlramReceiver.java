package yapp.dev_diary.Setting;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import java.util.Calendar;

import yapp.dev_diary.MainActivity;
import yapp.dev_diary.R;

/**
 * @author AnGwangHo
 * 사용자에게 알람을 발생시키는 Class
 * **/
public class AlramReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //service 호출 시 무엇을 할 지에 대한 정의

        boolean[] week = intent.getBooleanArrayExtra("week"); //선택 된 날짜에 대해서 intent로 담아둠

        final Calendar calendar = Calendar.getInstance();

        //선택한 날짜가 있으면 실행(배열은 0부터 시작하여 -1)
        if (week[calendar.get(Calendar.DAY_OF_WEEK)-1]) {

            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            //notiy touch 시 이동 시킬 Activity 설정
            PendingIntent mPendingIntent =
                    PendingIntent.getActivity(context,
                            0, new Intent(context, MainActivity.class),
                            PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder mBuilder = new Notification.Builder(context);
            mBuilder.setSmallIcon(R.drawable.app_round);
            mBuilder.setContentTitle(context.getResources().getString(R.string.alarm_title));
            mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
            mBuilder.setContentText(context.getResources().getString(R.string.alarm_contents));
            mBuilder.setDefaults(Notification.DEFAULT_ALL);
            mBuilder.setAutoCancel(true);// 사용자 noti touch 시 사라짐
            mBuilder.setContentIntent(mPendingIntent); //사용자 touch 시 이동 시키는 메소드
            nm.notify(0, mBuilder.build()); // 알람 발생
        }
    }
}
