package com.example.justdoit.Model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.justdoit.Repository.Repository;
import com.example.justdoit.View.Fragments.TasksFragment;

import java.io.IOException;

public class AlertRecevier extends BroadcastReceiver {
    private static final String TAG = "AlertRecevier";
    private Task mTask;
    private Repository mRepository;

    @Override
    public void onReceive(Context context, Intent intent) {
        mRepository = Repository.getInstance(context);
        String action = intent.getAction();
        if (action != null) {
            Task task = mRepository.getTask();
            task.setIsCompleted(StaticStringUtil.COMPLETED);
            mRepository.updateTask(task, task.getDescription(), true);
        }else {
            Log.d(TAG, "onReceive: add task");
            try {
                mTask = (Task) TasksFragment.deserialize(intent.getByteArrayExtra("task"));
                mRepository.setTask(mTask);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "onReceive: " + mTask);
        NotificationHelper notificationHelper = new NotificationHelper(context, mTask==null?mRepository.getTask():mTask);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
        notificationHelper.getManager().notify(1, nb.build());
    }

}
