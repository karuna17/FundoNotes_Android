package com.example.fundonotes.model;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthService {
    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();

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
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    listner.onAuthComplete(true, "User Created Successfully");
                } else {
                    listner.onAuthComplete(false, "Failed To Registered");
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
                    listener.onCompleteFBAuth();
                    Log.d("Facebook Status: ", "sign in with credintials: successful");
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                } else {
                    Log.d("Facebook Status: ", "sign in with credintials: failed", task.getException());
                }
            }
        });
    }

//    public void signOut(AuthListener listener) {
//     FirebaseAuth.getInstance().signOut();
//    }

}
