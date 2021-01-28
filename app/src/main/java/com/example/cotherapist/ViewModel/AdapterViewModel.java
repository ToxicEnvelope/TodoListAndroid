package com.example.cotherapist.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AdapterViewModel extends ViewModel {

    MutableLiveData<String> mutableLiveData=new MutableLiveData<>();

    public void setTextView(String s){
        mutableLiveData.setValue(s);
    }

    public MutableLiveData<String> getTextView(){

        return mutableLiveData;
    }
}
