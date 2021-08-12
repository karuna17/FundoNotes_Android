package com.example.fundonotes.view;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fundonotes.R;
import com.example.fundonotes.databinding.FragmentRegisterBinding;
import com.example.fundonotes.model.AuthListener;
import com.example.fundonotes.model.AuthService;
import com.example.fundonotes.model.Status;
import com.example.fundonotes.model.User;
import com.example.fundonotes.viewmodel.RegisterViewModel;
import com.example.fundonotes.viewmodel.RegisterViewModelFactory;
import com.example.fundonotes.viewmodel.SharedViewModel;
import com.example.fundonotes.viewmodel.SharedViewModelFactory;


public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private RegisterViewModel registerViewModel;
    private SharedViewModel sharedViewModel;

    public RegisterFragment() {
    }

    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_register, container, false);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false);
        registerViewModel = new ViewModelProvider(this, new RegisterViewModelFactory(new AuthService())).get(RegisterViewModel.class);
        sharedViewModel = new ViewModelProvider(getActivity(), new SharedViewModelFactory()).get(SharedViewModel.class);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.setRegisterViewModel(registerViewModel);
        //methods call
        registeringNewUser();
        loginIfAlreadyHaveAcc();
    }

    private void registeringNewUser() {
        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("FragmentLiveDataObserve")
            @Override
            public void onClick(View v) {
                String email = binding.inputEmail.getText().toString().trim();
                String password = binding.inputPassword.getText().toString().trim();
                String fullName = binding.inputName.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    binding.inputEmail.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    binding.inputPassword.setError("Password is required");
                    return;
                }
                if (password.length() < 6) {
                    binding.inputPassword.setError("Password must be greater than 6 characters");
                    return;
                }
                binding.progressBar.setVisibility(View.VISIBLE);
                //register the user in firebase
                User user = new User(fullName,email, password);
                registerViewModel.registerToFundoNotes(user);
                registerViewModel.userRegisterStatus.observe(RegisterFragment.this, status -> {
                    if (status.getStatus()) {
                        Toast.makeText(getContext(), status.getMessage(), Toast.LENGTH_SHORT).show();
                        sharedViewModel.set_gotoHomePageStatus(true);
                        binding.progressBar.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(getContext(), status.getMessage(), Toast.LENGTH_SHORT).show();
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private void loginIfAlreadyHaveAcc() {
        binding.alreadyAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedViewModel.set_gotoLoginPageStatus(true);
            }
        });
    }

}