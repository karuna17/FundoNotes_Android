package com.example.fundonotes.model;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fundonotes.R;
import com.example.fundonotes.view.MainActivity;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class AuthService {
    private static final String TAG = "User Profile Status: ";
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    UserDetails detailsOfUser;
    DocumentReference documentReference;
    String userID;
    FirebaseUser currentUser = mAuth.getCurrentUser();

    String uName, uEmail, uPassword;


    public void loginUser(User user, AuthListener listner) {
        mAuth.signInWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    listner.onAuthComplete(true, "Logged In Successfully");
                } else {
                    listner.onAuthComplete(false, "Failed to logged in");
                }
            }
        });
    }

    public void resetPassword(String email, AuthListener listener) {
        mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                listener.onAuthComplete(true, "Reset Link Sent To Your Email");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onAuthComplete(false, "Error...! Reset Link Is Not Sent" + e.getMessage());
            }
        });
    }

    public void registerUser(User user, AuthListener listner) {
        String uName = user.getUser_name();
        String uEmail = user.getEmail();
        String uPassword = user.getPassword();
        user = new User(uName, uEmail, uPassword);
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    listner.onAuthComplete(true, "User Created Successfully");
                    userID = mAuth.getCurrentUser().getUid();
                    documentReference = fstore.collection("users").document(userID);
                    Map<String, Object> userDB = new HashMap<>();
                    userDB.put("fName", uName);
                    userDB.put("u_email", uEmail);
                    userDB.put("u_pass", uPassword);
                    documentReference.set(userDB).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "onSuccess: User profile is created for " + userID);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: " + e.toString());
                        }
                    });

                } else {
                    listner.onAuthComplete(false, "Failed To Registered");
                }
            }
        });
    }

    public void getDataFromFirebase(User userinfo) {
        userID = mAuth.getCurrentUser().getUid();
        documentReference = fstore.collection("users").document(userID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    uName = task.getResult().getString("fName");
                    uEmail = task.getResult().getString("u_email");
                    uPassword = task.getResult().getString("u_pass");
                    new User(uName, uEmail, uPassword);
                }
            }
        });
    }

    public void fbLogin(AccessToken accessToken, FaceBookAuthListener listener) {
        Log.d("Token Status: ", "handleFacebookAccessToken = " + accessToken);
        AuthCredential credintial = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credintial).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d("Facebook Status: ", "sign in with credintials: successful");

                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (currentUser != null) {
                        String name = currentUser.getDisplayName();
                        String email = currentUser.getEmail();
                        Uri url = currentUser.getPhotoUrl();
                        detailsOfUser = new UserDetails(name, email, url);
                    }
                    listener.onCompleteFBAuth(detailsOfUser, true);
                } else {
                    Log.d("Facebook Status: ", "sign in with credintials: failed", task.getException());
                    listener.onCompleteFBAuth(detailsOfUser, false);
                }
            }
        });
    }

    public void signOut() {
        mAuth.signOut();
    }

    public void fbSignout() {
        LoginManager.getInstance().logOut();
    }

}