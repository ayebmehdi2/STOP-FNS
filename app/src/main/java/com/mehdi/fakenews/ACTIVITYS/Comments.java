package com.mehdi.fakenews.ACTIVITYS;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mehdi.fakenews.ADAPTERS.AdapterComments;
import com.mehdi.fakenews.DATA.Comment;
import com.mehdi.fakenews.DATA.User;
import com.mehdi.fakenews.MySingleton;
import com.mehdi.fakenews.R;
import com.mehdi.fakenews.databinding.LayoutCommentsBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Comments extends AppCompatActivity implements AdapterComments.CommentClick {

    LayoutCommentsBinding binding;

    private DatabaseReference reference;
    private AdapterComments adapterComments;
    private ArrayList<String> comments;
    private String id;
    String pic;
    String name;
    private ChildEventListener childEventListener;

    final private String serverKey = "key=" + "AAAAaigI5AM:APA91bGtEzI614I2-1p6fFyihhblP20SHTYzpwC7hFM0JncJY8P01kJ3SqX6v61XCmiYZt4QzoN6aWtzGAT6jIluhhPnffoatDklol0MYgCyZlat-388vSS9NGqX6lhq-fvqWafEolLi";

    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.layout_comments);

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        id = getIntent().getStringExtra("id");

        if (id == null){
            finish();
            return;
        }

        LinearLayoutManager lm = new LinearLayoutManager(this);
        binding.recComments.setLayoutManager(lm);
        adapterComments = new AdapterComments(this, "COMMENTS");
        binding.recComments.setHasFixedSize(true);
        binding.recComments.setAdapter(adapterComments);

        comments = new ArrayList<>();
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                comments.add(dataSnapshot.getKey());
                adapterComments.swapAdapter(comments);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                comments.remove(dataSnapshot.getKey());
                adapterComments.swapAdapter(comments);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        reference.child("POSTS").child(id).child("COMMENTS").addChildEventListener(childEventListener);

        /*
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.getChildrenCount() > 0)) return;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    comments.add(data.getKey());
                }
                adapterComments.swapAdapter(comments);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        */



        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userid =preferences.getString("uid","");

        reference.child("USERS").child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                if (u == null) return;
                Glide.with(Comments.this).load(u.getPhoto()).into(binding.myImg);
                pic = u.getPhoto();
                name = u.getName();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        binding.postComment.setOnClickListener(v -> {
            String t = binding.addComment.getText().toString();
            if (!(t.length() > 0)) return;

            String time = String.valueOf(System.currentTimeMillis());

            String commentId = id + "-" + new Random().nextInt(99999);

            Comment comment = new Comment(commentId, name, pic, t, time);


            reference.child("POSTS").child(id).child("COMMENTS").child(commentId).setValue(0);

            reference.child("COMMENTS").child(commentId).setValue(comment);

            reference.child("POSTS").child(id).child("COMMENTS");

            String userSendId = id.split("-")[0];
            TOPIC = "/topics/" + userSendId; //topic must match with what the receiver subscribed to
            NOTIFICATION_TITLE = name;
            NOTIFICATION_MESSAGE = t  + " - " + DateFormat.getDateInstance().format(new Date());


            JSONObject notification = new JSONObject();
            JSONObject notifcationBody = new JSONObject();
            try {
                notifcationBody.put("title", NOTIFICATION_TITLE);
                notifcationBody.put("message", NOTIFICATION_MESSAGE);

                notification.put("to", TOPIC);
                notification.put("data", notifcationBody);
            } catch (JSONException e) {
                Log.e(TAG, "onCreate: " + e.getMessage() );
            }
            sendNotification(notification);

            binding.addComment.setText("");


        });






    }

    private void sendNotification(JSONObject notification) {
        String FCM_API = "https://fcm.googleapis.com/fcm/send";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                response -> Log.i(TAG, "onResponse: " + response.toString()),
                error -> {
                    Toast.makeText(Comments.this, "Request error", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "onErrorResponse: Didn't work");
                }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        reference.child("POSTS").child(id).child("COMMENTS").addChildEventListener(childEventListener);
    }


    @Override
    public void profile(String commentId) {

    }

    @Override
    public void likes(String commentId) {

    }

    @Override
    public void replays(String commentId) {
        Intent i = new Intent(this, Replays.class);
        i.putExtra("id", commentId);
        startActivity(i);
    }
}
