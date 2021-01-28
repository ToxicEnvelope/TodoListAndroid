package com.example.cotherapist.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.os.Bundle;

import com.example.cotherapist.Model.AlertRecevier;
import com.example.cotherapist.R;
import com.example.cotherapist.View.Fragments.TasksFragment;

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