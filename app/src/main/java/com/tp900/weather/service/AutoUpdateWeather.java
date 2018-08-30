package com.tp900.weather.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.tp900.weather.R;

public class AutoUpdateWeather extends Service {
    public AutoUpdateWeather() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ShowNotification();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int hours = 60000;//每分钟
        long triggerAtTime = SystemClock.elapsedRealtime()+hours;
        Intent intent1 = new Intent(this,AutoUpdateWeather.class);
        PendingIntent pendingIntent= PendingIntent.getService(this,0,intent1,0);
        manager.cancel(pendingIntent);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }
    private void ShowNotification(){
        Log.d("ss", "ShowNotification: ");
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("测试通知")
                .setContentText("测试通知测试通知")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher_round)).build();
        notificationManager.notify(1,notification);
    }
}
