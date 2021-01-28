package com.example.cotherapist.Model;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.example.cotherapist.R;


public class NotificationHelper extends ContextWrapper {
    public static final String channelID = "channelID";
    public static final String channelName = "Channel Name";
    private static final String TAG = "NotificationHelper";
    private NotificationManager mManager;
    private Task mTask;
    private RemoteViews notificationLayoutExpanded;
    private RemoteViews notificationLayoutCollapsed;

    public NotificationHelper(Context base, Task task) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
        this.mTask =task;
        Log.d(TAG, "NotificationHelper: "+mTask.getIsCompleted());
        notificationLayoutCollapsed = new RemoteViews(getPackageName(), R.layout.notif_collapsed);
        notificationLayoutExpanded=new RemoteViews(base.getPackageName(),  R.layout.notif_expand);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    public NotificationCompat.Builder getChannelNotification() {
        Intent clickIntent = new Intent(this, AlertRecevier.class);
        clickIntent.setAction("toggle_switch");
        clickIntent.putExtra("clicked","clicked");
        PendingIntent clickPendingIntent = PendingIntent.getBroadcast(this,
                0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if(mTask.getIsCompleted().equals(StaticStringUtil.COMPLETED)){
            Log.d(TAG, "getChannelNotification: complete");
            notificationLayoutExpanded.setImageViewResource(R.id.toggle_btn,R.drawable.ic_completed);
        }else{
            Log.d(TAG, "getChannelNotification: not complete");
            notificationLayoutExpanded.setImageViewResource(R.id.toggle_btn,R.drawable.ic_not_completed);

        }
        notificationLayoutExpanded.setTextViewText(R.id.content, mTask.getDescription());
        notificationLayoutExpanded.setTextViewText(R.id.task_status, mTask.getIsCompleted());
        notificationLayoutExpanded.setOnClickPendingIntent(R.id.toggle_btn, clickPendingIntent);

        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setSmallIcon(R.drawable.ic_baseline_work_24)
                .setCustomContentView(notificationLayoutCollapsed)
                .setCustomBigContentView(notificationLayoutExpanded);
    }
}

