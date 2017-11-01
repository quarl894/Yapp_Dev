package yapp.dev_diary.Setting;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

import yapp.dev_diary.MainActivity;
import yapp.dev_diary.R;

/**
 * Created by HANSUNG on 2017-10-29.
 */

public class BroadcastD extends BroadcastReceiver {
    String INTENT_ACTION = Intent.ACTION_BOOT_COMPLETED;
    //알람시간 calendar에 set해주기

    @Override
    public void onReceive(Context context, Intent intent) {//알람 시간이 되었을때 onReceive를 호출함
        //NotificationManager 안드로이드 상태바에 메세지를 던지기위한 서비스 불러오고
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 14, 48, 0);
        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.smile).setTicker("일기쓰라고 알람함").setWhen(System.currentTimeMillis())
                .setNumber(1).setContentTitle("하루소리").setContentText("일기써라")
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingIntent);
        notificationmanager.notify(1, builder.build());

        Log.e("알림 시간22222222:", " " +" " +Long.toString(System.currentTimeMillis()));
    }
}
