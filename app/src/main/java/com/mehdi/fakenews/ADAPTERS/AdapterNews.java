package com.mehdi.fakenews.ADAPTERS;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mehdi.fakenews.ACTIVITYS.Replays;
import com.mehdi.fakenews.DATA.Post;
import com.mehdi.fakenews.MySingleton;
import com.mehdi.fakenews.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class AdapterNews extends RecyclerView.Adapter<AdapterNews.holder> {


    private DatabaseReference reference;


    final private String serverKey = "key=" + "AAAAaigI5AM:APA91bGtEzI614I2-1p6fFyihhblP20SHTYzpwC7hFM0JncJY8P01kJ3SqX6v61XCmiYZt4QzoN6aWtzGAT6jIluhhPnffoatDklol0MYgCyZlat-388vSS9NGqX6lhq-fvqWafEolLi";

    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;


    private Context context;

    private ArrayList<String> data = null;
    public void swapAdapter(ArrayList<String> gifts) {
        if (data == gifts) return;
        data = gifts;
        this.notifyDataSetChanged();
    }

    public interface PostClicks{
        void Comment(String idTag);
        void Details(String idTag);
    }

    private final PostClicks clicks;

    public AdapterNews(PostClicks c){
        clicks = c;
    }

    private String id;

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        id =preferences.getString("uid","");
        return new holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        String idPost = data.get(position);
        if (idPost == null) return;
        reference.child("POSTS").child(idPost).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post tag = dataSnapshot.getValue(Post.class);
                if (tag == null) return;


                Glide.with(context).load(tag.getImg()).into(holder.img);

                holder.title.setText(tag.getTitle());

                holder.desc.setText(tag.getDesc());

                holder.name.setText(tag.getAuthName());

                /*


                String date = "";
                long millS = System.currentTimeMillis() - Long.valueOf(tag.getDate());
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


                 */

                reference.child("POSTS").child(idPost).child("FAKE").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String lk = dataSnapshot.getChildrenCount() + " Fake";
                        holder.fake.setText(lk);

                        if (id.equals(tag.getAuthId())){
                            holder.action.setVisibility(View.GONE);
                            return;
                        }

                        if (dataSnapshot.hasChild(id)){
                            holder.action.setVisibility(View.GONE);
                        }else {
                            holder.action.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                reference.child("POSTS").child(idPost).child("REAL").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String lk = dataSnapshot.getChildrenCount() + " Real";
                        holder.real.setText(lk);

                        if (id.equals(tag.getAuthId())){
                            holder.action.setVisibility(View.GONE);
                            return;
                        }

                        if (dataSnapshot.hasChild(id)){
                            holder.action.setVisibility(View.GONE);
                        }else {
                            holder.action.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        if (data == null){
            return 0;
        }
        return data.size();
    }

    class holder extends RecyclerView.ViewHolder {

        ImageView img;

        TextView title, desc, name, fake, real;

        Button fakeA, realA;

        LinearLayout comment, action;

        public holder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);
            name = itemView.findViewById(R.id.name);
            fake = itemView.findViewById(R.id.fake_n);
            real = itemView.findViewById(R.id.real_n);
            fakeA = itemView.findViewById(R.id.fake_a);
            realA = itemView.findViewById(R.id.real_a);
            comment = itemView.findViewById(R.id.comment);
            action = itemView.findViewById(R.id.action);

            TOPIC = "/topics/" + id.split("-")[0]; //topic must match with what the receiver subscribed to
            NOTIFICATION_TITLE = name.getText().toString();
            NOTIFICATION_MESSAGE = "Vote in your news"  + " - " + DateFormat.getDateInstance().format(new Date());

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

            fakeA.setOnClickListener(v -> {
                reference.child("POSTS").child(data.get(getAdapterPosition())).child("FAKE").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(id)){
                            reference.child("POSTS").child(data.get(getAdapterPosition())).child("FAKE").
                                    child(id).removeValue();
                        }else {
                            reference.child("POSTS").child(data.get(getAdapterPosition())).child("FAKE").
                                    child(id).setValue(0);
                        }


                        sendNotification(notification);

                        notifyItemChanged(getAdapterPosition());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            });

            realA.setOnClickListener(v -> {
                reference.child("POSTS").child(data.get(getAdapterPosition())).child("REAL").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(id)){
                            reference.child("POSTS").child(data.get(getAdapterPosition())).child("REAL").
                                    child(id).removeValue();
                        }else {
                            reference.child("POSTS").child(data.get(getAdapterPosition())).child("REAL").
                                    child(id).setValue(0);
                        }
                        sendNotification(notification);
                        notifyItemChanged(getAdapterPosition());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            });
            comment.setOnClickListener(v -> clicks.Comment(data.get(getAdapterPosition())));

            itemView.setOnClickListener(v -> clicks.Details(data.get(getAdapterPosition())));
        }
    }

    private void sendNotification(JSONObject notification) {
        String FCM_API = "https://fcm.googleapis.com/fcm/send";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                response -> Log.i(TAG, "onResponse: " + response.toString()),
                error -> {
                    Toast.makeText(context, "Request error", Toast.LENGTH_LONG).show();
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
        MySingleton.getInstance(context.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

}
