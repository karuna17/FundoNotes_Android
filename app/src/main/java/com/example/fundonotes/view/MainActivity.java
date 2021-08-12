package com.example.fundonotes.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.fundonotes.R;
import com.example.fundonotes.model.Adapter;
import com.example.fundonotes.model.AuthService;
import com.example.fundonotes.model.User;
import com.example.fundonotes.viewmodel.LoginViewModel;
import com.example.fundonotes.viewmodel.LoginViewModelFactory;
import com.example.fundonotes.viewmodel.SharedViewModel;
import com.example.fundonotes.viewmodel.SharedViewModelFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private SharedViewModel sharedViewModel;
    private LoginViewModel loginViewModel;

    private Toolbar toolBar;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private ActionBarDrawerToggle toggle;
    private RecyclerView noteList;

    private ImageView profileImage;
    private String PROFILE_IMAGE_URL = null;
    private static final int TAKE_IMAGE_CODE = 10001;

    //private ActivityMainBinding binding;
    private String TAG = "MainActivity";
    final Context context = this;
    Adapter adapter;

    String userID;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DocumentReference documentReference;
    FirebaseFirestore fstore;


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

        profileImage = (ImageView) findViewById(R.id.user_picture);

        loginViewModel = new ViewModelProvider(MainActivity.this, new LoginViewModelFactory(new AuthService())).get(LoginViewModel.class);
//        sharedViewModel = new ViewModelProvider(this, new SharedViewModelFactory()).get(SharedViewModel.class);

        List<String> titles = new ArrayList<>();
        List<String> content = new ArrayList<>();

        titles.add("first Note");
        content.add("sample content created");

        titles.add("first Note");
        content.add("sample content created");

        adapter = new Adapter(titles, content);
        noteList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.profile_img);
        View view = MenuItemCompat.getActionView(menuItem);

        CircleImageView profileImage = view.findViewById(R.id.toolbar_profile_image);
//        Glide
//                .with(this)
//                .load("https://images.unsplash.com/photo-1478070531059-3db579c041d5?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=750&q=80")
//                .into(profileImage);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.userprofile_layout);
                dialog.setTitle("User Profile");

                // set the custom dialog components - text, image and button
                ImageView image = dialog.findViewById(R.id.user_picture);
                TextView name = dialog.findViewById(R.id.user_name);
                TextView email = dialog.findViewById(R.id.user_email);

                mAuth = FirebaseAuth.getInstance();
                currentUser = mAuth.getCurrentUser();
                fstore = FirebaseFirestore.getInstance();

                userID = currentUser.getUid();
                documentReference = fstore.collection("users").document(userID);
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()) {
                            image.setImageResource(R.drawable.iconimg);
                            String profile_name = task.getResult().getString("fName");
                            String profile_email = task.getResult().getString("u_email");
                            name.setText(profile_name);
                            email.setText(profile_email);
                        }
                    }
                });

                Button dialogButton = (Button) dialog.findViewById(R.id.profile_logout);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loginViewModel.normalLogout();
                        sharedViewModel.set_gotoLoginPageStatus(true);
                    }
                });

                // if button is clicked, close the custom dialog
                ImageButton closeButton  = (ImageButton) dialog.findViewById(R.id.btncancel);
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        return super.

                onCreateOptionsMenu(menu);
    }


    public void handleImageClick(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, TAKE_IMAGE_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_IMAGE_CODE){
            switch (resultCode){
                case RESULT_OK:
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    profileImage.setImageBitmap(bitmap);
                    imageUpload(bitmap);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void imageUpload(Bitmap bitmap) {
        ByteArrayOutputStream baos =  new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        userID = mAuth.getCurrentUser().getUid();
        StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("profileImages")
                .child(userID + ".jpeg");
        reference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       getDownloadUrl(reference);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {
                Log.e(TAG, "onFailure: ", e.getCause());
            }
        });
    }
    private void getDownloadUrl(StorageReference ref){
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG, "onSuccess: " + uri);
                setUserProfileUrl(uri);
            }
        });
    }

    private void setUserProfileUrl(Uri uri){
        currentUser = mAuth.getCurrentUser();
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
        currentUser.updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(MainActivity.this,"profile image successfully added....", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {
                Toast.makeText(MainActivity.this,"profile image failed....", Toast.LENGTH_SHORT).show();
            }
        });
    }
}