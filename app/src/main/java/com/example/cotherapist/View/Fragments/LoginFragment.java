package com.example.justdoit.View.Fragments;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.justdoit.Model.ViewModelEnum;
import com.example.justdoit.Model.ViewModelFactory;
import com.example.justdoit.R;
import com.example.justdoit.ViewModel.LoginViewModel;

public class LoginFragment extends Fragment {


    private final String TAG="LoginFragment";
    private LoginViewModel mViewModel;
    private Observer<Boolean> mOnLoginSucceed;
    private Observer<Boolean> mOnLoginFailed;



    public interface LoginListener {
        void onLogin();
        void onBackSignUp();
    }

    private LoginListener listener;


    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (LoginListener) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException("The activity must implement Login Listener!");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);
        final EditText emailEt = view.findViewById(R.id.email_et);
        final EditText passwordEt = view.findViewById(R.id.password_et);
        final Button loginBtn = view.findViewById(R.id.login_btn);
        final Button signUpBtn = view.findViewById(R.id.sign_up_btn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    String email = emailEt.getText().toString();
                    String password = passwordEt.getText().toString();
                    mViewModel.LoginUser(email, password);
                }
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener!=null){
                    listener.onBackSignUp();
                }
            }
        });

        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext(),
                ViewModelEnum.Login)).get(LoginViewModel.class);

        mOnLoginSucceed=new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                String authKey=mViewModel.getAuthKey();
                if(listener!=null&&authKey!=null){
                    listener.onLogin();
                }
            }
        };
        mOnLoginFailed=new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Log.d(TAG, "onChanged: "+"error login");
            }
        };

        mViewModel.getOnLoginSuccess().observe(this,mOnLoginSucceed);
        mViewModel.getOnLoginFailed().observe(this,mOnLoginFailed);

    }



}