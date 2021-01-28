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

import com.example.justdoit.Model.User;
import com.example.justdoit.Model.ViewModelEnum;
import com.example.justdoit.Model.ViewModelFactory;
import com.example.justdoit.R;
import com.example.justdoit.ViewModel.SignUpViewModel;

public class SignUpFragment extends Fragment {

    private static final String TAG = "SignUpFragment";
    private SignUpViewModel mViewModel;

    private Observer<Boolean> mSignInSucceedObserver;
    private Observer<Boolean> mSignInFailedObserver;


    public interface SignUpListener {
        void onGoLogin();
        void onSignUp();
    }

    private SignUpListener listener;

    public SignUpFragment() {}

    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            listener=(SignUpListener)context;
        }
        catch (ClassCastException ex) {
            throw new ClassCastException("The activity must implement SignUpFragment Listener!");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.sign_up_fragment, container, false);
        final EditText nameEt = view.findViewById(R.id.name_et);
        final EditText emailEt = view.findViewById(R.id.email_et);
        final EditText passwordEt = view.findViewById(R.id.password_et);
        final Button loginBtn = view.findViewById(R.id.login_btn);
        final Button signUpBtn=view.findViewById(R.id.sign_up_btn);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener!=null){
                    Log.d(TAG, "onClick: "+"signUp");
                    String name=nameEt.getText().toString();
                    String email=emailEt.getText().toString();
                    String password = passwordEt.getText().toString();
                    User user=new User(name,email,password);
                    mViewModel.signUpUser(user);
                }
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener!=null){
                    listener.onGoLogin();
                }
            }
        });
        return view;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext(),
                ViewModelEnum.SignUp)).get(SignUpViewModel.class);

        Log.d(TAG, "onCreate: "+mViewModel);

        mSignInSucceedObserver=new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                String authKey=mViewModel.getAuthKey();
                if(listener!=null&&authKey!=null){
                    listener.onSignUp();
                }
            }
        };
        mSignInFailedObserver=new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Log.d(TAG, "failed: "+"error");
            }
        };

        mViewModel.getSignUpSucceed().observe(this,mSignInSucceedObserver);
        mViewModel.getSignUpFailed().observe(this,mSignInFailedObserver);
    }
}