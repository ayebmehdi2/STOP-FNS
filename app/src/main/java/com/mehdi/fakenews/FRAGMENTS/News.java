package com.mehdi.fakenews.FRAGMENTS;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mehdi.fakenews.ADAPTERS.AdapterNews;
import com.mehdi.fakenews.R;
import com.mehdi.fakenews.databinding.RecycleItemsBinding;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class News extends Fragment implements AdapterNews.PostClicks {

    RecycleItemsBinding binding;

    FirebaseDatabase database;
    DatabaseReference reference;

    ChildEventListener childEventListener;

    AdapterNews news;

    String id;

    ArrayList<String> idsPost;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.recycle_items, container, false);


        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        id =preferences.getString("uid","");


        news = new AdapterNews(this);
        binding.rec.setHasFixedSize(true);
        binding.rec.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rec.setAdapter(news);

        binding.add.setOnClickListener(v -> clicks.add());

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
        reference.child("POSTS").addChildEventListener(childEventListener);



        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        reference.child("POSTS").removeEventListener(childEventListener);
    }


    private clicksNews clicks;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        clicks = (clicksNews) context;
    }

    @Override
    public void Comment(String idTag) {
        clicks.comment(idTag);
    }

    @Override
    public void Details(String idTag) {
        clicks.detail(idTag);
    }

    public interface clicksNews{
        void add();
        void comment(String idTag);
        void detail(String idTag);
    }
}
