package com.mehdi.fakenews.FRAGMENTS;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mehdi.fakenews.ADAPTERS.AdapterNews;
import com.mehdi.fakenews.DATA.User;
import com.mehdi.fakenews.R;
import com.mehdi.fakenews.databinding.ProfileLayoutBinding;

import java.util.ArrayList;


public class Profile extends Fragment implements AdapterNews.PostClicks {


    private DatabaseReference reference;
    private ChildEventListener childEventListener;

    private AdapterNews news;

    private String id;

    private ArrayList<String> idsPost;

    private ProfileLayoutBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.profile_layout, container, false);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        id =preferences.getString("uid","");
        reference.child("USERS").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                if (u == null) return;;
                binding.name.setText(u.getName());
                Glide.with(getContext()).load(u.getPhoto()).into(binding.pic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                editor.putBoolean("notfication", false);
                editor.putString("uid", null);
                editor.apply();

                FirebaseMessaging.getInstance().unsubscribeFromTopic(id).addOnSuccessListener(aVoid -> Toast.makeText(getContext().getApplicationContext(),"Unsubscribe Success",Toast.LENGTH_LONG).show());


                clicksProfil.goOut();
            }
        });
        news = new AdapterNews(this);
        binding.myPost.setHasFixedSize(true);
        binding.myPost.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.myPost.setAdapter(news);
        idsPost = new ArrayList<>();
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                idsPost.add(dataSnapshot.getKey());
                news.swapAdapter(idsPost);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reference.child("USERS").child(id).child("MY_POSTS").addChildEventListener(childEventListener);

        return binding.getRoot();
    }




    @Override
    public void onStop() {
        super.onStop();
        reference.child("USERS").child(id).child("MY_POSTS").removeEventListener(childEventListener);
    }



    private clicksProfil clicksProfil;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        clicksProfil = (clicksProfil) context;
    }

    @Override
    public void Comment(String idTag) {
        clicksProfil.commentPr(idTag);
    }

    @Override
    public void Details(String idTag) {

    }

    public interface clicksProfil{
        void goOut();
        void commentPr(String id);
    }


}
