package com.example.cotherapist.Model;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.cotherapist.ViewModel.AdapterViewModel;
import com.example.cotherapist.ViewModel.LoginViewModel;
import com.example.cotherapist.ViewModel.SignUpViewModel;
import com.example.cotherapist.ViewModel.TasksViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private Context mContext;

    private ViewModelEnum mViewModelEnum;

    public ViewModelFactory(Context context, ViewModelEnum viewModelEnum) {
        this.mContext = context;
        this.mViewModelEnum = viewModelEnum;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        T objToReturn = null;

        switch (mViewModelEnum) {
            case Login:
                if (modelClass.isAssignableFrom(LoginViewModel.class)) {
                    objToReturn = (T) new LoginViewModel(mContext);
                }
                break;
            case SignUp:
                if (modelClass.isAssignableFrom(SignUpViewModel.class)) {
                    objToReturn = (T) new SignUpViewModel(mContext);

                }
                break;
            case Task:
                if (modelClass.isAssignableFrom(TasksViewModel.class)) {
                    objToReturn = (T) new TasksViewModel(mContext);
                }
                break;
            case Adapter:
                if (modelClass.isAssignableFrom(AdapterViewModel.class)) {
                    objToReturn = (T) new AdapterViewModel();
                }
                break;


//            case Welcome:
//                if (modelClass.isAssignableFrom(WelcomeViewModel.class)) {
//                    objToReturn = (T) new WelcomeViewModel(mContext);
//                }
//                break;
        }
        return objToReturn;
    }
}