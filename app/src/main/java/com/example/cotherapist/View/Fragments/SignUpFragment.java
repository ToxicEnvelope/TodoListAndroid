package com.example.cotherapist.View.Fragments;

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
import android.widget.Toast;

import com.example.cotherapist.Model.Patient.Patient;
import com.example.cotherapist.Model.Therapist.Therapist;
import com.example.cotherapist.Model.ViewModelEnum;
import com.example.cotherapist.Model.ViewModelFactory;
import com.example.cotherapist.R;
import com.example.cotherapist.ViewModel.SignUpViewModel;

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
        final EditText fullNameEt = view.findViewById(R.id.full_name_et);
        final EditText emailEt = view.findViewById(R.id.email_et);
        final EditText passwordEt = view.findViewById(R.id.password_et);
        final EditText confirmPasswordEt = view.findViewById(R.id.confirm_password_et);
        final Button loginBtn = view.findViewById(R.id.login_btn);
        final Button signUpBtn=view.findViewById(R.id.sign_up_btn);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener!=null){
                    Log.d(TAG, "onClick: "+"signUp");
                    String fullName = fullNameEt.getText().toString();
                    String email = emailEt.getText().toString();
                    String password = passwordEt.getText().toString();
                    String confirmPassword = confirmPasswordEt.getText().toString();
                    if (password.equals(confirmPassword)) {
                        Therapist therapist = new Therapist(fullName, email, password);
                        Log.d(TAG, "Therapist object "+ therapist +"\n[DATA] " +  therapist.getFullName() + ", " + therapist.getEmail() + ", "+ therapist.getPassword());
                        mViewModel.signUpUser(therapist);
                    } else {
                        Toast.makeText(view.getContext(), "Password doesn't match!", Toast.LENGTH_LONG).show();
                    }
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
                Toast.makeText(getContext(), "Email already in use!", Toast.LENGTH_LONG).show();
            }
        };

        mViewModel.getSignUpSucceed().observe(this,mSignInSucceedObserver);
        mViewModel.getSignUpFailed().observe(this,mSignInFailedObserver);
    }
}