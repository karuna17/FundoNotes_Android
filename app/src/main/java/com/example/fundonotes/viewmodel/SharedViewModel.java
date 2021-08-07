package com.example.fundonotes.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fundonotes.model.AuthListener;
import com.example.fundonotes.model.AuthService;
import com.example.fundonotes.model.User;

public class SharedViewModel extends ViewModel {

    private MutableLiveData<Boolean> _gotoRegisterPageStatus = new MutableLiveData<Boolean>();
    public LiveData<Boolean> gotoRegisterPageStatus = (LiveData<Boolean>) _gotoRegisterPageStatus;

    private MutableLiveData<Boolean> _gotoLoginPageStatus = new MutableLiveData<Boolean>();
    public LiveData<Boolean> gotoLoginPageStatus = (LiveData<Boolean>) _gotoLoginPageStatus;

    private MutableLiveData<Boolean> _gotoHomePageStatus = new MutableLiveData<Boolean>();
    public LiveData<Boolean> gotoHomePageStatus = (LiveData<Boolean>) _gotoHomePageStatus;

    public void set_gotoRegisterPageStatus(Boolean status) {
        this._gotoRegisterPageStatus.setValue(status);
    }

    public void set_gotoLoginPageStatus(Boolean status) {
        this._gotoLoginPageStatus.setValue(status);
    }

    public void set_gotoHomePageStatus(Boolean status) {
        Log.d("TAG", "set_gotoHomePageStatus: "+status);
        this._gotoHomePageStatus.setValue(status);
    }

}

