package com.example.cotherapist.ViewModel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cotherapist.Repository.Repository;

public class LoginViewModel extends ViewModel {



    private Repository mRepository;

    private MutableLiveData<Boolean> mOnLoginSuccess;
    private MutableLiveData<Boolean> mOnLoginFailed;

    public LoginViewModel(Context context) {
        mRepository=Repository.getInstance(context);
    }

    public MutableLiveData<Boolean> getOnLoginSuccess() {
        if(mOnLoginSuccess==null){
            mOnLoginSuccess=new MutableLiveData<>();
            attachLoginListener();
        }
        return mOnLoginSuccess;
    }

    private void attachLoginListener() {
        mRepository.setLoginListener(new Repository.RepositoryLoginInterface() {
            @Override
            public void onUserLoginSucceed() {
                mOnLoginSuccess.setValue(true);
            }

            @Override
            public void onUserLoginFailed() {
                mOnLoginSuccess.setValue(false);
            }
        });
    }

    public MutableLiveData<Boolean> getOnLoginFailed() {
        if(mOnLoginFailed==null){
            mOnLoginFailed=new MutableLiveData<>();
            attachLoginListener();
        }
        return mOnLoginFailed;
    }

    public void LoginUser(String email, String password) {
        mRepository.loginUser(email,password);
    }

    public String getAuthKey() {
       return mRepository.getAuthKey();
    }
}