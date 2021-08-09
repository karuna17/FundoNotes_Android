package com.example.fundonotes.view;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.fundonotes.R;
import com.example.fundonotes.model.AuthService;
import com.example.fundonotes.viewmodel.LoginViewModel;
import com.example.fundonotes.viewmodel.LoginViewModelFactory;
import com.example.fundonotes.viewmodel.RegisterViewModel;
import com.example.fundonotes.viewmodel.RegisterViewModelFactory;
import com.example.fundonotes.viewmodel.SharedViewModel;
import com.example.fundonotes.viewmodel.SharedViewModelFactory;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainFragment extends Fragment {
    private Button logoutButton;
    private LoginViewModel loginViewModel;
    private SharedViewModel sharedViewModel;

    public MainFragment() {
    }

    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        logoutButton = v.findViewById(R.id.logout_button);
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory(new AuthService())).get(LoginViewModel.class);
        sharedViewModel = new ViewModelProvider(getActivity(), new SharedViewModelFactory()).get(SharedViewModel.class);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull  View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logout();
    }

    private void logout() {
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginViewModel.normalLogout();
                sharedViewModel.set_gotoLoginPageStatus(true);
            }
        });

    }
}