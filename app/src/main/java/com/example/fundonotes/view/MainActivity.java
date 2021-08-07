package com.example.fundonotes.view;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import com.example.fundonotes.R;
import com.example.fundonotes.databinding.ActivityMainBinding;
import com.example.fundonotes.viewmodel.SharedViewModel;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private SharedViewModel sharedViewModel;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private ActivityMainBinding binding;
    private String TAG = "MainActivity";

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(binding.includeToolbar.toolBar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);

//        final ActionBar actionBar = getSupportActionBar();
//        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
//        actionBar.setHomeAsUpIndicator(R.drawable.ic_navigation);
//        actionBar.setDisplayHomeAsUpEnabled(true);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        sharedViewModel.set_gotoLoginPageStatus(true);
        observeAppNav();
    }


    private void observeAppNav() {
        sharedViewModel.gotoHomePageStatus.observe(MainActivity.this, status -> {
            if (status) {
                binding.includeToolbar.appBarLayout.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment()).commit();
            }
        });

        sharedViewModel.gotoLoginPageStatus.observe(MainActivity.this, status -> {
            if (status) {
                binding.includeToolbar.appBarLayout.setVisibility(View.GONE);
                //binding.fragmentContainer.setVisibility(View.GONE);
                // binding.navView.setVisibility(View.GONE);

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoginFragment()).commit();

            }
        });

        sharedViewModel.gotoRegisterPageStatus.observe(MainActivity.this, status -> {
            if (status) {
                binding.includeToolbar.appBarLayout.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RegisterFragment()).commit();
            }
        });
    }

}