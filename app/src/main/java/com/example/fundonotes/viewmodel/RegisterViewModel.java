package com.example.fundonotes.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fundonotes.model.AuthService;
import com.example.fundonotes.model.Status;
import com.example.fundonotes.model.User;

public class RegisterViewModel extends ViewModel {

    private AuthService authService;
    private MutableLiveData<Status> _userRegisterStatus = new MutableLiveData<>();
    public LiveData<Status> userRegisterStatus = (LiveData<Status>) _userRegisterStatus;

    public RegisterViewModel(AuthService authService) {
        this.authService = authService;
    }

    public void registerToFundoNotes(User user) {
        authService.registerUser(user, ((status, message) -> _userRegisterStatus.setValue(new Status(status, message))));
    }
}
