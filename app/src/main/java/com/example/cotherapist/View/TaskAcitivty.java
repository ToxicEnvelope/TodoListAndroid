package com.example.justdoit.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;

import com.example.justdoit.Model.AlertRecevier;
import com.example.justdoit.R;
import com.example.justdoit.View.Fragments.LoginFragment;
import com.example.justdoit.View.Fragments.TasksFragment;

public class TaskAcitivty extends AppCompatActivity {

    private static final String TASK_FRAG = "task_fragment";
    AlertRecevier alertRecevier;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_acitivty);
         alertRecevier= new AlertRecevier();

        IntentFilter filter = new IntentFilter("toggle_switch");
        registerReceiver(alertRecevier, filter);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.task_activity_layout, TasksFragment.newInstance(), TASK_FRAG)
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(alertRecevier);
    }
}