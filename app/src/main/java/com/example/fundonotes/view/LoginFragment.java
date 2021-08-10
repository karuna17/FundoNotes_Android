package com.example.fundonotes.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fundonotes.R;
import com.example.fundonotes.databinding.FragmentLoginBinding;
import com.example.fundonotes.model.AuthService;
import com.example.fundonotes.model.FBStatus;
import com.example.fundonotes.model.User;
import com.example.fundonotes.viewmodel.LoginViewModel;
import com.example.fundonotes.viewmodel.LoginViewModelFactory;
import com.example.fundonotes.viewmodel.SharedViewModel;
import com.example.fundonotes.viewmodel.SharedViewModelFactory;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;

import java.util.Arrays;

public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";

    CallbackManager callbackManager;
    FragmentLoginBinding binding;
    private LoginViewModel loginViewModel;
    private SharedViewModel sharedViewModel;

    public LoginFragment() {
    }

    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        callbackManager = CallbackManager.Factory.create();
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory(new AuthService())).get(LoginViewModel.class);
        sharedViewModel = new ViewModelProvider(getActivity(), new SharedViewModelFactory()).get(SharedViewModel.class);
        binding.setLoginViewModel(loginViewModel);

        // call All the methods
        login();
        creatingNewAcc();
        resetingNewPassword();
        facebookLogin();
        fbLogOut();
        stayLoggedIn();
    }

    /*  AuthListener listener = new AuthListener() {
        @Override
        public void onAuthComplete(boolean status, String message) {
            if (status) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
//                MainFragment mainFrag = new MainFragment();
//                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.fragment_container, mainFrag).addToBackStack(null).commit();
//                sharedViewModel.set_gotoHomePageStatus(status);
                binding.progressBar.setVisibility(View.GONE);
            } else {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.GONE);
            }
        }
    }; */

    private void login() {
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("FragmentLiveDataObserve")
            @Override
            public void onClick(View v) {
                String email = binding.inputEmail.getText().toString().trim();
                String password = binding.inputPassword.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    binding.inputEmail.setError("Email is required");
                    Log.d("Result", "onClick: " + email);
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
                //Authenticate the user
                User user = new User(email, password);
                loginViewModel.loginToFundoNotes(user);
                loginViewModel.userLoginStatus.observe(LoginFragment.this, status -> {

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

    private void creatingNewAcc() {
        binding.createNewAcc.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("FragmentLiveDataObserve")
            @Override
            public void onClick(View v) {
                sharedViewModel.set_gotoRegisterPageStatus(true);
            }
        });
    }

    private void resetingNewPassword() {
        binding.forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialouge = new AlertDialog.Builder(v.getContext());
                passwordResetDialouge.setTitle("Reset Password? ");
                passwordResetDialouge.setMessage("Enter Your Email To Reset The Password Link");
                passwordResetDialouge.setView(resetMail);
                passwordResetDialouge.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @SuppressLint("FragmentLiveDataObserve")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mail = resetMail.getText().toString();
                        //reset password
                        loginViewModel.resettingPasswordToFundoNotes(mail);
                        loginViewModel.resetPasswordStatus.observe(LoginFragment.this, status -> {
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
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                passwordResetDialouge.create().show();
            }
        });
    }

    private void facebookLogin() {

        binding.faceBookLoginButton.setFragment(this);
        binding.faceBookLoginButton.setPermissions(Arrays.asList("email", "public_profile"));
        binding.faceBookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Facebook Status: ", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("Facebook Status: ", "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("Facebook Status: ", "facebook:onError", error);
            }
        });

    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        // UserDetails details = new UserDetails();
        loginViewModel.fbLoginToFundoNotes(accessToken);
        loginViewModel.userFBLoginStatus.observe(LoginFragment.this, new Observer<FBStatus>() {

            @Override
            public void onChanged(FBStatus fbStatus) {
                Log.d(TAG, "onClick: dummy" + fbStatus.getFbUserStatus());

                if (fbStatus.getFbUserStatus()) {
                    Log.d(TAG, "onClick: dummy1" + fbStatus.getFbUserStatus());
                    Toast.makeText(getContext(), (CharSequence) fbStatus.getFbUserDetails(), Toast.LENGTH_SHORT).show();
                    sharedViewModel.set_gotoHomePageStatus(true);
                    binding.progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getContext(), (CharSequence) fbStatus.getFbUserDetails(), Toast.LENGTH_SHORT).show();
                    binding.progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void fbLogOut() {
        // for FaceBook SignOut
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    loginViewModel.fbLogoutFundoNotes();
                    sharedViewModel.set_gotoLoginPageStatus(true);
                    binding.progressBar.setVisibility(View.GONE);
                }
            }
        };
    }

    private void stayLoggedIn() {

        SharedPreferences preferences = getContext().getSharedPreferences("checkbox", Context.MODE_PRIVATE);
        String checkbox = preferences.getString("checkbox", "");
        if (checkbox.equals("true")) {
            sharedViewModel.set_gotoHomePageStatus(true);
        } else if (checkbox.equals("false")) {
            Toast.makeText(getContext(), "Please Login", Toast.LENGTH_SHORT).show();
        }

        binding.rememberLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    SharedPreferences preferences = getContext().getSharedPreferences("checkbox", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "true");
                    editor.apply();
                    Toast.makeText(getContext(), "Checked", Toast.LENGTH_SHORT).show();
                } else if (!buttonView.isChecked()) {
                    SharedPreferences preferences = getContext().getSharedPreferences("checkbox", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "false");
                    editor.apply();
                    Toast.makeText(getContext(), "Unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
