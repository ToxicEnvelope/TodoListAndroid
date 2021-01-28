package com.example.cotherapist.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.cotherapist.R;
import com.example.cotherapist.View.Fragments.LoginFragment;
import com.example.cotherapist.View.Fragments.SignUpFragment;

public class MainActivity extends AppCompatActivity implements SignUpFragment.SignUpListener, LoginFragment.LoginListener {
    private final String LOGIN_FRAG = "login_details_fragment";
    private final String SIGN_UP_FRAG = "sign_up_fragment";
    private final String TASKS_FRAG= "tasks";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.root_layout, LoginFragment.newInstance(), SIGN_UP_FRAG)
                .commit();

    }


    @Override
    public void onLogin() {
        startTaskActivity();
    }

    @Override
    public void onBackSignUp() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.root_layout, SignUpFragment.newInstance(), SIGN_UP_FRAG)
                .commit();
    }

    @Override
    public void onGoLogin() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.root_layout, LoginFragment.newInstance(), LOGIN_FRAG)
                .commit();
    }

    @Override
    public void onSignUp() {
      startTaskActivity();
    }

    private void startTaskActivity() {
        Intent intent = new Intent(MainActivity.this, TaskAcitivty.class);
        startActivity(intent);
        finish();
    }
}