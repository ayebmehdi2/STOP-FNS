package com.mehdi.fakenews.ACTIVITYS;

import android.annotation.SuppressLint;
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
import com.mehdi.fakenews.databinding.LayoutReplayBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Replays extends AppCompatActivity implements AdapterComments.CommentClick {

    LayoutReplayBinding binding;

    private DatabaseReference reference;
    private AdapterComments adapterReplays;
    private ArrayList<String> replays;
    private ChildEventListener childEventListener;
    private String id;
    String pic;
    String name;

    final private String serverKey = "key=" + "AAAAaigI5AM:APA91bGtEzI614I2-1p6fFyihhblP20SHTYzpwC7hFM0JncJY8P01kJ3SqX6v61XCmiYZt4QzoN6aWtzGAT6jIluhhPnffoatDklol0MYgCyZlat-388vSS9NGqX6lhq-fvqWafEolLi";

    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.layout_replay);

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

        reference.child("COMMENTS").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Comment comment = dataSnapshot.getValue(Comment.class);
                if (comment == null) return;


                String date = "";
                long millS = System.currentTimeMillis() - Long.valueOf(comment.getTime());
                long min = TimeUnit.MILLISECONDS.toMinutes(millS);
                if (min < 60){
                    date = min + " min";
                }else if ((min > 60) && (min < 1440)){
                    long hour = TimeUnit.MILLISECONDS.toHours(millS);
                    date = hour + " hour";
                }else {
                    @SuppressLint("SimpleDateFormat")
                    DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm");
                    Date result = new Date(millS);
                    date = simple.format(result);
                }

                binding.namePerson.setText(comment.getName());
                Glide.with(Replays.this).load(comment.getPic()).into(binding.imgPerson);
                binding.time.setText(date);
                binding.comment.setText(comment.getComment());



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        LinearLayoutManager lm = new LinearLayoutManager(this);
        binding.recReplay.setLayoutManager(lm);
        adapterReplays = new AdapterComments(this, "REPLAYS");
        binding.recReplay.setHasFixedSize(true);
        binding.recReplay.setAdapter(adapterReplays);

        replays = new ArrayList<>();
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                replays.add(dataSnapshot.getKey());
                adapterReplays.swapAdapter(replays);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                replays.remove(dataSnapshot.getKey());
                adapterReplays.swapAdapter(replays);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reference.child("COMMENTS").child(id).child("REPLAYS").addChildEventListener(childEventListener);


        /*
        replays = new ArrayList<>();
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.getChildrenCount() > 0)) return;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String is = data.getValue(String.class);
                    if (is == null) return;
                    replays.add(data.getKey());
                }
                adapterReplays.swapAdapter(replays);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        reference.child("COMMENTS").child(id).child("REPLAYS").addListenerForSingleValueEvent(valueEventListener);

         */


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userid =preferences.getString("uid","");
        reference.child("USERS").child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                if (u == null) return;;

                Glide.with(Replays.this).load(u.getPhoto()).into(binding.myImg);

                name = u.getName();
                pic = u.getPhoto();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        binding.postComment.setOnClickListener(v -> {
            String rep = binding.addComment.getText().toString();
            if (!(rep.length() > 0)) return;

            String time = String.valueOf(System.currentTimeMillis());

            String replayId = id + "-" + new Random().nextInt(99999);

            Comment comment = new Comment(replayId, name, pic, rep, time);


            reference.child("COMMENTS").child(id).child("REPLAYS").child(replayId).setValue(0);

            reference.child("REPLAYS").child(replayId).setValue(comment);

            binding.addComment.setText("");

            boolean b =preferences.getBoolean("not",true);
            if (!b || userid.equals(id.split("-")[0])){
                return;
            }

            String userSendId = id.split("-")[0];
            TOPIC = "/topics/" + userSendId; //topic must match with what the receiver subscribed to
            NOTIFICATION_TITLE = name;
            NOTIFICATION_MESSAGE = rep  + " - " + DateFormat.getDateInstance().format(new Date());

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

        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        reference.child("COMMENTS").child(id).child("REPLAYS").removeEventListener(childEventListener);
    }

    @Override
    public void profile(String repId) {

    }

    @Override
    public void likes(String repId) {
    }

    @Override
    public void replays(String repId) {
        // no action
    }


    private void sendNotification(JSONObject notification) {
        String FCM_API = "https://fcm.googleapis.com/fcm/send";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                response -> Log.i(TAG, "onResponse: " + response.toString()),
                error -> {
                    Toast.makeText(Replays.this, "Request error", Toast.LENGTH_LONG).show();
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
}
