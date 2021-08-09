package com.example.fundonotes.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.fundonotes.R;
import com.example.fundonotes.model.Adapter;
import com.example.fundonotes.viewmodel.SharedViewModel;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private SharedViewModel sharedViewModel;
    private Toolbar toolBar;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private ActionBarDrawerToggle toggle;
    private RecyclerView noteList;
    //private ActivityMainBinding binding;
    private String TAG = "MainActivity";

    Adapter adapter;

    @SuppressLint({"RestrictedApi", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);

        noteList = findViewById(R.id.notelist);

        drawerLayout = findViewById(R.id.drawer);
        navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);

        // This is for Hamburger menu icon on toolbar
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolBar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        List<String> titles = new ArrayList<>();
        List<String> content = new ArrayList<>();

        titles.add("first Note");
        content.add("sample content created");

        titles.add("first Note");
        content.add("sample content created");

        adapter = new Adapter(titles,content);
        noteList.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        noteList.setAdapter(adapter);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        sharedViewModel.set_gotoLoginPageStatus(true);
        observeAppNav();
    }

    private void observeAppNav() {
        sharedViewModel.gotoHomePageStatus.observe(MainActivity.this, status -> {
            if (status) {
                toolBar.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment()).commit();
            }
        });

        sharedViewModel.gotoLoginPageStatus.observe(MainActivity.this, status -> {
            if (status) {
                toolBar.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoginFragment()).commit();
            }
        });

        sharedViewModel.gotoRegisterPageStatus.observe(MainActivity.this, status -> {
            if (status) {
                toolBar.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RegisterFragment()).commit();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            default:
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
//        MenuItem menuItem = menu.findItem(R.id.profile_img);
//        View view = MenuItemCompat.getActionView(menuItem);
//
//        CircleImageView profileImage = (CircleImageView) view.findViewById(R.id.toolbar_profile_image);
//          Glide.with(profileImage).load(R.drawable.iconimg).into(profileImage);
//        profileImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this,"clicked on profile image",Toast.LENGTH_SHORT).show();
//            }
//        });
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @SuppressLint("NonConstantResourceId")
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.profile_img:
//                Toast.makeText(MainActivity.this,"clicked on profile image",Toast.LENGTH_SHORT).show();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }


}