package com.mehdi.fakenews.ACTIVITYS;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mehdi.fakenews.DATA.User;
import com.mehdi.fakenews.R;
import com.mehdi.fakenews.databinding.LoginBinding;

import java.util.Collections;
import java.util.List;

public class ActivityAuth extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth auth;

    FirebaseAuth.AuthStateListener authStateListener;


    LoginBinding binding;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.login);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        auth = FirebaseAuth.getInstance();



        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    startActivity(new Intent(ActivityAuth.this, MainActivity.class));
                }else {
                    binding.login.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            signIn();
                        }
                    });
                }
            }
        };

    }

    public void signIn(){
        Log.d("SignUpActivity", "user null");
        List<AuthUI.IdpConfig> providers = Collections.singletonList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(providers)
                        //.setTheme(R.style.LoginTheme)
                        .build(),
                100);
    }


    public void setupUser(final FirebaseUser user){


        String uid = user.getUid();
        SharedPreferences.Editor preferences = PreferenceManager.getDefaultSharedPreferences(ActivityAuth.this).edit();
        preferences.putString("uid", uid);
        preferences.apply();

        FirebaseMessaging.getInstance().subscribeToTopic(uid).addOnSuccessListener(aVoid -> Toast.makeText(getApplicationContext(),"Subscribe Success",Toast.LENGTH_LONG).show());


        reference.child("USERS").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(uid)){
                    startActivity(new Intent(ActivityAuth.this, MainActivity.class));
                }else {

                    reference.child("USERS").child(uid).setValue(new User(user.getDisplayName(), uid, user.getPhotoUrl().toString()));

                    startActivity(new Intent(ActivityAuth.this, MainActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null){
                    startActivity(new Intent(ActivityAuth.this, ActivityAuth.class));
                    return;
                }
                setupUser(user);
            } else {
                Toast.makeText(this, "Sign Filed", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (authStateListener != null){
            auth.removeAuthStateListener(authStateListener);
        }

    }
}